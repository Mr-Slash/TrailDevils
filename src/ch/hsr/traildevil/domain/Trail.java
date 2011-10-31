package ch.hsr.traildevil.domain;

import java.io.Serializable;

public class Trail implements Serializable{

	private static final long serialVersionUID = -657192760929891505L;

	private Integer TrailId;
	private String NextCity;
	private Float GmapX;
	private Float GmapY;
	private String Name;
	private String Desc;
	private String Journey;
	private Boolean IsCommercial;
	private String State;
	private String Country;
	private Integer CountryId;
	private String Url;
	private String CreatedDate;
	private Integer Favorits;
	private String ImageUrl800;
	private String ImageUrl120;

	public Integer getTrailId() {
		return TrailId;
	}

	public String getNextCity() {
		return NextCity;
	}

	public Float getGmapX() {
		return GmapX;
	}

	public Float getGmapY() {
		return GmapY;
	}

	public String getName() {
		return Name;
	}

	public String getDesc() {
		return Desc;
	}

	public String getJourney() {
		return Journey;
	}

	public Boolean getIsCommercial() {
		return IsCommercial;
	}

	public String getState() {
		return State;
	}

	public String getCountry() {
		return Country;
	}

	public Integer getCountryId() {
		return CountryId;
	}

	public String getUrl() {
		return Url;
	}

	public String getCreatedDate() {
		return CreatedDate;
	}

	public Integer getFavorits() {
		return Favorits;
	}

	public String getImageUrl800() {
		return ImageUrl800;
	}

	public String getImageUrl120() {
		return ImageUrl120;
	}

	public void setTrailId(Integer trailId) {
		TrailId = trailId;
	}

	public void setNextCity(String nextCity) {
		NextCity = nextCity;
	}

	public void setGmapX(Float gmapX) {
		GmapX = gmapX;
	}

	public void setGmapY(Float gmapY) {
		GmapY = gmapY;
	}

	public void setName(String name) {
		Name = name;
	}

	public void setDesc(String desc) {
		Desc = desc;
	}

	public void setJourney(String journey) {
		Journey = journey;
	}

	public void setIsCommercial(Boolean isCommercial) {
		IsCommercial = isCommercial;
	}

	public void setState(String state) {
		State = state;
	}

	public void setCountry(String country) {
		Country = country;
	}

	public void setCountryId(Integer countryId) {
		CountryId = countryId;
	}

	public void setUrl(String url) {
		Url = url;
	}

	public void setCreatedDate(String createdDate) {
		CreatedDate = createdDate;
	}

	public void setFavorits(Integer favorits) {
		Favorits = favorits;
	}

	public void setImageUrl800(String imageUrl800) {
		ImageUrl800 = imageUrl800;
	}

	public void setImageUrl120(String imageUrl120) {
		ImageUrl120 = imageUrl120;
	}
}
