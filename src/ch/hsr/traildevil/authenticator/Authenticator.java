package ch.hsr.traildevil.authenticator;

import android.accounts.AbstractAccountAuthenticator;
import android.accounts.Account;
import android.accounts.AccountAuthenticatorResponse;
import android.accounts.AccountManager;
import android.accounts.NetworkErrorException;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import ch.hsr.traildevil.util.Constants;
import ch.hsr.traildevil.util.network.NetworkUtilities;

/**
 * This class is an implementation of AbstractAccountAuthenticator for
 * authenticating accounts in the TrailDevils domain.
 */
public class Authenticator extends AbstractAccountAuthenticator {

	private static final String TAG_PREFIX = Authenticator.class.getSimpleName() + ": ";
	
	private Context context; //Authentication Service context

	public Authenticator(Context context) {
		super(context);
		this.context = context;
	}

	/**
	 * The user has requested to add a new account to the system. We return an
	 * intent that will launch our login screen if the user has not logged in
	 * yet, otherwise our activity will just pass the user's credentials on to
	 * the account manager.
	 */
	@Override
	public Bundle addAccount(AccountAuthenticatorResponse response, String accountType, String authTokenType, String[] requiredFeatures, Bundle options) throws NetworkErrorException {
		Log.i(Constants.TAG, TAG_PREFIX + "addAcount(" +
				" accountType = " + (accountType != null ? accountType : "null") +
				" authTokenType = " + (authTokenType != null ? authTokenType : "null") + ")");
		
		Intent intent = new Intent(context, AuthenticatorActivity.class);
		intent.putExtra(AuthenticatorActivity.PARAM_AUTHTOKEN_TYPE, authTokenType);
		intent.putExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE, response);

		Bundle reply = new Bundle();
		reply.putParcelable(AccountManager.KEY_INTENT, intent);
		return reply;
	}

	/**
	 * Before every sync, we check that the already entered account credentials are still correct.
	 * {@inheritDoc}
	 */
	@Override
	public Bundle confirmCredentials(AccountAuthenticatorResponse response, Account account, Bundle options) {
		Log.i(Constants.TAG, TAG_PREFIX + "confirmCredentials()");
		
		// check if username password is still correct (online) 
		if(options != null && options.containsKey(AccountManager.KEY_PASSWORD)){
			String password = options.getString(AccountManager.KEY_PASSWORD);
			boolean verified = onlineConfirmPassword(account.name, password);
			Bundle result = new Bundle();
			result.putBoolean(AccountManager.KEY_BOOLEAN_RESULT, verified);
			return result;		
		}
		
		// Launch AuthenticatorActivity to confirm the credentials again if no password was set at all
		Log.i(Constants.TAG, TAG_PREFIX + "no password is set at all -> goto authentication activity");

		Intent intent = new Intent(context, AuthenticatorActivity.class);
		intent.putExtra(AuthenticatorActivity.PARAM_USERNAME, account.name);
		intent.putExtra(AuthenticatorActivity.PARAM_CONFIRMCREDENTIALS, true);
		intent.putExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE, response);
		
		Bundle bundle = new Bundle();
		bundle.putParcelable(AccountManager.KEY_INTENT, intent);
		return bundle;		
	}

	@Override
	public Bundle editProperties(AccountAuthenticatorResponse response, String accountType) {
		Log.i(Constants.TAG, TAG_PREFIX + "editProperties()");
		throw new UnsupportedOperationException();
	}

	@Override
	public Bundle getAuthToken(AccountAuthenticatorResponse response, Account account, String authTokenType, Bundle options) throws NetworkErrorException {
		Log.i(Constants.TAG, TAG_PREFIX + "getAuthToken()");
		return null;
	}

	@Override
	public String getAuthTokenLabel(String authTokenType) {
		Log.i(Constants.TAG, TAG_PREFIX + "getAuthTokenLabel()");
        if (authTokenType.equals(Constants.AUTH_TOKEN_TYPE)) {
            return "Authenticator.getAuthTokenLabel";
        }
        return null;
	}

	@Override
	public Bundle hasFeatures(AccountAuthenticatorResponse response, Account account, String[] features) throws NetworkErrorException {
		Log.i(Constants.TAG, TAG_PREFIX + "hasFeature()");
		final Bundle result = new Bundle();
		result.putBoolean(AccountManager.KEY_BOOLEAN_RESULT, false);
		return result;
	}

	@Override
	public Bundle updateCredentials(AccountAuthenticatorResponse response, Account account, String authTokenType, Bundle options) {
		Log.i(Constants.TAG, TAG_PREFIX + "updateCredentials()");
		return null;
	}
	
    /**
     * Validates user's password on the server
     */
    private boolean onlineConfirmPassword(String username, String password) {
    	Log.i(Constants.TAG, TAG_PREFIX + "onlineConfirmPassword with username: " + username + " password: " + password);
    	return NetworkUtilities.authenticate(username, password, null/* Handler */, null/* Context */);
    }	
}