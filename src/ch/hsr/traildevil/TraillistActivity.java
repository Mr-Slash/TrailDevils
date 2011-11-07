package ch.hsr.traildevil;

import java.util.List;

import android.app.Activity;
import android.app.Dialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;
import ch.hsr.traildevil.application.Controller;
import ch.hsr.traildevil.domain.Trail;
import ch.hsr.traildevil.util.Constants;

public class TraillistActivity extends ListActivity {

	private static final String TAG_PREFIX = TraillistActivity.class.getSimpleName() + ": ";
	public static final int DIALOG_PROGRESS_ID = 0;

	private Controller controller;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.tracklist);
		controller = new Controller();

		if (controller.isNetworkAvailable()) {
			controller.startSynchronization(this);
		}else{
			// TODO It would be nicer to show an initial empty list and a dialog box which says that the old data
			// 		is loaded. Data loading should happen in a async task, so that the dialog could be shown before.
			displayTrailData();
			Toast infoToast = Toast.makeText(this, "Internet connection unavailable. Old Data is displayed.", Toast.LENGTH_LONG);
			infoToast.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL , 0, 0);
			infoToast.show();
		}
	}
	
	/**
	 * This method is invoked by the Async Task when the Data synchronization has completed.
	 */
	public void syncCompleted(){
		displayTrailData();
		removeDialog(DIALOG_PROGRESS_ID);
	}

	/**
	 * This method is invoked by the Dialog when the synchronization is canceled by the user.
	 */
	public void syncAborted(){
		displayTrailData();
		removeDialog(DIALOG_PROGRESS_ID);
	}
	
	/**
	 * Load Trail data from db and display 
	 */
	private void displayTrailData() {
		List<Trail> trails = controller.getTrails();
		setListAdapter(new TraillistAdapter(this, R.layout.tracklist_item, trails, controller.getMaxFavorits()));
	}
	
	@Override
	protected void onNewIntent(Intent intent) {
		setIntent(intent);

		if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
			String query = intent.getStringExtra(SearchManager.QUERY);
			getListView().setFilterText(query);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.tracklist_menu, menu);
		return true;
	}

	@Override
	protected void onListItemClick(ListView listView, View view, int position, long id) {
		super.onListItemClick(listView, view, position, id);
		Trail trail = (Trail) listView.getAdapter().getItem(position);
		Intent detail = new Intent(this, TrailActivity.class);
		detail.putExtra("trailName", trail.getName());
		detail.putExtra("trailPosition", position);
		startActivity(detail);
	}

	/**
	 * Show a dialog with a progress bar. The user is informed, that the local
	 * data is updated.
	 */
	@Override
	protected Dialog onCreateDialog(int id) {
		
		if(id == DIALOG_PROGRESS_ID){
			final ProgressDialog dialog = new ProgressDialog(this);
			dialog.setTitle("Synchronizing Data");
			dialog.setMessage("Loading...");
			dialog.setIndeterminate(true);
			dialog.setCancelable(false); // Back Button not supported to cancel dialog
			dialog.setButton(Dialog.BUTTON_NEGATIVE, "Cancel", new OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					controller.stopSynchronization();
				}
			});
			return dialog;
		}
		return null;
	}
	
	/**
	 * Returns the latest modified timestamp of all trail. This timestamp could be 
	 * used to check for online updates. Note that this value is read as shared property.
	 * 
	 * @return The latest timestamp or 0 if no one exists.
	 */
	public long getLastModifiedTimestamp(){
		SharedPreferences preferences = getPreferences(Activity.MODE_PRIVATE);
		return preferences.getLong(Constants.LAST_MODIFIED_TIMESTAMP_KEY, 0);
	}	
	
	/**
	 * Saves the latest modified timestamp of all trails as shared property.
	 * @param timestamp The latest timestamp to set.
	 */
	public void setLastModifiedTimestamp(long timestamp){
		SharedPreferences preferences = getPreferences(Activity.MODE_PRIVATE);
		
		Editor editor = preferences.edit();
		editor.putLong(Constants.LAST_MODIFIED_TIMESTAMP_KEY, timestamp);
		editor.commit();
	}
}