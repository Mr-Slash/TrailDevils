package ch.hsr.traildevil.util.persistence;

import java.util.Collections;
import java.util.List;

import android.util.Log;
import ch.hsr.traildevil.domain.Trail;
import ch.hsr.traildevil.util.Constants;

import com.db4o.query.Predicate;

public class TrailProvider extends Db4oHelper {

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
	
	/**
	 * Searches for an existing Trail with the given id.
	 * 
	 * @param id The Trail with the given id
	 * @return The matched Trail or null if no one exists
	 */
	public Trail find(final int id){
		List<Trail> trails = db().query(new Predicate<Trail>() {
			@Override
			public boolean match(Trail trail) {
				return trail.getId() == id;
			}
		});
		
		if(trails.size() > 0)
			return trails.get(0);		
		return null;
	}

	public List<Trail> findAll() {
		return db().query(Trail.class);
	}
	
	public void deleteAll(){
		Log.i(Constants.TAG, TAG_PREFIX + "start deleteAll()");
		for(Trail trail : findAll()){
			delete(trail);
		}
		Log.i(Constants.TAG, TAG_PREFIX + "end deleteAll()");
	}
}
