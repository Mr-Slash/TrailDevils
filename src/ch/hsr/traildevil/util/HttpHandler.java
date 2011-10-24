package ch.hsr.traildevil.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.graphics.drawable.Drawable;
import android.util.Log;
import ch.hsr.traildevil.R;
import ch.hsr.traildevil.application.TrailDevilsController;

public class HttpHandler {

	private static final String TAG = "traildevil";
	private static final String TAG_PREFIX = HttpHandler.class.getSimpleName() + ": ";

	private HttpClient client = new DefaultHttpClient();
	private InputStreamReader isr;

	public void connectTo(String url) {
		try {
			HttpGet httpGet = new HttpGet(url);
			httpGet.setHeader("Accept", "application/json");

			Log.i(TAG, TAG_PREFIX + "send http request to url:" + url);
			HttpResponse response = client.execute(httpGet);
			isr = new InputStreamReader(response.getEntity().getContent(), "utf-8");
			Log.i(TAG, TAG_PREFIX + "http response received");
			for(Header header : response.getAllHeaders()){
				Log.i(TAG, TAG_PREFIX + header.getName() + ": " + header.getValue());
			}
		} catch (Exception e) {
			Log.e(TAG, TAG_PREFIX + "connecting to server failed", e);
		}
	}

	public boolean isHostReachable(String url) {
		Log.i(TAG, TAG_PREFIX + "check if host is reachable at url: " + url);

		HttpGet httpGet = new HttpGet(url);
		HttpResponse response = null;
		try {
			response = client.execute(httpGet);
		} catch (IOException e) {
			Log.e(TAG, TAG_PREFIX + "connecting to server failed", e);
		}
		return response == null ? false : true;
	}

	public InputStreamReader getReader() {
		return (isr != null) ? isr : null;
	}

	public void resetStream() {
		if (isr != null) {
			try {
				isr.close();
			} catch (IOException e) {
				Log.e(TAG, TAG_PREFIX + "closing input stream failed", e);
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
				return TrailDevilsController.context.getResources().getDrawable(R.drawable.photo_not_available);
			}
		} catch (MalformedURLException e) {
			Log.e(TAG, TAG_PREFIX + "parsing URL failed", e);
		} catch (IOException e) {
			Log.e(TAG, TAG_PREFIX + "problem with i/o", e);
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
					Log.e(TAG, TAG_PREFIX + "closing input stream failed", e);
				}
			}
		}
		return null;
	}

}
