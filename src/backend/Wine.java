package backend;

import java.io.Serializable;

public class Wine implements Serializable {

	private static final long serialVersionUID = -2112464206949166975L;

	private String name;
	private String maker;
	private String body;
	private String flavor;
	private String type;
	private String sugar;
	private String location;
	private Region region;

	public Wine() {
		super();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getMaker() {
		return maker;
	}

	public void setMaker(String maker) {
		this.maker = maker;
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

	public String getFlavor() {
		return flavor;
	}

	public void setFlavor(String flavor) {
		this.flavor = flavor;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getSugar() {
		return sugar;
	}

	public void setSugar(String sugar) {
		this.sugar = sugar;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}
	
	public Region getRegion() {
		return region;
	}

	public void setRegion(Region region) {
		this.region = region;
	}

	@Override
	public String toString() {
		return name;
	}
	
	/**
	 * Returns a string with full description.
	 */
	public String prettyPrint() {
		return "Name: " + name + "\nMaker: " + maker + "\nBody: " + body + "\nFlavor: " + 
				flavor + "\nType " + type + "\nSugar: " + sugar + "\nLocation: " + 
				(new String(location)).replace("/", ", ") +
				"\nRegion: " + region;
	}

}
