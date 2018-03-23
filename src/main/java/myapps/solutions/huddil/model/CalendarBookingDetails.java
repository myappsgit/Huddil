package myapps.solutions.huddil.model;

import java.util.ArrayList;
import java.util.List;

public class CalendarBookingDetails {

	private List<FacilityTiming> timing = new ArrayList<FacilityTiming>();
	private List<CalendarBooking> bookingDetails = new ArrayList<>();

	public CalendarBookingDetails(List<CalendarBooking> bookingDetails, List<FacilityTiming> timing) {
		this.bookingDetails = bookingDetails;
		this.timing = timing;
	}

	public List<FacilityTiming> getTiming() {
		return timing;
	}

	public void setTiming(List<FacilityTiming> timing) {
		this.timing = timing;
	}

	public List<CalendarBooking> getBookingDetails() {
		return bookingDetails;
	}

	public void setBookingDetails(List<CalendarBooking> bookingDetails) {
		this.bookingDetails = bookingDetails;
	}

}
