package ch.hsr.traildevil.util.persistence;

import android.util.Log;

import com.db4o.Db4oEmbedded;
import com.db4o.ObjectContainer;

public class Db4oHelper {

	private static ObjectContainer oc = null;
	private String dbLocation;

	public Db4oHelper(String dbLocation) {
		this.dbLocation = dbLocation;
	}

	/**
	 * Create, open and close the database
	 */
	public ObjectContainer db() {

		try {
			if (oc == null || oc.ext().isClosed()) {
				oc = Db4oEmbedded.openFile(Db4oEmbedded.newConfiguration(), db4oDBFullPath());

				// We first load the initial data from the database
				// ExercisesLoader.load(context, oc);
			}
			return oc;
		} catch (Exception ie) {
			Log.e(Db4oHelper.class.getName(), ie.toString());
			return null;
		}
	}

	/**
	 * Returns the path for the database location
	 */
	private String db4oDBFullPath() {
		return dbLocation + "/" + "traildevils.db4o";
	}

	/**
	 * Closes the database
	 */
	public void close() {
		if (oc != null) {
			oc.close();
		}
	}
}
