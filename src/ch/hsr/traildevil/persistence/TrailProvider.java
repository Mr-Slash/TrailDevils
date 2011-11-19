package ch.hsr.traildevil.persistence;

import java.util.ArrayList;
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
		if (provider == null)
			provider = new TrailProvider(dbLocation);

		return provider;
	}

	public void store(Trail trail) {
		db().store(trail);
	}

	public void store(List<Trail> trails) {
		for (Trail trail : trails) {
			store(trail);
		}
	}

	public void delete(Trail trail) {
		db().delete(trail);
	}

	/**
	 * Searches for an existing Trail with the given id.
	 * 
	 * @param id
	 *            The Trail with the given id
	 * @return The matched Trail or null if no one exists
	 */
	public Trail find(final int id) {
		Trail template = new Trail();
		template.setId(id);
		
		List<Trail> trails = db().queryByExample(template);

		if (trails.size() > 0)
			return trails.get(0);
		return null;
	}

	public List<Trail> findAll() {
		List<Trail> trails = db().query(Trail.class);
		if (trails == null)
			trails = new ArrayList<Trail>();

		return trails;
	}

	public List<Trail> findAllSorted() {
		List<Trail> trails = findAll();

		// copy the returned result, since the list returned by the db doesn't
		// support reordering!
		List<Trail> copy = new ArrayList<Trail>(trails.size());
		for (Trail trail : trails) {
			copy.add(trail);
		}

		Collections.sort(copy);
		return copy;
	}

	public void deleteAll() {
		Log.i(Constants.TAG, TAG_PREFIX + "start deleteAll()");
		for (Trail trail : findAll()) {
			delete(trail);
		}
		Log.i(Constants.TAG, TAG_PREFIX + "end deleteAll()");
	}
}
