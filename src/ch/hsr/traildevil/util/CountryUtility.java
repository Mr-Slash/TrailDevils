package ch.hsr.traildevil.util;

import ch.hsr.traildevil.R;

//TODO when Server data is ready, parsing http://152.96.80.18:8080/api/trails/countries 
//and save countries in a list

public class CountryUtility {

	enum Country {
		AUSTRIA, GERMANY, SWITZERLAND, ITALY, LIECHTENSTEIN, FRANCE,
		CANADA, SLOVENIA, NORWAY, CZECHREPUBLIC, UNITEDSTATES, SWEDEN, 
		SPAIN, ANDORRA, NEWZEALAND, BELGIUM, PERU, POLAND;

		public String toString() {
			return name().toString().toLowerCase();
		}
	}

	public static int getResource(String c) {
		String country = c.toLowerCase().replace(" ", "");

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
		if (Country.CANADA.toString().equals(country))
			return R.drawable.canada;
		if (Country.SLOVENIA.toString().equals(country))
			return R.drawable.slovenia;
		if (Country.NORWAY.toString().equals(country))
			return R.drawable.norway;		
		if (Country.CZECHREPUBLIC.toString().equals(country))
			return R.drawable.czechrepublic;
		if (Country.UNITEDSTATES.toString().equals(country))
			return R.drawable.unitedstates;
		if (Country.SWEDEN.toString().equals(country))
			return R.drawable.sweden;
		if (Country.SPAIN.toString().equals(country))
			return R.drawable.spain;
		if (Country.ANDORRA.toString().equals(country))
			return R.drawable.andorra;
		if (Country.NEWZEALAND.toString().equals(country))
			return R.drawable.newzealand;
		if (Country.BELGIUM.toString().equals(country))
			return R.drawable.belgium;
		if (Country.PERU.toString().equals(country))
			return R.drawable.peru;
		if (Country.POLAND.toString().equals(country))
			return R.drawable.poland;		
		return 0;
	}
}
