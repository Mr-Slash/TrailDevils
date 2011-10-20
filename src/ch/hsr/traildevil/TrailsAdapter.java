package ch.hsr.traildevil;

import java.util.List;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import ch.hsr.traildevil.domain.Trail;
import ch.hsr.traildevil.util.CountryUtility;
import ch.hsr.traildevil.util.HttpHandler;

public class TrailsAdapter extends ArrayAdapter<Trail> {
	private int resource;
	private View rowView;
	private Trail trail;
	private ImageView iconView, countryView;
	private TextView trackName;
	private TextView morning;
	private TextView afternoon;
	private TextView status;
	
	public TrailsAdapter(Activity context, int resource, List<Trail> trails) {
		super(context, resource, trails);
		this.resource = resource;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = ((Activity) super.getContext()).getLayoutInflater();
		rowView = inflater.inflate(resource, null, true);
		trail = getItem(position);
		initViews();
		updateViews();

		return rowView;
	}

	private void initViews() {
		iconView = (ImageView) rowView.findViewById(R.id.icon);
		countryView = (ImageView) rowView.findViewById(R.id.traillist_country);
		trackName = (TextView) rowView.findViewById(R.id.trackname);
		morning = (TextView) rowView.findViewById(R.id.morning);
		afternoon = (TextView) rowView.findViewById(R.id.afternoon);
		status = (TextView) rowView.findViewById(R.id.status);
	}

	private void updateViews() {
		trackName.setText(trail.getName());
		morning.setText("Vormittag: trocken");	//TODO Live weather?
		afternoon.setText("Nachmittag: nass");	//TODO Live weather?
		status.setText(trail.getState());
		iconView.setImageDrawable(HttpHandler.getHttpImage(trail.getImageUrl120(), getContext().getResources()));
		countryView.setImageResource(CountryUtility.getResource(trail.getCountry()));
	}		

}
