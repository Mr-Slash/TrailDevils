package ch.hsr.traildevil.util;

import ch.hsr.traildevil.R;

public class CountryUtility {

	enum Country {
		AUSTRIA, GERMANY, SWITZERLAND, ITALY, LIECHTENSTEIN, FRANCE;

		public String toString() {
			return name().toString().toLowerCase();
		}
	}

	public static int getResource(String c) {
		String country = c.toLowerCase();

		if (Country.AUSTRIA.toString().equals(country))
			return R.drawable.austria;
		if (Country.GERMANY.toString().equals(country))
			return R.drawable.germany;
		if (Country.SWITZERLAND.toString().equals(country))
			return R.drawable.switzerland;
		if (Country.ITALY.toString().equals(country))
			return R.drawable.italy;
		if (Country.LIECHTENSTEIN.toString().equals(country))
			return R.drawable.liechtenstein;
		if (Country.FRANCE.toString().equals(country))
			return R.drawable.france;
		return 0;	//TODO R.drawable.nocountry; 
	}
}
