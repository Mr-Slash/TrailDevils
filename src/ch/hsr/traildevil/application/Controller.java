package ch.hsr.traildevil.application;

import java.util.List;

import android.content.Context;
import android.os.AsyncTask;
import android.os.AsyncTask.Status;
import android.util.Log;
import ch.hsr.traildevil.domain.Trail;
import ch.hsr.traildevil.persistence.TrailProvider;
import ch.hsr.traildevil.presentation.TraillistActivity;
import ch.hsr.traildevil.util.Constants;
import ch.hsr.traildevil.util.network.HttpHandler;

public class Controller {

	private static final String TAG_PREFIX = Controller.class.getSimpleName() + ": ";

	private static HttpHandler httpHandler = new HttpHandler();
	private ch.hsr.traildevil.persistence.TrailProvider trailProvider;
	private AsyncTask<String, Integer, Long> asyncTask;

	public Controller(Context ctx) {
		initInternalStorageDir(ctx);
		trailProvider = TrailProvider.getInstance(Constants.DB_LOCATION);

		// ensures that the db is created/opened within the UI Thread and access
		// rights are given
		trailProvider.db();
	}

	private void initInternalStorageDir(Context ctx) {
		Constants.DB_LOCATION = ctx.getFilesDir().getAbsolutePath();
	}

	public List<Trail> getTrails() {
		return trailProvider.findAllSorted();
	}

	public Trail getTrail(int index) {
		return getTrails().get(index);
	}

	public Trail findTrail(int id) {
		return trailProvider.find(id);
	}

	public boolean isNetworkAvailable() {
		return httpHandler.isHostReachable(getTrailsUrl());
	}

	private String getTrailsUrl() {
		return Constants.TRAILS_URL;
	}

	public int getMaxFavorits() {
		int max = 0;
		for (Trail trail : getTrails()) {
			max = Math.max(max, trail.getFavorits());
		}
		return max;
	}

	/**
	 * Whether this is the first data download or not. If so, the whole Data is
	 * downloaded without any filter. Note that it is not possible to filter out
	 * deleted trails, since then we would might miss the latest ModifiedUnixTs.
	 * For evaluation the lastModifiedTimestamp within the shared properties is
	 * used.
	 * 
	 * @param activity
	 *            The activity to access shared properties
	 * 
	 * @return whether or not it is the initial download
	 */
	private boolean isFirstDownload(TraillistActivity activity) {
		return activity.getLastModifiedTimestamp() == 0;
	}

	/**
	 * Start synchronizing the Trail data.
	 * 
	 * @param traillistActivity
	 *            The activity to notify about changes
	 */
	public void startSynchronization(TraillistActivity activity) {
		Log.i(Constants.TAG, TAG_PREFIX + "start async task");

		String updateUrl = getTrailsUrl();

		if (!isFirstDownload(activity)) {
			long lastModifiedTimestamp = activity.getLastModifiedTimestamp();
			updateUrl += "&$filter=ModifiedUnixTs%20gt%20" + lastModifiedTimestamp;
		}

		// start async thread
		asyncTask = new SynchronizeTask(activity).execute(updateUrl, String.valueOf(isFirstDownload(activity)));
	}

	/**
	 * Stops synchronizing the Trail data, if the task is running or pending(=
	 * not started yet).
	 */
	public void stopSynchronization() {
		if (asyncTask != null && (asyncTask.getStatus() == Status.RUNNING || asyncTask.getStatus() == Status.PENDING)) {
			asyncTask.cancel(false);
		}
	}
}
