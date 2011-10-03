package ch.hsr.traildevil.util;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.OverlayItem;

public class POIOverlayItem extends OverlayItem{

	
	public POIOverlayItem(double latitude, double longitude, String title, String snippet) {
		this(new GeoPoint( (int)(latitude * 10E6), (int) (longitude * 10E6)), title, snippet);
	}
	
	public POIOverlayItem(GeoPoint location, String title, String snippet){
		super(location, title, snippet);
	}
}
