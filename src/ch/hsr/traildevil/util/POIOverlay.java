package ch.hsr.traildevil.util;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import android.graphics.drawable.Drawable;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.OverlayItem;

/**
 * Add a list of OverlayItems, which will be displayed by a map.
 * 
 * @author Sandro
 */
public class POIOverlay extends ItemizedOverlay {

	private List<OverlayItem> locations = null;
	private GeoPoint center = null;

	public POIOverlay(Drawable marker, OverlayItem ...overlayItems) {
		super(boundCenterBottom(marker));
		
		locations = Arrays.asList(overlayItems);
		populate(); 
	}

	/**
	 * We added this method to find the middle point of the cluster Start each
	 * edge on its opposite side and move across with each point. The top of the
	 * world is +90, the bottom -90, the west edge is -180, the east +180
	 * 
	 * @return
	 */
	public GeoPoint getCenterPoint() {
		if (center == null) {
			int northEdge = -90000000;
			int southEdge = 90000000;
			int eastEdge = -180000000;
			int westEdge = 180000000;
			
			Iterator<OverlayItem> iter = locations.iterator();
			while (iter.hasNext()) {
				GeoPoint pt = iter.next().getPoint();
				if (pt.getLatitudeE6() > northEdge)
					northEdge = pt.getLatitudeE6();
				if (pt.getLatitudeE6() < southEdge)
					southEdge = pt.getLatitudeE6();
				if (pt.getLongitudeE6() > eastEdge)
					eastEdge = pt.getLongitudeE6();
				if (pt.getLongitudeE6() < westEdge)
					westEdge = pt.getLongitudeE6();
			}
			center = new GeoPoint((int) ((northEdge + southEdge) / 2), (int) ((westEdge + eastEdge) / 2));
		}
		
		return center;
	}

	@Override
	protected OverlayItem createItem(int i) {
		return locations.get(i);
	}

	@Override
	public int size() {
		return locations.size();
	}

}
