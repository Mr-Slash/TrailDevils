package ch.hsr.traildevil;

import java.util.ArrayList;
import java.util.List;

import android.app.ListActivity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;
import ch.hsr.traildevil.application.TrailDevilsController;
import ch.hsr.traildevil.domain.Trail;

public class TrailDevilsActivity extends ListActivity {

	TrailDevilsController controller;
	
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
}