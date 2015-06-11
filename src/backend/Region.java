package backend;

import java.io.Serializable;

public class Region implements Serializable {

	private static final long serialVersionUID = 3104702860727863157L;
	
	private String name;
	private GeoLocation loc;
	
	public Region() {
		super();
	}

	public Region(String name, double lat, double lng) {
		super();
		this.name = name;
		this.loc = new GeoLocation(lat, lng);
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public GeoLocation getLoc() {
		return loc;
	}

	public void setLoc(GeoLocation loc) {
		this.loc = loc;
	}

	@Override
	public String toString() {
		return name + "; " + loc.getLatitude() + "; " + loc.getLongitude();
	}
	
}
