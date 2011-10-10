package ch.hsr.traildevil;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import ch.hsr.traildevil.util.POIOverlayItem;
import ch.hsr.traildevil.util.POIOverlay;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.OverlayItem;

public class DetailActivity extends MapActivity {

	private MapView mapView;	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.detail);
		
		mapView = (MapView) findViewById(R.id.detailview_mapview);
		mapView.setBuiltInZoomControls(true);
		
		// create and add POI's
		Drawable marker = getResources().getDrawable(R.drawable.map_marker);
		POIOverlayItem magicKindom = new POIOverlayItem(28.418971, -81.581436, "Disney Magic Kindom", "Disney Magic Kindom");
		POIOverlayItem sevenLagoon = new POIOverlayItem(28.410067, -81.583699 , "Disney Seven Lagoon", "Disney Seven Lagoon");

		POIOverlay overlay = new POIOverlay(marker, magicKindom, sevenLagoon);
		mapView.getOverlays().add(overlay);

		int latSpan = overlay.getLatSpanE6();
		int lonSpan = overlay.getLonSpanE6();
		
		MapController controller = mapView.getController();
		controller.setCenter(overlay.getCenterPoint());
		controller.zoomToSpan((int)(latSpan*1.5), (int) (lonSpan*1.5));
		
		handleIntent(getIntent());
	}
	
	private void handleIntent(Intent intent) {
		String name = intent.getStringExtra("trailName");
		int trailId = intent.getIntExtra("trailId", 0);
		setTitle(name);
		//TODO Fill the Views of this activity with the data received from the database
		//TextView status = (TextView) findViewById(R.id.afternoon);
		//status.setText(traildata.getTrailName());
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
