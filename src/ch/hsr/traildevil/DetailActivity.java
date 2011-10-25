package ch.hsr.traildevil;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import ch.hsr.traildevil.application.TrailDevilsController;
import ch.hsr.traildevil.domain.Trail;
import ch.hsr.traildevil.util.CountryUtility;
import ch.hsr.traildevil.util.ImageDownloader;
import ch.hsr.traildevil.util.POIOverlay;
import ch.hsr.traildevil.util.POIOverlayItem;

import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;

public class DetailActivity extends MapActivity {

	private MapView mapView;	
	private ImageView trailLogo;
	private TextView trailStatus;
	private TextView trailDesc;
	private TrailDevilsController appController;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.detail);
		
		initComponents();
		handleIntent(getIntent());
	}

	/**
	 * Initializes the components
	 */
	private void initComponents() {
		
		appController = new TrailDevilsController();
		
		trailLogo = (ImageView) findViewById(R.id.detailview_logo);
		trailStatus = (TextView) findViewById(R.id.detailview_status);
		trailDesc = (TextView) findViewById(R.id.detailview_description);
		mapView = (MapView) findViewById(R.id.detailview_mapview);
	}

	private void handleIntent(Intent intent) {
		int trailPosition = intent.getIntExtra("trailPosition", 0);
		updateViews(appController.getTrail(trailPosition));
	}

	private void updateViews(Trail trail) {
		setTitle(trail.getName());
		ImageDownloader.Instance.loadDrawable(trail.getImageUrl800(), trailLogo, R.drawable.photo_not_available, getApplicationContext());
		trailStatus.setText(trail.getState());
		trailDesc.setText(trail.getDesc());
		
		createGoogleMapView(trail);
	}
	
	private void createGoogleMapView(Trail trail) {
		// create and add POI's
		Drawable marker = getResources().getDrawable(R.drawable.map_marker);
		POIOverlayItem trailLocation = new POIOverlayItem(trail.getGmapX(), trail.getGmapY(), trail.getName(), trail.getName());
		
		POIOverlay overlay = new POIOverlay(marker, trailLocation);
		mapView.getOverlays().add(overlay);
		mapView.setBuiltInZoomControls(true);

		MapController mapController = mapView.getController();
		mapController.setCenter(overlay.getCenterPoint());
		mapController.setZoom(17);
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
