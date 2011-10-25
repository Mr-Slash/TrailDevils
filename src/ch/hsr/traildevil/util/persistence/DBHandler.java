package ch.hsr.traildevil.util.persistence;

import java.util.Collections;
import java.util.List;

import ch.hsr.traildevil.domain.Trail;

import com.db4o.Db4oEmbedded;
import com.db4o.ObjectContainer;

public class DBHandler {
	
	private static final String DB_FILENAME = "traildevil.db4o";

	// A different configuration is not needed at this point. 
	// Enhance it when cascading of updates are required!
	private static ObjectContainer db = Db4oEmbedded.openFile(Db4oEmbedded.newConfiguration(), DB_FILENAME); 
	
	public void closeDb(){
		if(db != null)
			db.close();
	}
	
	/**
	 * Returns an unmodifiable list, to be sure that no entries are deleted or added.
	 *  
	 * @return a List of Trails
	 */
	public List<Trail> getTrails(){
		return Collections.unmodifiableList(db.query(Trail.class));
	}

	/**
	 * This method inserts and updates the db.
	 * 
	 * @param trails the Trails to save.
	 */
	public void save(List<Trail> trails){
		for(Trail trail : trails){
			db.store(trail);
		}
	}
	
	public void delete(List<Trail> trails){
		for(Trail trail : trails){
			db.delete(trail);
		}
	}
}
