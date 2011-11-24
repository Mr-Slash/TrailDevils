package ch.hsr.traildevil.application;

import static ch.hsr.traildevil.util.network.HttpHandler.CONNECTION_MODE_CLOSE;
import static ch.hsr.traildevil.util.network.HttpHandler.TYPE_JSON;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.zip.GZIPInputStream;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.util.ByteArrayBuffer;

import android.os.AsyncTask;
import android.util.Log;
import ch.hsr.traildevil.R;
import ch.hsr.traildevil.domain.Trail;
import ch.hsr.traildevil.persistence.TrailProvider;
import ch.hsr.traildevil.presentation.TraillistActivity;
import ch.hsr.traildevil.util.Constants;
import ch.hsr.traildevil.util.network.HttpHandler;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

/**
 * This class is used to synchronize asynchronously the local db with the remote one. 
 * 
 * @author Sandro
 */
public class SynchronizeTask extends AsyncTask<String, Integer, Long> {
	
	private static final String TAG_PREFIX = SynchronizeTask.class.getSimpleName() + ": ";
	
	private final TraillistActivity activity;
	private TrailProvider trailProvider;
	private final Lock mutex = new ReentrantLock(); // to synchronize the UI Thread and the background Thread
	
	public SynchronizeTask(TraillistActivity activity) {
		this.activity = activity;
		this.trailProvider = TrailProvider.getInstance(Constants.DB_LOCATION);
	}
	
	@Override
	protected void onPreExecute() {
		activity.showDialog(TraillistActivity.DIALOG_PROGRESS_ID);
	}

	@Override
	protected Long doInBackground(String... params) {
		
		mutex.lock();
		try{
			Log.i(Constants.TAG, TAG_PREFIX + "start synchronizing");
			
			String updateUrl = params[0];
			boolean firstDownload = Boolean.valueOf(params[1]);
			
			List<Trail> trails = loadTrailsData(updateUrl);
			
			if(isCancelled())
				return null;
			
			long result = firstDownload ? fillDb(trails) : updateDb(trails);
			
			if(isCancelled())
				return null;
			
			return result;
		}finally{
			mutex.unlock();
		}
	}
	

	/**
	 * This method is invoked from the UI Thread and is therefore safe to update the
	 * UI. This method is just called, if the task has not been canceled.
	 */
	@Override
	protected void onProgressUpdate(Integer... values) {
		String message = activity.getString(values[0]);
		int progress = values[1];
		int max = values[2];
		
		activity.updateProgressbar(message, progress, max);
	}	
	
	/**
	 * Is invoked when the doInBackground() method has finished and AsyncTask.cancel() has not been invoked. 
	 * This method is invoked from the UI Thread and is therefore safe to update the UI.
	 */
	@Override
	protected void onPostExecute(Long result) {
		try{
		}
		finally{ // ensure that this thread is not interrupted
			Log.i(Constants.TAG, TAG_PREFIX + "Start commiting data");
			trailProvider.commit(); // commit db changes
			Log.i(Constants.TAG, TAG_PREFIX + "data commited");
			
			// store new timestamp (Note that when no update was found, 0 is passed as result)
			long lastModifiedTimestamp = Math.max(result, activity.getLastModifiedTimestamp());
			activity.setLastModifiedTimestamp(lastModifiedTimestamp);
			Log.i(Constants.TAG, TAG_PREFIX + "synchronizing completed.");
		}
		activity.syncCompleted();
	}
	
	/**
	 * This method is invoked when the async task is canceled. This is the case when
	 * the user presses the "cancel" button on the progress dialog. 
	 * Note: This method is invoked directly after the <code>asyncTask.cancel()</code>
	 * 		 method call. Since the UI Thread and the Async Thread run concurrently, it's
	 * 		 possible that this method runs before <code>doInBackground()</code> has finished.
	 * 		 And therefore it's possible that the rollback happens before the db insert.
	 * 		 Google API 11 (Android 3.0.x Honeycomb) offers a more convenient method <code>onCancelled(Object)</code>
	 * 		 which is called after <code>doInBackground()</code> has finished. But since we're using
	 * 		 Google API 10, we are implementing our own wait mechanism through a mutex.
	 */
	@Override
	protected void onCancelled() {

		mutex.lock();
		try{
			Log.i(Constants.TAG, TAG_PREFIX + "synchronizing cancelled. Data is rolled back.");
			
			trailProvider.rollback();
			activity.syncAborted();
		}finally{
			mutex.unlock();
		}
	}
	
	/**
	 * Downloads the Trails data from the web. Converts it into Trail objects and
	 * returns it as list.
	 * 
	 * @param url The url to fetch
	 * @return a List of downloaded Trails
	 */
	private List<Trail> loadTrailsData(String url) {
		Log.i(Constants.TAG, TAG_PREFIX + "Start downloading new data from the web");
		
		List<Trail> trails = new ArrayList<Trail>();
		HttpHandler httpHandler = new HttpHandler();
		InputStream responseInputStream = null;
		Reader responseReader = null;
		int contentLength = 0;

		try{
			HttpResponse response = httpHandler.connectTo(url, TYPE_JSON, CONNECTION_MODE_CLOSE, true);
			contentLength = (int) Math.max(0, response.getEntity().getContentLength());
			Log.i(Constants.TAG, TAG_PREFIX + "Content Length of Download = " + contentLength);

			responseInputStream = response.getEntity().getContent();
			ByteArrayBuffer byteBuffer = downloadData(responseInputStream, contentLength);
			if(byteBuffer == null) // if cancel was invoked
				return null;
			
			responseReader = unzip(response, byteBuffer);
			
			trails = parseData(responseReader);
			if(trails == null) // if cancel was invoked
				return null;
			
		} catch (IOException e) {
			Log.i(Constants.TAG, TAG_PREFIX + "Exception while reading from input stream", e);
		}finally{
			// ensure that the stream is closed
			HttpHandler.safeClose(responseInputStream); 
			HttpHandler.safeClose(responseReader);
		}
		
		Log.i(Constants.TAG, TAG_PREFIX + "#" + trails.size() + " Trails downloaded & parsed");
		return trails;
	}

	/**
	 * Downloads the data. It reads all content from the inputStream and stores it into a byteBuffer.
	 * The reason why it is copied in chunks, is because the user might pushes the cancel button during
	 * the download.
	 * 
	 * @param inputStream The input stream to read from
	 * @param contentLength The amount of data to read (just necessary for the progressbar)
	 * @return A ByteArrayBuffer with the whole http response or null if cancel was invoked.
	 * @throws IOException
	 */
	private ByteArrayBuffer downloadData(InputStream inputStream, int contentLength) throws IOException {
		ByteArrayBuffer byteBuffer = new ByteArrayBuffer(contentLength);
		byte[] buffer = new byte[2048];
		int read = 0;
		while( (read = inputStream.read(buffer)) >= 0){
			if(isCancelled()){
				Log.i(Constants.TAG, TAG_PREFIX + "Stop downloading data, since cancel was invoked");
				return null;
			}
			byteBuffer.append(buffer, 0, read);
			
			// update progress bar
			publishProgress(R.string.progressbar_downloading, byteBuffer.length(), contentLength);
		}
		return byteBuffer;
	}

	/**
	 * Parses the JSON Data and converts them into a List of Trail data.
	 *  
	 * @param reader The Reader which contains the JSON Data
	 * @return a List of Trails or null if cancel was invoked.
	 */
	private List<Trail> parseData(Reader reader) {
		Log.i(Constants.TAG, TAG_PREFIX + "start parsing data");
		List<Trail> trails = new ArrayList<Trail>();
		Gson gson = new Gson();
		JsonElement json = new JsonParser().parse(reader);
		
		for (JsonElement element : json.getAsJsonArray()) {
			
			if(isCancelled()){
				Log.i(Constants.TAG, TAG_PREFIX + "Stop parsing data, since cancel was invoked");
				return null;
			}
			trails.add(gson.fromJson(element, Trail.class));
		}
		return trails;
	}
	
	
	/**
	 * Checks whether the HTTP response is gzip encoded and if so decodes it. 
	 * 
	 * @param response The HTTP response
	 * @param buffer The buffer which contains the HTTP response content
	 * @return A Reader containing the decoded HTTP response content 
	 * 
	 * @throws UnsupportedEncodingException
	 * @throws IOException
	 */
	private Reader unzip(HttpResponse response, ByteArrayBuffer buffer) throws UnsupportedEncodingException, IOException {
		Header header = response.getFirstHeader("Content-Encoding");
		if(header != null && "gzip".equalsIgnoreCase(header.getValue())){
			Log.i(Constants.TAG, TAG_PREFIX + "unzip input stream");
			ByteArrayInputStream bais = new ByteArrayInputStream(buffer.buffer());
			return new InputStreamReader(new GZIPInputStream(bais), "utf-8");
		}

		// convert from byte to string and set the correct encoding
		Log.i(Constants.TAG, TAG_PREFIX + "create parsable string");
		return new StringReader(new String(buffer.buffer(), "utf-8"));
	}	
	
	/**
	 * Fills the Db for the first time. It should just be used for this purpose. 
	 * It is just done in a second method, for better maintainability. 
	 * Note that the data is not yet persited after this method call!
	 * 
	 * @param trails The trails to fill.
	 * @return the latest modified timestamp
	 */
	private long fillDb(List<Trail> trails){
		Log.i(Constants.TAG, TAG_PREFIX + "Start filling the db for the first time");
		long lastModifiedTimestamp = 0;
		
		for(int i = 0; i < trails.size() ; i++){
			
			Trail newTrail = trails.get(i);
			
			if(isCancelled()){
				Log.i(Constants.TAG, TAG_PREFIX + "Stop filling the db, since cancel was invoked");
				return -1;
			}
			
			lastModifiedTimestamp = Math.max(lastModifiedTimestamp, newTrail.getModifiedUnixTs());
			
			if(!newTrail.isDeleted()){ 
				trailProvider.store(newTrail);
			}
			
			//update progress bar
			publishProgress(R.string.progressbar_updating, i, trails.size());
		}	
		
		Log.i(Constants.TAG, TAG_PREFIX + "Db fill complete, but commit is outstanding");
		return lastModifiedTimestamp;		
	}
	
	/**
	 * Updates the DB with the new data. Note that the data is not yet persited after this method call!
	 * 
	 * @param trails A list of Trails to update
	 * @return The max value of last modified timestamps or 0 if no updates were found
	 */
	private long updateDb(List<Trail> trails) {
		Log.i(Constants.TAG, TAG_PREFIX + "Start updating the db");
		long lastModifiedTimestamp = 0;
		
		for(int i = 0; i < trails.size(); i++){
			
			Trail downloadedTrail = trails.get(i);
			
			if(isCancelled()){
				Log.i(Constants.TAG, TAG_PREFIX + "Stop updating the db, since cancel was invoked");
				return -1;
			}
			
			lastModifiedTimestamp = Math.max(lastModifiedTimestamp, downloadedTrail.getModifiedUnixTs());

			Log.i(Constants.TAG, TAG_PREFIX + "Find trail with id = " + downloadedTrail.getId());
			Trail existingTrail = trailProvider.find(downloadedTrail.getId());
			Log.i(Constants.TAG, TAG_PREFIX + "Trail " + existingTrail != null ? "found" : "not found");
			
			if(isTrailNotInDb(existingTrail)){
				if(!downloadedTrail.isDeleted()){
					trailProvider.store(downloadedTrail);
				}else{
					// do nothing, since trail is not in local db, 
					// but has already been deleted on server side
				}
			}else{
				if(downloadedTrail.isDeleted()){ // deleted
					trailProvider.delete(existingTrail);
				}else{ // modified
					trailProvider.delete(existingTrail);
					trailProvider.store(downloadedTrail);
				}
			}
			
			// update progress bar
			publishProgress(R.string.progressbar_updating, i, trails.size());
			
			Log.i(Constants.TAG, TAG_PREFIX + "Trail updated");
		}	
		
		Log.i(Constants.TAG, TAG_PREFIX + "Db update complete, but commit is outstanding");
		return lastModifiedTimestamp;
	}

	/**
	 * It is a new trail if it doesn't exist on the db yet.
	 * 
	 * @param existingTrail The trail to check
	 * @return true if it 
	 */
	private boolean isTrailNotInDb(Trail existingTrail) {
		return existingTrail == null;
	}
}
