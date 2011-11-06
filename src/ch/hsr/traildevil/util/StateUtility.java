package ch.hsr.traildevil.util;

import android.widget.TextView;
import ch.hsr.traildevil.R;

public class StateUtility {

	enum State {
		NEW, NOK, OK;

		public String toString() {
			return name().toString().toLowerCase();
		}
	}

	public static void setState(TextView view, String state) {
		if (State.OK.toString().equals(state)) {
			view.setText(R.string.state_open);
			view.setTextColor(view.getResources().getColor(R.color.dark_green));
		}
		if (State.NEW.toString().equals(state)) {
			view.setText(R.string.state_new);
			view.setTextColor(view.getResources().getColor(R.color.dark_grey));
		}
		if (State.NOK.toString().equals(state)) {
			view.setText(R.string.state_closed);
			view.setTextColor(view.getResources().getColor(R.color.dark_red));
		}
	}
}
