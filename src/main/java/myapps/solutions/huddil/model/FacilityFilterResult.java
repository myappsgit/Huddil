package myapps.solutions.huddil.model;

public class FacilityFilterResult {

	private int id;
	private String title;
	private String description;
	private int capacity;
	private double latitude;
	private double longtitude;
	private double costPerHour;
	private double costPerDay;
	private double costPerMonth;
	private double averageRating;
	private int size;
	private int status;
	private String contactNo;
	private String alternateContactNo;
	private String emailId;
	private String alternateEmailId;
	private String thumbnail;
	private String typeName;
	private String city;
	private String locality;
	private String locationName;
	private String landmark;
	private String address;
	private String nearBy;
	private String Amenities;
	private String imgPath;
	public FacilityFilterResult() {

	}

	public FacilityFilterResult(int id, String title, String description, int capacity, double latitude,
			double longtitude, double costPerHour, double costPerDay, double costPerMonth, double averageRating,
			int size, int status, String contactNo, String alternatecontactNo, String emailId, String alternateEmailId,
			String thumbnail, String typeName, String city, String locality, String locationName, String landmark,
			String address, String nearBy, String Amenities,
			String imgPath/* , int favorite */) {
		this.id = id;
		this.title = title;
		this.description = description;
		this.capacity = capacity;
		this.latitude = latitude;
		this.longtitude = longtitude;
		this.costPerHour = costPerHour;
		this.costPerDay = costPerDay;
		this.costPerMonth = costPerMonth;
		this.averageRating = averageRating;
		this.size = size;
		this.status = status;
		this.contactNo = contactNo;
		this.alternateContactNo = alternatecontactNo;
		this.emailId = emailId;
		this.alternateEmailId = alternateEmailId;
		this.thumbnail = thumbnail;
		this.typeName = typeName;
		this.city = city;
		this.locality = locality;
		this.locationName = locationName;
		this.landmark = landmark;
		this.address = address;
		this.nearBy = nearBy;
		this.Amenities = Amenities;
		this.imgPath = imgPath;
		/* this.favorite = favorite; */
	}

	public FacilityFilterResult(int id) {
		this.id = id;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public int getCapacity() {
		return capacity;
	}

	public void setCapacity(int capacity) {
		this.capacity = capacity;
	}

	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public double getLongtitude() {
		return longtitude;
	}

	public void setLongtitude(double longtitude) {
		this.longtitude = longtitude;
	}

	public double getCostPerHour() {
		return costPerHour;
	}

	public void setCostPerHour(double costPerHour) {
		this.costPerHour = costPerHour;
	}

	public double getCostPerDay() {
		return costPerDay;
	}

	public void setCostPerDay(double costPerDay) {
		this.costPerDay = costPerDay;
	}

	public double getCostPerMonth() {
		return costPerMonth;
	}

	public void setCostPerMonth(double costPerMonth) {
		this.costPerMonth = costPerMonth;
	}

	public double getAverageRating() {
		return averageRating;
	}

	public void setAverageRating(double averageRating) {
		this.averageRating = averageRating;
	}

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getContactNo() {
		return contactNo;
	}

	public void setContactNo(String contactNo) {
		this.contactNo = contactNo;
	}

	public String getAlternateContactNo() {
		return alternateContactNo;
	}

	public void setAlternateContactNo(String alternateContactNo) {
		this.alternateContactNo = alternateContactNo;
	}

	public String getTypeName() {
		return typeName;
	}

	public void setTypeName(String typeName) {
		this.typeName = typeName;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getLocality() {
		return locality;
	}

	public void setLocality(String locality) {
		this.locality = locality;
	}

	public String getLocationName() {
		return locationName;
	}

	public void setLocationName(String locationName) {
		this.locationName = locationName;
	}

	public String getLandmark() {
		return landmark;
	}

	public void setLandmark(String landmark) {
		this.landmark = landmark;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getNearBy() {
		return nearBy;
	}

	public void setNearBy(String nearBy) {
		this.nearBy = nearBy;
	}

	public String getAmenities() {
		return Amenities;
	}

	public void setAmenities(String Amenities) {
		this.Amenities = Amenities;
	}

	public String getImgPath() {
		return imgPath;
	}

	public void setImgPath(String imgPath) {
		this.imgPath = imgPath;
	}

	public String getEmailId() {
		return emailId;
	}

	public void setEmailId(String emailId) {
		this.emailId = emailId;
	}

	public String getAlternateEmailId() {
		return alternateEmailId;
	}

	public void setAlternateEmailId(String alternateEmailId) {
		this.alternateEmailId = alternateEmailId;
	}

	public String getThumbnail() {
		return thumbnail;
	}

	public void setThumbnail(String thumbnail) {
		this.thumbnail = thumbnail;
	}	
}
