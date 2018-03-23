package myapps.solutions.huddil.model;

import java.util.Date;

public class ReviewResult {
	
	private int id;
	private String comments;
	private Date dateTime;
	private double rating;
	private int parentId;
	private int facilityId;
	private int bookingId;
	
	public ReviewResult() {
	
	}

	public ReviewResult(int id, String comments, Date dateTime, double rating, int parentId, int facilityId,int bookingId) {
		this.id = id;
		this.comments =comments;
		this.dateTime = dateTime;
		this.rating = rating;
		this.parentId = parentId;
		this.facilityId = facilityId;
		this.bookingId = bookingId;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}

	public Date getDateTime() {
		return dateTime;
	}

	public void setDateTime(Date dateTime) {
		this.dateTime = dateTime;
	}

	public double getRating() {
		return rating;
	}

	public void setRating(double rating) {
		this.rating = rating;
	}

	public int getParentId() {
		return parentId;
	}

	public void setParentId(int parentId) {
		this.parentId = parentId;
	}

	public int getFacilityId() {
		return facilityId;
	}

	public void setFacilityId(int facilityId) {
		this.facilityId = facilityId;
	}

	public int getBookingId() {
		return bookingId;
	}

	public void setBookingId(int bookingId) {
		this.bookingId = bookingId;
	}
	
	

}
