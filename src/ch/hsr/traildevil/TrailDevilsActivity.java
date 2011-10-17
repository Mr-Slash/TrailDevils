package ch.hsr.traildevil;

import java.util.ArrayList;
import java.util.List;

import android.app.Dialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;
import ch.hsr.traildevil.application.TrailDevilsController;
import ch.hsr.traildevil.domain.Trail;

public class TrailDevilsActivity extends ListActivity {

	private static final String TAG = "traildevil";
	private static final String TAG_PREFIX = TrailDevilsActivity.class.getSimpleName() + ": ";
	private static final int DIALOG_PROGRESS_ID = 0;
	
	private TrailDevilsController controller;
	private Thread syncThread;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.tracklist);
		
		controller = new TrailDevilsController(getDir("data", Context.MODE_PRIVATE).toString());

		List<Trail> trails = new ArrayList<Trail>();
		trails.add(controller.getTrail(0));
		trails.add(controller.getTrail(1));
		trails.add(controller.getTrail(2));
		trails.add(controller.getTrail(3));
		trails.add(controller.getTrail(4));

		setListAdapter(new TrailsAdapter(this, R.layout.tracklist_item, trails));
		handle(getIntent());
		
		showDialog(DIALOG_PROGRESS_ID);
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		controller.close();
		controller = null;
	}

	@Override
	protected void onNewIntent(Intent intent) {
		setIntent(intent);
		handle(intent);
	}

	private void handle(Intent intent) {
		if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
			String query = intent.getStringExtra(SearchManager.QUERY);
			// doSearch(query);
			Toast.makeText(getApplicationContext(), "TEST " + query, Toast.LENGTH_SHORT).show();
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
		Intent detail = new Intent(this, DetailActivity.class);
		detail.putExtra("trailName", trail.getName());
		detail.putExtra("trailPosition", position);
		startActivity(detail);
	}
	
	/**
	 * Show a dialog with a progress bar. The user is informed, that the local data
	 * is updated.
	 */
	@Override
	protected Dialog onCreateDialog(int id) {
		final ProgressDialog dialog = new ProgressDialog(this);
		dialog.setMessage("Check if new data is available..");
		dialog.setIndeterminate(true);
		dialog.setCancelable(true);
		dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
			public void onCancel(DialogInterface dialog) {
				Log.i(TAG, TAG_PREFIX + "Dialog cancel has been invoked");
				if (syncThread != null) {
					syncThread.interrupt();
					finish();
				}
			}
		});
		dialog.setButton(Dialog.BUTTON_NEGATIVE, "Cancel", new OnClickListener() {
			
			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();
			}
		});
		return dialog;
	}	
}