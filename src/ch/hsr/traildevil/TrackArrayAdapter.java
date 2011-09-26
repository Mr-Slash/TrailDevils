package ch.hsr.traildevil;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class TrackArrayAdapter extends ArrayAdapter<String> {
	private final Activity context;
	private final String[] names;

	public TrackArrayAdapter(Activity context, String[] names) {
		super(context, R.layout.tracklist_item, names);
		this.context = context;
		this.names = names;
	}


	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = context.getLayoutInflater();
		View rowView = inflater.inflate(R.layout.tracklist_item, null, true);

		ImageView imageView = (ImageView) rowView.findViewById(R.id.icon);
		TextView trackName = (TextView) rowView.findViewById(R.id.trackname);
		TextView morning = (TextView) rowView.findViewById(R.id.morning);
		TextView afternoon = (TextView) rowView.findViewById(R.id.afternoon);
		TextView status = (TextView) rowView.findViewById(R.id.status);

		//TODO Fetch data from db
		trackName.setText(names[position]);
		morning.setText("Vormittag: trocken");
		afternoon.setText("Nachmittag: nass");
		status.setText("offen");
		imageView.setImageResource(R.drawable.icon);

		return rowView;
	}
}
