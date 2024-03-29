package ch.hsr.traildevil.domain;

import java.io.Serializable;

public class Trail implements Serializable, Comparable<Trail> {

	private static final long serialVersionUID = -657192760929891505L;

	private int Id;
	private String NextCity;
	private float GmapX;
	private float GmapY;
	private String Name;
	private String Desc;
	private String IsOpen;
	private String Country;
	private long ModifiedUnixTs;
	private long DeletedUnixTs;
	private int Favorits;
	private String ImageUrl800;
	private String ImageUrl120;

	public int getId() {
		return Id;
	}

	public void setId(int id) {
		Id = id;
	}

	public String getNextCity() {
		return NextCity;
	}

	public void setNextCity(String nextCity) {
		NextCity = nextCity;
	}

	public float getGmapX() {
		return GmapX;
	}

	public void setGmapX(float gmapX) {
		GmapX = gmapX;
	}

	public float getGmapY() {
		return GmapY;
	}

	public void setGmapY(float gmapY) {
		GmapY = gmapY;
	}

	public String getName() {
		return Name;
	}

	public void setName(String name) {
		Name = name;
	}

	public String getDesc() {
		return Desc;
	}

	public void setDesc(String desc) {
		Desc = desc;
	}

	public String getIsOpen() {
		return IsOpen;
	}

	public void setIsOpen(String isOpen) {
		IsOpen = isOpen;
	}

	public String getCountry() {
		return Country;
	}

	public void setCountry(String country) {
		Country = country;
	}

	public long getModifiedUnixTs() {
		return ModifiedUnixTs;
	}

	public void setModifiedUnixTs(long modifiedUnixTs) {
		ModifiedUnixTs = modifiedUnixTs;
	}

	public long getDeletedUnixTs() {
		return DeletedUnixTs;
	}

	public void setDeletedUnixTs(long deletedUnixTs) {
		DeletedUnixTs = deletedUnixTs;
	}

	public int getFavorits() {
		return Favorits;
	}

	public void setFavorits(int favorits) {
		Favorits = favorits;
	}

	public String getImageUrl800() {
		return ImageUrl800;
	}

	public void setImageUrl800(String imageUrl800) {
		ImageUrl800 = imageUrl800;
	}

	public String getImageUrl120() {
		return ImageUrl120;
	}

	public void setImageUrl120(String imageUrl120) {
		ImageUrl120 = imageUrl120;
	}

	public boolean isDeleted() {
		return getDeletedUnixTs() > 0;
	}

	public int compareTo(Trail another) {
		return Integer.valueOf(another.getFavorits()).compareTo(getFavorits());
	}
	
	public String toString() {
		return getName();
	}
}
