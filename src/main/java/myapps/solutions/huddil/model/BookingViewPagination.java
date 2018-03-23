package myapps.solutions.huddil.model;

import java.util.List;

public class BookingViewPagination {

	private int id;
	private String count;
	private List<BookingView> bookingViews;

	public BookingViewPagination(int id) {
		this.id = id;
	}

	public BookingViewPagination(String id, List<BookingView> bookingViews) {
		this.count = id;
		this.bookingViews = bookingViews;
	}

	public BookingViewPagination() {
		// TODO Auto-generated constructor stub
	}

	public List<BookingView> getBookingViews() {
		return bookingViews;
	}

	public void setBookingViews(List<BookingView> bookingViews) {
		this.bookingViews = bookingViews;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getCount() {
		return count;
	}

	public void setCount(String count) {
		this.count = count;
	}


}
