package ch.hsr.traildevil.util.persistence;

import android.util.Log;
import ch.hsr.traildevil.domain.Trail;
import ch.hsr.traildevil.util.Constants;

import com.db4o.Db4oEmbedded;
import com.db4o.ObjectContainer;
import com.db4o.config.EmbeddedConfiguration;

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
				oc = Db4oEmbedded.openFile(createConfiguration(), db4oDBFullPath());
			}
			return oc;
		} catch (Exception e) {
			Log.e(Constants.TAG, TAG_PREFIX + "accessing the db failed", e);
			return null;
		}
	}

	private EmbeddedConfiguration createConfiguration() {
		EmbeddedConfiguration config = Db4oEmbedded.newConfiguration();
		config.common().objectClass(Trail.class).objectField("Id").indexed(true);
		config.common().messageLevel(1);
		return config;
	}

	/**
	 * Returns the path for the database location
	 */
	private String db4oDBFullPath() {
		return dbLocation + "/" + "traildevils.db4o";
	}
	
	/**
	 * Rolls back the running transaction. Note that Data already in memory are not
	 * restored!
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
