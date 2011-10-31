package ch.hsr.traildevil.application;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import android.content.Context;
import android.util.Log;
import ch.hsr.traildevil.domain.Trail;
import ch.hsr.traildevil.util.Constants;
import ch.hsr.traildevil.util.network.HttpHandler;
import ch.hsr.traildevil.util.persistence.TrailProvider;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

public class Controller {
	
	private static final String TAG = "traildevil";
	private static final String TAG_PREFIX = Controller.class.getSimpleName() + ": ";
	
	private static HttpHandler httpHandler = new HttpHandler();
	private TrailProvider trailProvider;
	
	public Controller(){
		trailProvider = TrailProvider.getInstance(Constants.DB_LOCATION);
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
	 * Synchronize raw Trails
	 *  
	 * @param context The context of Authenticator Activity
	 * @param trails The list of trails
	 */
	public static synchronized void syncTrails(Context context, List<Trail> trails){
		TrailProvider trailProvider = TrailProvider.getInstance(Constants.DB_LOCATION);
		
		for(Trail trail : trails){
			Trail found = trailProvider.find(trail.getTrailId());
			
			if(found == null){
				trailProvider.store(trail);
			}else{
				// if update
				found.setName(trail.getName());
				found.setCountry(trail.getName());
				// etc
				
				// if delete
				//delete from db
			}
		}
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
		return Constants.TRAILS_URL;
	}
}
