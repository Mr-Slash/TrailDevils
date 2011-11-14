package ch.hsr.traildevil.util;

import android.widget.TextView;
import ch.hsr.traildevil.R;

public class StateUtility {

	public static void setState(TextView view, String state) {
		if ("true".equals(state)) {
			view.setText(R.string.state_open);
			view.setTextColor(view.getResources().getColor(R.color.dark_green));
		} else {
			view.setText(R.string.state_closed);
			view.setTextColor(view.getResources().getColor(R.color.dark_red));
		}
	}
}
