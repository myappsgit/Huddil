package myapps.solutions.huddil.model;

import java.util.List;

public class BookingResultsPagination {

	String id;
	List<BookingResults> bookingResults;

	public BookingResultsPagination(String id, List<BookingResults> bookingResults) {
		super();
		this.id = id;
		this.bookingResults = bookingResults;
	}

	public BookingResultsPagination() {
		
	}

	public BookingResultsPagination(int i) {
		this.id = Integer.toString(i);
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public List<BookingResults> getBookingResults() {
		return bookingResults;
	}

	public void setBookingResults(List<BookingResults> bookingResults) {
		this.bookingResults = bookingResults;
	}

}
