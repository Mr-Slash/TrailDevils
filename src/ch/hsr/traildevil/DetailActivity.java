package ch.hsr.traildevil;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import ch.hsr.traildevil.application.TrailDevilsController;
import ch.hsr.traildevil.domain.Trail;
import ch.hsr.traildevil.util.CountryUtility;
import ch.hsr.traildevil.util.HttpHandler;
import ch.hsr.traildevil.util.POIOverlay;
import ch.hsr.traildevil.util.POIOverlayItem;

import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;

public class DetailActivity extends MapActivity {

	private MapView mapView;	
	private ImageView trailLogo, trailCountry;
	private TextView trailStatus;
	private TrailDevilsController appController;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.detail);
		appController = new TrailDevilsController(getDir("data", Context.MODE_PRIVATE).toString(), getApplicationContext());
		initViews();
		createGoogleMapView();
		handleIntent(getIntent());
	}

	private void initViews() {
		trailLogo = (ImageView) findViewById(R.id.detailview_logo);
		trailCountry = (ImageView) findViewById(R.id.detailview_country);
		trailStatus = (TextView) findViewById(R.id.detailview_status);
		mapView = (MapView) findViewById(R.id.detailview_mapview);
	}

	private void createGoogleMapView() {
		// create and add POI's
		Drawable marker = getResources().getDrawable(R.drawable.map_marker);
		POIOverlayItem magicKindom = new POIOverlayItem(28.418971, -81.581436, "Disney Magic Kindom", "Disney Magic Kindom");
		POIOverlayItem sevenLagoon = new POIOverlayItem(28.410067, -81.583699 , "Disney Seven Lagoon", "Disney Seven Lagoon");

		POIOverlay overlay = new POIOverlay(marker, magicKindom, sevenLagoon);
		mapView.getOverlays().add(overlay);
		mapView.setBuiltInZoomControls(true);

		int latSpan = overlay.getLatSpanE6();
		int lonSpan = overlay.getLonSpanE6();
		
		MapController mapController = mapView.getController();
		mapController.setCenter(overlay.getCenterPoint());
		mapController.zoomToSpan((int)(latSpan*1.5), (int) (lonSpan*1.5));
	}
	
	private void handleIntent(Intent intent) {
		int trailPosition = intent.getIntExtra("trailPosition", 0);
		updateViews(appController.getTrail(trailPosition));
	}

	private void updateViews(Trail trail) {
		setTitle(trail.getName());
		trailLogo.setImageDrawable(HttpHandler.getHttpImage(trail.getImageUrl120()));
		trailCountry.setImageResource(CountryUtility.getResource(trail.getCountry()));
		trailStatus.setText(trail.getState());
	}
	
	@Override
	protected boolean isLocationDisplayed() {
		return false;
	}

	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}
}
