package myapps.solutions.huddil.model;

public class SavedFacility {

	Integer id;
	String facilityName;
	String buildingName;
	String city;
	String facilityType;

	public SavedFacility(Integer id) {
		this.id = id;
	}

	public SavedFacility(int id, String title, String cityName, String typeName, String name) {
		this.id = id;
		this.facilityName = title;
		this.buildingName = name;
		this.city = cityName;
		this.facilityType = typeName;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getFacilityName() {
		return facilityName;
	}

	public void setFacilityName(String facilityName) {
		this.facilityName = facilityName;
	}

	public String getBuildingName() {
		return buildingName;
	}

	public void setBuildingName(String buildingName) {
		this.buildingName = buildingName;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getFacilityType() {
		return facilityType;
	}

	public void setFacilityType(String facilityType) {
		this.facilityType = facilityType;
	}

}
