package ch.hsr.traildevil.util.persistence;

import java.util.Collections;
import java.util.List;

import android.util.Log;

import ch.hsr.traildevil.domain.Trail;

public class TrailProvider extends Db4oHelper {

	private static final String TAG = "traildevil";
	private static final String TAG_PREFIX = TrailProvider.class.getSimpleName() + ": ";
	
	private static TrailProvider provider;
	
	private TrailProvider(String dbLocation) {
		super(dbLocation);
	}

	public static TrailProvider getInstance(String dbLocation) {
		if(provider == null)
			provider = new TrailProvider(dbLocation);
		
		return provider;
	}

	public void store(Trail trail) {
		db().store(trail);
	}
	
	public void store(List<Trail> trails){
		for(Trail trail : trails){
			store(trail);
		}
	}

	public void delete(Trail trail) {
		db().delete(trail);
	}

	public List<Trail> findAll() {
		return Collections.unmodifiableList(db().query(Trail.class));
	}
	
	public void deleteAll(){
		Log.i(TAG, TAG_PREFIX + "start deleteAll()");
		for(Trail trail : findAll()){
			delete(trail);
		}
		Log.i(TAG, TAG_PREFIX + "end deleteAll()");
	}
}
