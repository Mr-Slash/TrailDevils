package ch.hsr.traildevil.util.persistence;

import android.util.Log;

import ch.hsr.traildevil.util.Constants;

import com.db4o.Db4oEmbedded;
import com.db4o.ObjectContainer;

public class Db4oHelper {

	private static final String TAG_PREFIX = Db4oHelper.class.getSimpleName() + ": ";
	
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
		} catch (Exception e) {
			Log.e(Constants.TAG, TAG_PREFIX + "accessing the db failed", e);
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
	 * Rolls back the running transaction. Note that Data already in memory are not
	 * restored! TODO Check what happens if data are modified and then canceled!
	 */
	public void rollback(){
		if(oc != null){
			db().rollback();
		}
	}

	/**
	 * Commits the transaction and closes the database.
	 */
	public void close() {
		if (oc != null) {
			oc.close();
		}
	}
	
	/**
	 * Commits the changes to the db. Note that a commit will commit this transaction and start
	 * Immediately a new one! 
	 */
	public void commit(){
		if(oc != null){
			oc.commit();
		}
	}
}
