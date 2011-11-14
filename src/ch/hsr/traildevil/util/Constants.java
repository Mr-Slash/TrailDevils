package ch.hsr.traildevil.util;

public class Constants {
	
	/** The log tag in order to filter the log output */
	public static final String TAG = "traildevil";
	
	/** The location of the DB */
	public static String DB_LOCATION = "/data/data/ch.hsr.traildevil/files";
	
	/** productive URL */
	public static final String TRAILS_URL = "http://152.96.80.18:8080/api/trails?$orderby=favorits%20desc";
	
	/** test URL. Make sure that it points to your test server (not localhost, since we'are on the device)! */
	public static final String TEST_TRAILS_URL = "http://172.30.51.223:8080/TrailDevilsServer/trails";	
	
	/** How many stars can be maximum filled in the Trail List */
	public static final int MAX_FAVORITE_STARS = 5;
	
	/** The last modified timestamp of all trail. This key could be used to lookup the value from shared preferences.*/
	public static final String LAST_MODIFIED_TIMESTAMP_KEY = "LAST_MODIFIED_TIMESTAMP";
}
