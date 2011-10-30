package ch.hsr.traildevil.authenticator;

import ch.hsr.traildevil.util.Constants;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

/**
 * Service to handle Account authentication. Authenticator service that returns a subclass 
 * of AbstractAccountAuthenticator in onBind()
 */
public class AuthenticatorService extends Service {

	private static final String TAG_PREFIX = AuthenticatorService.class.getSimpleName() + ": ";
	private Authenticator accountAuthenticator = null;
	
	@Override
	public void onCreate() {
		Log.i(Constants.TAG, TAG_PREFIX + "created");
		accountAuthenticator = new Authenticator(this);
	}
	
	@Override
	public void onDestroy() {
		Log.i(Constants.TAG, TAG_PREFIX + "destroied");
	}

	@Override
	public IBinder onBind(Intent intent) {
		Log.i(Constants.TAG, TAG_PREFIX + "onBind()");
		return accountAuthenticator.getIBinder();
	}
}