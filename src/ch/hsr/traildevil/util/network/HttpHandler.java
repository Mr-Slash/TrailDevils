package ch.hsr.traildevil.util.network;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import android.graphics.drawable.Drawable;
import android.util.Log;
import ch.hsr.traildevil.util.Constants;

public class HttpHandler {

	private static final String TAG_PREFIX = HttpHandler.class.getSimpleName() + ": ";
	
	//TODO simulate with slow connection if data could be downloaded without timeout exception 
	private static final int 	CONNECTION_TIMEOUT = 10 * 1000; // ms 
	
	private HttpClient client = new DefaultHttpClient();
	private InputStreamReader isr;

	public static String TYPE_JSON = "application/json";
	public static String TYPE_XML = "application/xml";
	
	
	/** 
	 * Keeps the connection alive for subsequent TCP requests. This is the standard behavior in HTTP1.1 when
	 * no connection mode is set explicitly. 
	 * Note:
	 * When inputStream.close() is invoked (just with keep-alive mode) it takes around 60 seconds to close, 
	 * since httpClient tries to keep the connection alive. Take a look 
	 * at: http://web.archiveorange.com/archive/v/NoFkI0ERZDXOHvEf2bK4
	 */
	public static String CONNECTION_MODE_KEEP_ALIVE = "keep-alive";
	
	/** Closes the connection either if response is complete or inputStream.close() has been invoked */
	public static String CONNECTION_MODE_CLOSE = "close";
	
	public HttpHandler(){
		final HttpParams params = client.getParams();
		// set connection params
        HttpConnectionParams.setConnectionTimeout(params, CONNECTION_TIMEOUT);
        HttpConnectionParams.setSoTimeout(params, CONNECTION_TIMEOUT);
        
        // set connection manager params
        ConnManagerParams.setTimeout(params, CONNECTION_TIMEOUT);
	}
	
	public void connectTo(String url, String type, String connectionMode) {
		try {
			HttpGet httpGet = new HttpGet(url);
			httpGet.setHeader("Accept", type);
			httpGet.setHeader("Connection", connectionMode);
			
			Log.i(Constants.TAG, TAG_PREFIX + "send http request to url:" + url);
			HttpResponse response = client.execute(httpGet);
			isr = new InputStreamReader(response.getEntity().getContent(), "utf-8");
			Log.i(Constants.TAG, TAG_PREFIX + "http response received");
		} catch (Exception e) {
			Log.e(Constants.TAG, TAG_PREFIX + "connecting to server failed", e);
		}
	}

	public boolean isHostReachable(String url) {
		Log.i(Constants.TAG, TAG_PREFIX + "check if host is reachable at url: " + url);

		HttpGet httpGet = new HttpGet(url);
		HttpResponse response = null;
		try {
			response = client.execute(httpGet);
		} catch (IOException e) {
			Log.e(Constants.TAG, TAG_PREFIX + "connecting to server failed", e);
		}
		
		Log.i(Constants.TAG, TAG_PREFIX + "host is reachable = : " + (response != null));
		return response != null;
	}

	public InputStreamReader getReader() {
		return (isr != null) ? isr : null;
	}

	public void resetStream() {
		if (isr != null) {
			try {
				isr.close();
			} catch (IOException e) {
				Log.e(Constants.TAG, TAG_PREFIX + "closing input stream failed", e);
			}
		}
	}

	public static Drawable getHttpImage(String url) {
		InputStream is = null;
		try {
			if (url != null) {
				is = new URL(url).openStream();
				return Drawable.createFromStream(is, null);
			} else {
				return null;
			}
		} catch (MalformedURLException e) {
			Log.e(Constants.TAG, TAG_PREFIX + "parsing URL failed", e);
		} catch (IOException e) {
			Log.e(Constants.TAG, TAG_PREFIX + "problem with i/o", e);
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
					Log.e(Constants.TAG, TAG_PREFIX + "closing input stream failed", e);
				}
			}
		}
		return null;
	}

}
