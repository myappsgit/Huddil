package myapps.solutions.huddil.model;

import java.util.Date;

public class BookingNotificationTemplate {

	private Date fromTime;
	private Date toTime;
	private Facility facility;
	private UserPref serviceProvider;
	private BookingCost bookingCost;
	private UserSearchResult consumer;

	public BookingNotificationTemplate() {
	}

	public BookingNotificationTemplate(Date fromTime, Date toTime, Facility facility, BookingCost bookingCost) {
		this.fromTime = fromTime;
		this.toTime = toTime;
		this.facility = facility;
		this.bookingCost = bookingCost;
	}

	public BookingNotificationTemplate(Date fromTime, Date toTime, Facility facility, UserPref serviceProvider) {
		this.fromTime = fromTime;
		this.toTime = toTime;
		this.facility = facility;
		this.serviceProvider = serviceProvider;
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

	public Facility getFacility() {
		return facility;
	}

	public void setFacility(Facility facility) {
		this.facility = facility;
	}

	public UserPref getServiceProvider() {
		return serviceProvider;
	}

	public void setServiceProvider(UserPref serviceProvider) {
		this.serviceProvider = serviceProvider;
	}

	public BookingCost getBookingCost() {
		return bookingCost;
	}

	public void setBookingCost(BookingCost bookingCost) {
		this.bookingCost = bookingCost;
	}

	public UserSearchResult getConsumer() {
		return consumer;
	}

	public void setConsumer(UserSearchResult consumer) {
		this.consumer = consumer;
	}
}
