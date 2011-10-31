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

package ch.hsr.traildevil.syncadapter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.http.ParseException;
import org.apache.http.auth.AuthenticationException;
import org.json.JSONException;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.Context;
import android.content.SyncResult;
import android.os.Bundle;
import android.util.Log;
import ch.hsr.traildevil.domain.Trail;
import ch.hsr.traildevil.util.Constants;
import ch.hsr.traildevil.util.network.NetworkUtilities;

/**
 * SyncAdapter implementation for syncing TrailDevil data.
 */
public class SyncAdapter extends AbstractThreadedSyncAdapter {

	private static final String TAG_PREFIX = SyncAdapter.class.getSimpleName() + " ";
	
    private final AccountManager accountManager;
    private final Context context;

    /** In order to request just updates newer than this date */
    private Date lastUpdated;

    public SyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
        this.context = context;
        
        accountManager = AccountManager.get(context);
    }

    /**
     * Is invoked when a sync is requested. 
     */
	@Override
	public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {
		List<Trail> trails = new ArrayList<Trail>();
		
		String authenticationToken = null;
		
		try {
			// use the account manager to request the credentials
			authenticationToken = accountManager.blockingGetAuthToken(account, Constants.AUTH_TOKEN_TYPE, true /* notifyAuthFailure */);
			
			// fetch updates from the trail devils service over the internet
			trails = NetworkUtilities.fetchTrailUpdates(account, authenticationToken, lastUpdated);
			
			// update the last synced date.
			lastUpdated = new Date(); // use latest trail-update timestamp instead, since time may very on the device
			
			// update trails on db
			Log.d(Constants.TAG, "Calling contactManager's sync contacts");
			//TODO store trails in db and ?notify activities to update?
			
		} catch (OperationCanceledException e) {
			Log.e(Constants.TAG, TAG_PREFIX + "OperationCanceledException", e);
		} catch (AuthenticatorException e) {
			syncResult.stats.numParseExceptions++;
			Log.e(Constants.TAG, TAG_PREFIX + "AuthenticatorException", e);
		} catch (IOException e) {
			syncResult.stats.numIoExceptions++;
			Log.e(Constants.TAG, TAG_PREFIX + "IOException", e);
		} catch (AuthenticationException e) {
			accountManager.invalidateAuthToken(Constants.ACCOUNT_TYPE, authenticationToken);
            syncResult.stats.numAuthExceptions++;
            Log.e(Constants.TAG, TAG_PREFIX + "AuthenticationException", e);
		} catch (ParseException e) {
			syncResult.stats.numParseExceptions++;
			Log.e(Constants.TAG, TAG_PREFIX + "ParseException", e);
		} catch (JSONException e) {
			syncResult.stats.numParseExceptions++;
			Log.e(Constants.TAG, TAG_PREFIX + "JSONException", e);
		}
	}

}
