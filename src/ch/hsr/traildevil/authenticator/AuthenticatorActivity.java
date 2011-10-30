/*
 * Copyright (C) 2010 The Android Open Source Project
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package ch.hsr.traildevil.authenticator;

import android.accounts.Account;
import android.accounts.AccountAuthenticatorActivity;
import android.accounts.AccountAuthenticatorResponse;
import android.accounts.AccountManager;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;
import ch.hsr.traildevil.R;
import ch.hsr.traildevil.util.Constants;
import ch.hsr.traildevil.util.network.NetworkUtilities;

/**
 * Activity which displays login screen to the user.
 */
public class AuthenticatorActivity extends AccountAuthenticatorActivity {

	private static final String TAG_PREFIX = AuthenticatorActivity.class.getSimpleName() + ": ";
	
	/** The unique dialog id to identify the dialog to show/hide */ 
	private static final int DIALOG_PROGRESS_ID = 0;
	
	// Intent parameters which are passed to this activity when authentication failed or new 
	public static final String PARAM_PASSWORD 			= "password";
	public static final String PARAM_USERNAME 			= "username";
	public static final String PARAM_AUTHTOKEN_TYPE 	= "authtokenType";
	public static final String PARAM_CONFIRMCREDENTIALS = "confirmCredentials";
	
	/** Holds all accounts */
	private AccountManager accountManager;
	
	/** For posting authentication attempts back to the UI thread */
	private final Handler handler = new Handler();
	
	/** Performs the long running authentication process */
	private Thread authenticationThread;
	
	/** A unique identifier for the account authentication */
	private String authenticationTokenType;
	
	/** 
	 * Was the original caller asking for an entirely new account?
	 * If so, we add a new account, otherwise we just update the
	 * credentials.
	 */ 
	private boolean requestNewAccount; 
	
	/**
	 * If set, it means that a sync was requested but the credentials are not correct
	 * anymore, therefore the user gets redirected to the authorization activity in order to 
	 * update his credentials.
	 */
	private boolean confirmCredentials; 
	
	/** The account username */
	private String username;
	
	/** The account password (aka authentication token) */
	private String password;
	
	// UI fields
	private TextView messageView;
	private EditText usernameEdit;
	private EditText passwordEdit;
	
	@Override
	protected void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		
		accountManager = AccountManager.get(this);
		extractIntentParameters();
		initComponents();
	}
	
	/**
	 * Handles onClick event on the Submit button. Sends username/password to
	 * the server for authentication.
	 * 
	 * @param view
	 *            The Submit button for which this method is invoked
	 */
	public void handleLogin(View view) {
		Log.i(Constants.TAG, TAG_PREFIX + "handleLogin()");
		username = usernameEdit.getText().toString();
		password = passwordEdit.getText().toString();

		if (TextUtils.isEmpty(username) || TextUtils.isEmpty(password)) {
			messageView.setText(getMessage());
		} else {
			showDialog(DIALOG_PROGRESS_ID);
			
			// Start authenticating...
			authenticationThread = NetworkUtilities.attemptAuth(username, password, handler, this);
		}
	}	
	
	
	/**
	 * Callback method, when the authentication process completes.
	 * @see NetworkUtilities.sendResult()
	 */
	public void onAuthenticationResult(boolean success) {
		Log.i(Constants.TAG, TAG_PREFIX + "onAuthenticationResult(" + success + ")");
		dismissDialog(DIALOG_PROGRESS_ID); // Hide the progress dialog
		
		if (success) {
			if (confirmCredentials) { // in case that a sync was performed but the credentials are not correct anymore
				finishConfirmCredentials(true);
			} else { // default case, where a new account is added
				finishLogin();
			}
		} else {
			Log.e(Constants.TAG, TAG_PREFIX + "onAuthenticationResult: failed to authenticate");
			messageView.setText("Please enter valid username and password");
		}
	}	
	
	/**
	 * Adds a new account if no one exists or just updates the password of the
	 * account. Called when response is received from the server for authentication
	 * request. See onAuthenticationResult(). Sets the
	 * AccountAuthenticatorResult which is sent back to the caller. Also sets
	 * the authentication token in AccountManager for this account.
	 */
	protected void finishLogin() {
		Log.i(Constants.TAG, TAG_PREFIX + "finishLogin()");
		final Account account = new Account(username, Constants.ACCOUNT_TYPE);

		if (requestNewAccount) {
			accountManager.addAccountExplicitly(account, password, null);
			
			// TODO add accounts to sync => needs a sync adapter in order to work, i guess..
			//ContentResolver.setSyncAutomatically(account, ContactsContract.AUTHORITY, true);
		} else {
			accountManager.setPassword(account, password);
		}
		
		final Intent intent = new Intent();
		intent.putExtra(AccountManager.KEY_ACCOUNT_NAME, username);
		intent.putExtra(AccountManager.KEY_ACCOUNT_TYPE, Constants.ACCOUNT_TYPE);
		if (Constants.AUTH_TOKEN_TYPE.equals(authenticationTokenType)) { // is null when a new account is created
			intent.putExtra(AccountManager.KEY_AUTHTOKEN, password);
		}
		setAccountAuthenticatorResult(intent.getExtras());
		setResult(RESULT_OK, intent);
		finish();
	}	
	
	/**
	 * Called when response is received from the server for confirm credentials
	 * request. The credentials were incorrect therefore we have to update them. 
	 * See onAuthenticationResult(). Sets the AccountAuthenticatorResult 
	 * which is sent back to the caller.
	 * 
	 * @param result The confirmCredentials result.
	 */
	protected void finishConfirmCredentials(boolean result) {
		Log.i(Constants.TAG, TAG_PREFIX + "finishConfirmCredentials()");
		
		final Account account = new Account(username, Constants.ACCOUNT_TYPE);
		accountManager.setPassword(account, password);
		
		final Intent intent = new Intent();
		intent.putExtra(AccountManager.KEY_BOOLEAN_RESULT, result);
		setAccountAuthenticatorResult(intent.getExtras());
		setResult(RESULT_OK, intent);
		finish();
	}	
	
	/**
	 * Creates the Authenticator dialog.
	 * 
	 * @param id The unique dialog id, which identifies the dialog.
	 * @return The created dialog.
	 */
	@Override
	protected Dialog onCreateDialog(int id) {
		final ProgressDialog dialog = new ProgressDialog(this);
		dialog.setMessage("Authenticating");
		dialog.setIndeterminate(true);
		dialog.setCancelable(true);
		dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
			
			public void onCancel(DialogInterface dialog) {
				Log.i(Constants.TAG, TAG_PREFIX + "dialog cancel has been invoked");
				if (authenticationThread != null) {
					authenticationThread.interrupt();
					finish();
				}
			}
		});
		return dialog;
	}
	

	/**
	 * Extract Intent Parameters, which have been passed from a previous activity in order
	 * to pre-set some fields. 
	 */
	private void extractIntentParameters() {
		Intent intent = getIntent();
		username 				= intent.getStringExtra(PARAM_USERNAME);
		authenticationTokenType	= intent.getStringExtra(PARAM_AUTHTOKEN_TYPE);
		confirmCredentials 		= intent.getBooleanExtra(PARAM_CONFIRMCREDENTIALS, false);
		requestNewAccount 		= username == null; // when no username is set, it means that a new account is required
	}
	

	/**
	 * Initializes the layout components. E.g. re-sets the username field if it was already entered
	 */
	private void initComponents() {
		requestWindowFeature(Window.FEATURE_LEFT_ICON);
		setContentView(R.layout.login);
		getWindow().setFeatureDrawableResource(Window.FEATURE_LEFT_ICON, android.R.drawable.ic_dialog_alert);
		
		messageView = (TextView) findViewById(R.id.message);
		usernameEdit = (EditText) findViewById(R.id.username_edit);
		passwordEdit = (EditText) findViewById(R.id.password_edit);

		usernameEdit.setText(username);
		messageView.setText(getMessage());
	}

	/**
	 * Returns the message to be displayed at the top of the login dialog box. E.g. An information text at startup or
	 * a error text when a login attempt failed.

	 * @return The message to show
	 */
	private String getMessage() {
		// If no username, then we ask the user to log in using an
		// appropriate service.
		if (TextUtils.isEmpty(username)) {
			return "No username was set";
		}

		// We have an account but no password
		if (TextUtils.isEmpty(password)) {
			return "No password was set";
		}
		return "Type in your username & password";
	}

	//TODO Delete this
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		
		Account account = new Account("myUsername", "ch.hsr.traildevil.account");
		AccountManager am = AccountManager.get(this);
		boolean accountCreated = am.addAccountExplicitly(account, "myPassword", null);
		 
		Bundle extras = getIntent().getExtras();
		if (extras != null) {
		 if (accountCreated) {  //Pass the new account back to the account manager
		  AccountAuthenticatorResponse response = extras.getParcelable(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE);
		  Bundle result = new Bundle();
		  result.putString(AccountManager.KEY_ACCOUNT_NAME, "myUsername");
		  result.putString(AccountManager.KEY_ACCOUNT_TYPE, "myAccountType");
		  response.onResult(result);
		 }
		 finish();
		}
	}
}
