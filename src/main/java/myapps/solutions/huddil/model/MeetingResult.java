package myapps.solutions.huddil.model;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

public class MeetingResult {

	private int id;
	private String title;
	private String description;
	private String participandsId;
	private int bookingId;
	private Date fromTime;
	private Date toTime;
	private String cityName;
	private String localityName;
	private String locationName;
	private String address;
	private String landmark;
	private int userId;
	private String displayName;

	public MeetingResult(int id) {
		this.id = id;
	}

	public MeetingResult(int id, String title, String description, String participandsId, int bookingId, Date fromTime,
			Date toTime, String cityName, String localityName, String locationName, String address, String landmark,
			int userId, String displayName) {
		this.id = id;
		this.title = title;
		this.description = description;
		this.participandsId = participandsId;
		this.bookingId = bookingId;
		this.fromTime = fromTime;
		this.toTime = toTime;
		this.cityName = cityName;
		this.localityName = localityName;
		this.locationName = locationName;
		this.address = address;
		this.landmark = landmark;
		this.userId = userId;
		this.displayName = displayName;
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

	public String getParticipandsId() {
		return participandsId;
	}

	public void setParticipandsId(String participandsId) {
		this.participandsId = participandsId;
	}

	public int getBookingId() {
		return bookingId;
	}

	public void setBookingId(int bookingId) {
		this.bookingId = bookingId;
	}

	public Date getFromTime() {
		return fromTime;
	}

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+5:30")
	public void setFromTime(Date fromTime) {
		this.fromTime = fromTime;
	}

	public Date getToTime() {
		return toTime;
	}

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+5:30")
	public void setToTime(Date toTime) {
		this.toTime = toTime;
	}

	public String getCityName() {
		return cityName;
	}

	public void setCityName(String cityName) {
		this.cityName = cityName;
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

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}
}
