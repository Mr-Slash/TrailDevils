package ch.hsr.traildevil;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import ch.hsr.traildevil.domain.Trail;
import ch.hsr.traildevil.util.CountryUtility;
import ch.hsr.traildevil.util.ImageDownloader;

public class TrailsAdapter extends ArrayAdapter<Trail> {

	private int resource;
	private Trail trail;
	private final LayoutInflater inflator;
	private ViewHolder holder;
	private Context context;

	public TrailsAdapter(Activity context, int resource, List<Trail> trails) {
		super(context, resource, trails);
		this.resource = resource;
		
		this.context = context.getApplicationContext();
		inflator = context.getLayoutInflater();
	}

	@Override
	public View getView(int position, View rowView, ViewGroup parent) {

		if (rowView == null) {
			rowView = inflator.inflate(resource, null);
			holder = new ViewHolder();
			initViews(rowView);
			rowView.setTag(holder);
		} else {
			holder = (ViewHolder) rowView.getTag();
		}

		trail = getItem(position);
		updateViews();

		return rowView;
	}

	private void initViews(View rowView) {
		holder.trackName = (TextView) rowView.findViewById(R.id.trackname);
		holder.morning = (TextView) rowView.findViewById(R.id.morning);
		holder.afternoon = (TextView) rowView.findViewById(R.id.afternoon);
		holder.status = (TextView) rowView.findViewById(R.id.status);
		holder.iconView = (ImageView) rowView.findViewById(R.id.icon);
		holder.countryView = (ImageView) rowView.findViewById(R.id.traillist_country);
	}

	private void updateViews() {
		holder.trackName.setText(trail.getName());
		holder.morning.setText("Vormittag: trocken"); // TODO Live weather?
		holder.afternoon.setText("Nachmittag: nass"); // TODO Live weather?
		holder.status.setText(trail.getState());
		holder.countryView.setImageResource(CountryUtility.getResource(trail.getCountry()));
		ImageDownloader.Instance.loadDrawable(trail.getImageUrl120(), holder.iconView, R.drawable.photo_not_available, context);
	}

	private static class ViewHolder {
		public ImageView iconView;
		public ImageView countryView;
		public TextView trackName;
		public TextView morning;
		public TextView afternoon;
		public TextView status;
	}
}
