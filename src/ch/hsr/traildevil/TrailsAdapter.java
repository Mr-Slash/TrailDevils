package ch.hsr.traildevil;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import ch.hsr.traildevil.domain.Trail;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class TrailsAdapter extends ArrayAdapter<Trail> {
	private int resource;
	private View rowView;
	private Trail trail;
	private ImageView imageView;
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
		bindToViews();

		return rowView;
	}

	private void initViews() {
		imageView = (ImageView) rowView.findViewById(R.id.icon);
		trackName = (TextView) rowView.findViewById(R.id.trackname);
		morning = (TextView) rowView.findViewById(R.id.morning);
		afternoon = (TextView) rowView.findViewById(R.id.afternoon);
		status = (TextView) rowView.findViewById(R.id.status);
	}

	private void bindToViews() {
		trackName.setText(trail.getName());
		morning.setText("Vormittag: trocken");	//TODO Live weather?
		afternoon.setText("Nachmittag: nass");	//TODO Live weather?
		status.setText(trail.getState());

		try {
			InputStream is = new URL(trail.getImageUrl120()).openStream();
			imageView.setImageDrawable(Drawable.createFromStream(is, "src"));
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
