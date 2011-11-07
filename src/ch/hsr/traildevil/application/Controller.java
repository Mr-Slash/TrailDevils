package ch.hsr.traildevil.application;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import android.os.AsyncTask;
import android.os.AsyncTask.Status;
import android.util.Log;
import ch.hsr.traildevil.TraillistActivity;
import ch.hsr.traildevil.domain.Trail;
import ch.hsr.traildevil.sync.SynchronizeTask;
import ch.hsr.traildevil.util.Constants;
import ch.hsr.traildevil.util.network.HttpHandler;
import ch.hsr.traildevil.util.persistence.TrailProvider;

public class Controller {
	
	private static final String TAG = "traildevil";
	private static final String TAG_PREFIX = Controller.class.getSimpleName() + ": ";
	
	private static HttpHandler httpHandler = new HttpHandler();
	private TrailProvider trailProvider;
	private AsyncTask<String, String, Long> asyncTask;

	public Controller(){
		trailProvider = TrailProvider.getInstance(Constants.DB_LOCATION);
	}
	
	public List<Trail> getTrails(){
		if(trailProvider.findAll() == null)
			return new ArrayList<Trail>();
			//loadTrailsData();
		
		return trailProvider.findAll();
	}
	
	public Trail getTrail(int index){
		return getTrails().get(index);
	}
	
	
	public boolean isNetworkAvailable(){
		return httpHandler.isHostReachable(getTrailsUrl());
	}

	private String getTrailsUrl() {
		return Constants.TRAILS_URL;
	}
	
	public int getMaxFavorits(){
		int max = 0;
		for (Trail trail : getTrails()) {
			max = Math.max(max, trail.getFavorits());
		}
		return max;
	}

	/**
	 * Start synchronizing the Trail data.
	 * @param traillistActivity The activity to notify about changes
	 */
	public void startSynchronization(TraillistActivity activity) {
		Log.i(Constants.TAG, TAG_PREFIX + "start async task");

		long lastModifiedTimestamp = activity.getLastModifiedTimestamp();
		//String updateUrl = getTrailsUrl() + "?$filter=LastModifiedTs%20gt%20"+ lastModifiedTimestamp;
		String updateUrl = getTrailsUrl() + "?$filter=TrailId%20lt%2010"; // get 10 first entries to simulate update

		// start async thread
		asyncTask = new SynchronizeTask(activity).execute(updateUrl);
	}

	/**
	 * Stops synchronizing the Trail data, if the task is running or pending(= not started yet). 
	 */
	public void stopSynchronization() {
		if(asyncTask != null && asyncTask.getStatus() == Status.RUNNING || asyncTask.getStatus() == Status.PENDING){
			asyncTask.cancel(false);
		}
	}
}
