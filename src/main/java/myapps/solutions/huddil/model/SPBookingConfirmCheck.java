package myapps.solutions.huddil.model;

import java.util.Date;

public class SPBookingConfirmCheck {

	int facilityId;
	String facilityType;
	int facilityTypeId;
	int bookingId;
	int bookedSeats;
	int facilitySeats;
	Date fromTime;
	Date toTime;

	public SPBookingConfirmCheck(int facilityId, String facilityType, int facilityTypeId, int bookingId,
			int bookedSeats, int facilitySeats, Date fromTime, Date toTime) {
		this.facilityId = facilityId;
		this.facilityType = facilityType;
		this.facilityTypeId = facilityTypeId;
		this.bookingId = bookingId;
		this.bookedSeats = bookedSeats;
		this.facilitySeats = facilitySeats;
		this.fromTime = fromTime;
		this.toTime = toTime;
	}

	public int getFacilityId() {
		return facilityId;
	}

	public void setFacilityId(int facilityId) {
		this.facilityId = facilityId;
	}

	public String getFacilityType() {
		return facilityType;
	}

	public void setFacilityType(String facilityType) {
		this.facilityType = facilityType;
	}

	public int getFacilityTypeId() {
		return facilityTypeId;
	}

	public void setFacilityTypeId(int facilityTypeId) {
		this.facilityTypeId = facilityTypeId;
	}

	public int getBookingId() {
		return bookingId;
	}

	public void setBookingId(int bookingId) {
		this.bookingId = bookingId;
	}

	public int getBookedSeats() {
		return bookedSeats;
	}

	public void setBookedSeats(int bookedSeats) {
		this.bookedSeats = bookedSeats;
	}

	public int getFacilitySeats() {
		return facilitySeats;
	}

	public void setFacilitySeats(int facilitySeats) {
		this.facilitySeats = facilitySeats;
	}

	public Date getFromTime() {
		return fromTime;
	}

	public void setFromTime(Date fromTime) {
		this.fromTime = fromTime;
	}

	public Date getToTime() {
		return toTime;
	}

	public void setToTime(Date toTime) {
		this.toTime = toTime;
	}

}
