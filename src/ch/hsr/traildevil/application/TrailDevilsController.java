package ch.hsr.traildevil.application;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import android.content.Context;
import android.util.Log;
import ch.hsr.traildevil.domain.Trail;
import ch.hsr.traildevil.util.HttpHandler;
import ch.hsr.traildevil.util.TrailProvider;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

public class TrailDevilsController {
	
	private static final String TAG = "traildevil";
	private static final String TAG_PREFIX = TrailDevilsController.class.getSimpleName() + ": ";
	
	// productive URL
	private static final String TRAILS_URL = "http://152.96.80.18:8080/api/trails";
	
	// test URL. Make sure that it points to your test server (not localhost, since we'are on the device)!
	private static final String TEST_TRAILS_URL = "http://172.30.51.223:8080/TrailDevilsServer/trails";
	
	private static HttpHandler httpHandler = new HttpHandler();
	public static Context context;
	
	private TrailProvider trailProvider;
	
	public TrailDevilsController(String dbLocation, Context ctx){
		trailProvider = TrailProvider.getInstance(dbLocation);
		trailProvider.deleteAll();
		TrailDevilsController.context = ctx;
	}
	
	public List<Trail> getTrails(){
		
		if(trailProvider.findAll() == null || trailProvider.findAll().isEmpty())
			loadTrailsData();
		
		return trailProvider.findAll();
	}
	
	public Trail getTrail(int index){
		return getTrails().get(index);
	}
	
	
	private void loadTrailsData() {
		Log.i(TAG, TAG_PREFIX + "no trail data found in DB. Start downloading it from the web");
		httpHandler.connectTo(getTrailsUrl());
		
		List<Trail> trails = new ArrayList<Trail>(500);

		JsonElement json = new JsonParser().parse(httpHandler.getReader());
		Iterator<JsonElement> iterator = json.getAsJsonArray().iterator();
		Gson gson = new Gson();
		while (iterator.hasNext()) {
			trails.add(gson.fromJson((JsonElement) iterator.next(), Trail.class));
		}
		httpHandler.resetStream();
		
		// store in db
		trailProvider.store(trails);
		Log.i(TAG, TAG_PREFIX + "trail data downloaded and stored in the DB");
	}

	/**
	 * Closes the db session. Note that a close is required for a commit!
	 */
	public void close() {
		trailProvider.close();
	}
	
	public boolean isNetworkAvailable(){
		return httpHandler.isHostReachable(getTrailsUrl());
	}

	private String getTrailsUrl() {
		return TRAILS_URL;
	}
}
