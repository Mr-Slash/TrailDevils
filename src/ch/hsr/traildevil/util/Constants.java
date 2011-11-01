package ch.hsr.traildevil.util;

public class Constants {
	
	/** The log tag in order to filter the log output */
	public static final String TAG = "traildevil";
	
	/** Device wide unique account type identifier (must match with the name defined in authenticator.xml)*/
	public static final String ACCOUNT_TYPE = "ch.hsr.traildevil";
	
	/** The authority to sync trail data (must match with the name defined in syncadapter.xml) */
	public static final String TRAIL_AUTHORITY = "ch.hsr.traildevil.Trail";

	/** Device wide unique authentication token type */
    public static final String AUTH_TOKEN_TYPE = "ch.hsr.traildevil";
	
	/** The location of the DB */
	public static final String DB_LOCATION = "/data/data/ch.hsr.traildevil/app_data";
	
	/** productive URL */
	public static final String TRAILS_URL = "http://152.96.80.18:8080/api/trails";
	
	/** test URL. Make sure that it points to your test server (not localhost, since we'are on the device)! */
	public static final String TEST_TRAILS_URL = "http://172.30.51.223:8080/TrailDevilsServer/trails";	
	
	/** How many stars can be maximum filled in the Trail List */
	public static final int MAX_FAVORITE_STARS = 7;
}
