package ch.hsr.traildevil;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import android.app.ListActivity;
import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;
import ch.hsr.traildevil.domain.Trail;
import ch.hsr.traildevil.util.HttpHandler;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

public class TrailDevilsActivity extends ListActivity {

	private List<Trail> trails;
	private static final String webServiceUrl = "http://152.96.80.18:8080/api/trails/1";
	private HttpHandler webHandler;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.tracklist);

		init();
		fillOneTrail();
		// fillAllTrails();

		setListAdapter(new TrailsAdapter(this, R.layout.tracklist_item, trails));
		handle(getIntent());
	}

	private void fillOneTrail() {
		webHandler.connectTo(webServiceUrl);
		trails.add(new Gson().fromJson(webHandler.getReader(), Trail.class));
		webHandler.resetStream();
	}

	private void fillAllTrails() {
		webHandler.connectTo(webServiceUrl);
		JsonElement json = new JsonParser().parse(webHandler.getReader());
		Iterator<JsonElement> iterator = json.getAsJsonArray().iterator();
		Gson gson = new Gson();

		while (iterator.hasNext()) {
			trails.add(gson.fromJson((JsonElement) iterator.next(), Trail.class));
		}
		webHandler.resetStream();
	}

	private void init() {
		trails = new ArrayList<Trail>();
		webHandler = new HttpHandler();
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