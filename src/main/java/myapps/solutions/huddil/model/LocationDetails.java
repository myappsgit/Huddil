package myapps.solutions.huddil.model;

public class LocationDetails {

	private int id;
	private String city;
	private String localityName;
	private String locationName;
	private String address;
	private String landmark;
	private String nearBy;
	private String description;

	public LocationDetails(int id, String city, String localityName, String locationName, String address,
			String landmark, String nearBy, String description) {
		this.id = id;
		this.city = city;
		this.localityName = localityName;
		this.locationName = locationName;
		this.address = address;
		this.landmark = landmark;
		this.nearBy = nearBy;
		this.description = description;
	}

	public LocationDetails(int id) {
		this.id= id;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getLocalityName() {
		return localityName;
	}

	public void setLocalityName(String localityName) {
		this.localityName = localityName;
	}

	public String getLocationName() {
		return locationName;
	}

	public void setLocationName(String locationName) {
		this.locationName = locationName;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getLandmark() {
		return landmark;
	}

	public void setLandmark(String landmark) {
		this.landmark = landmark;
	}

	public String getNearBy() {
		return nearBy;
	}

	public void setNearBy(String nearBy) {
		this.nearBy = nearBy;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

}
