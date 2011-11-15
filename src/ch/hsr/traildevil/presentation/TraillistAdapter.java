package ch.hsr.traildevil.presentation;

import java.util.List;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import ch.hsr.traildevil.R;
import ch.hsr.traildevil.domain.Trail;
import ch.hsr.traildevil.util.Constants;
import ch.hsr.traildevil.util.CountryUtility;
import ch.hsr.traildevil.util.StateUtility;
import ch.hsr.traildevil.util.network.ImageDownloader;

public class TraillistAdapter extends ArrayAdapter<Trail> {

	private Trail trail;
	private final LayoutInflater inflator;
	private ViewHolder holder;
	private int resource;
	private int maxFav;

	public TraillistAdapter(Activity context, int resource, List<Trail> trails, int favorites) {
		super(context, resource, trails);
		this.resource = resource;
		this.maxFav = favorites;
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
		holder.tracknameView = (TextView) rowView.findViewById(R.id.trackname);
		holder.favoritesRatingbar = (RatingBar) rowView.findViewById(R.id.traillist_ratingbar);
		holder.favoritesRatingbar.setNumStars(Constants.MAX_FAVORITE_STARS);
		holder.statusView = (TextView) rowView.findViewById(R.id.status);
		holder.iconView = (ImageView) rowView.findViewById(R.id.icon);
		holder.countryView = (ImageView) rowView.findViewById(R.id.traillist_country);
	}

	private void updateViews() {
		holder.tracknameView.setText(trail.getName());
		holder.favoritesRatingbar.setRating(getRating());
		StateUtility.setState(holder.statusView, trail.getIsOpen());
		holder.countryView.setImageResource(CountryUtility.getResource(trail.getCountry()));
		ImageDownloader.Instance.loadDrawable(trail.getImageUrl120(), holder.iconView, R.drawable.nophotosmall);
	}

	private float getRating() {
		return (trail.getFavorits() < 1) ? 0 : (((float) trail.getFavorits() / maxFav) * Constants.MAX_FAVORITE_STARS);
	}

	private static class ViewHolder {
		public ImageView iconView;
		public ImageView countryView;
		public TextView tracknameView;
		public RatingBar favoritesRatingbar;
		public TextView statusView;
	}
}
