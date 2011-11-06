package ch.hsr.traildevil.util.network;

import java.io.BufferedReader;
import java.io.IOException;

import android.util.Log;
import ch.hsr.traildevil.util.Constants;

public class WeatherService {

	private static final String TAG = Constants.TAG;
	private static final String TAG_PREFIX = WeatherService.class.getSimpleName() + ": ";
	private static String[] toReplaceList = { "Ä", "ä", "Ö", "ö", "Ü", "ü", "é", "è", "à", "â", " " };
	private static String[] replaceList = { "Ae", "ae", "Oe", "oe", "Ue", "ue", "e", "e", "a", "a", "" };
	private static HttpHandler httpHandler = new HttpHandler();
	private static final String PATTERN = "img src=";

	public static String getWeatherImageUrl(String nextCity) {
		String city = parse(nextCity);
		String url = null;
		try {
			url = getImageUrl(city);
		} catch (IOException e) {
			Log.e(TAG, TAG_PREFIX + "i/o problem fetching image url", e);
		}
		return url;
	}

	private static String getImageUrl(String city) throws IOException {
		BufferedReader in = openConnection(city);

		String line = null;
		while ((line = in.readLine()) != null) {
			if (line.contains(PATTERN)) {
				int startsAt = line.indexOf(PATTERN);
				line = line.substring(startsAt, line.length() - 9).replace(PATTERN + "\"", "");
				Log.i(TAG, TAG_PREFIX + city + "\t" + line);
				break;
			}
		}
		closeConnection(in);

		return line;
	}

	private static void closeConnection(BufferedReader in) throws IOException {
		in.close();
		httpHandler.resetStream();
	}

	private static BufferedReader openConnection(String city) {
		httpHandler.connectTo(getUrl(city), HttpHandler.TYPE_XML);
		BufferedReader in = new BufferedReader(httpHandler.getReader());
		return in;
	}

	private static String parse(String city) {
		for (int i = 0; i < toReplaceList.length; i++) {
			city = city.replaceAll(toReplaceList[i], replaceList[i]);
		}
		return city;
	}

	private static String getUrl(String city) {
		StringBuilder sb = new StringBuilder();
		sb.append("http://query.yahooapis.com/v1/public/yql?q=use%20'http%3A%2F%2Fgithub.com");
		sb.append("%2Fyql%2Fyql-tables%2Fraw%2Fmaster%2Fweather%2Fweather.bylocation.xml'");
		sb.append("%20as%20we%3Bselect%20*%20from%20we%20where%20location%3D%22" + city + "%22%20and%20unit%3D'c'");
		sb.append("&diagnostics=true&env=store%3A%2F%2Fdatatables.org%2Falltableswithkeys");
		return sb.toString();
	}
}
