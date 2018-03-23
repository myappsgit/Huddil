package myapps.solutions.huddil.model;

import java.math.BigInteger;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;

public class CalendarBooking {

	private int bookingId;
	private String roomType;
	private Date fromTime;
	private Date toTime;
	private BigInteger bookedSeats;
	private BigInteger remainingSeats;
	private int bookedBySp;

	public CalendarBooking(int bookingId, String roomType, Date fromTime, Date toTime, BigInteger bookedSeats,
			BigInteger remainingSeats, String bookedBySp) {
		this.bookingId = bookingId;
		this.roomType = roomType;
		this.fromTime = fromTime;
		this.toTime = toTime;
		this.bookedSeats = bookedSeats;
		this.remainingSeats = remainingSeats;
		this.bookedBySp = bookedBySp.equals("y") ? 1 : 0;
	}

	public int getBookingId() {
		return bookingId;
	}

	public void setBookingId(int bookingId) {
		this.bookingId = bookingId;
	}

	public String getRoomType() {
		return roomType;
	}

	public void setRoomType(String roomType) {
		this.roomType = roomType;
	}

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+5:30")
	public Date getFromTime() {
		return fromTime;
	}

	public void setFromTime(Date fromTime) {
		this.fromTime = fromTime;
	}

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+5:30")
	public Date getToTime() {
		return toTime;
	}

	public void setToTime(Date toTime) {
		this.toTime = toTime;
	}

	public BigInteger getBookedSeats() {
		return bookedSeats;
	}

	public void setBookedSeats(BigInteger bookedSeats) {
		this.bookedSeats = bookedSeats;
	}

	public BigInteger getRemainingSeats() {
		return remainingSeats;
	}

	public void setRemainingSeats(BigInteger remainingSeats) {
		this.remainingSeats = remainingSeats;
	}

	public int getBookedBySp() {
		return bookedBySp;
	}

	public void setBookedBySp(int bookedBySp) {
		this.bookedBySp = bookedBySp;
	}

}