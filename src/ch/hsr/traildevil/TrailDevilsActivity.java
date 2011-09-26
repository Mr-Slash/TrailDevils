package ch.hsr.traildevil;

import android.app.ListActivity;
import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

public class TrailDevilsActivity extends ListActivity {
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.tracklist);
		
		String[] tracks = new String[] { "Monte Tamaro", "Lac Blanc", "Todtnau", "San Bernardino", "Porte du Soleil", "Livigno" };
		
		//TODO mit SimpleCursorAdapter ersetzen
		setListAdapter(new TrackArrayAdapter(this, tracks));
		
		handleIntent(getIntent());
	}

	@Override
	protected void onNewIntent(Intent intent) {
		setIntent(intent);
		handleIntent(intent);
	}

	private void handleIntent(Intent intent) {
		if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
			String query = intent.getStringExtra(SearchManager.QUERY);
			// doSearch(query);
			Toast.makeText(getApplicationContext(), "TEST " + query, Toast.LENGTH_SHORT).show();
		}
	}

}