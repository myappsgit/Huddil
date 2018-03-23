package myapps.solutions.huddil.model;

import java.math.BigInteger;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

public class BookingView {

	private int bookingId;
	private Date bookedFrom;
	private Date bookedTo;
	private Date bookedTime;
	private double totalPrice;
	private String paymentMethod;
	private int status;
	private String title;
	private String typeName;
	private String name;
	private String address;
	private String displayName;
	private String emailId;
	private String mobileNo;
	private int count;
	private int seats;

	public BookingView() {

	}
	
	public BookingView(int bookingId) {
		this.bookingId = bookingId;
	}

	public BookingView(int bookingId, Date bookedFrom, Date bookedTo, Date bookedTime, double totalPrice, String paymentMethod, int status, String title, String typeName, String name, String address, String displayName, String emailId, String mobileNo, int seats) {
		this.bookingId = bookingId;
		this.bookedFrom = bookedFrom;
		this.bookedTo = bookedTo;
		this.bookedTime = bookedTime;
		this.totalPrice = totalPrice;
		this.paymentMethod = paymentMethod;
		this.status = status;
		this.title = title;
		this.typeName = typeName;
		this.name = name;
		this.address = address;
		this.displayName =displayName;
		this.emailId = emailId;
		this.mobileNo = mobileNo;
		this.seats = seats;

	}

	public BookingView(int bookingId, Date bookedFrom, Date bookedTo, Date bookedTime, double totalPrice, String paymentMethod, BigInteger status, String title, String typeName, String name, String address, String displayName, String emailId, String mobileNo, int seats) {
		this.bookingId = bookingId;
		this.bookedFrom = bookedFrom;
		this.bookedTo = bookedTo;
		this.bookedTime = bookedTime;
		this.totalPrice = totalPrice;
		this.paymentMethod = paymentMethod;
		this.status = status.intValue();
		this.title = title;
		this.typeName = typeName;
		this.name = name;
		this.address = address;
		this.displayName =displayName;
		this.emailId = emailId;
		this.mobileNo = mobileNo;
		this.seats = seats;
	}

	public int getBookingId() {
		return bookingId;
	}

	public void setBookingId(int bookingId) {
		this.bookingId = bookingId;
	}
	
	@JsonFormat(shape=JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm:ss", timezone="GMT+5:30")
	public Date getBookedFrom() {
		return bookedFrom;
	}

	public void setBookedFrom(Date bookedFrom) {
		this.bookedFrom = bookedFrom;
	}

	@JsonFormat(shape=JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm:ss", timezone="GMT+5:30")
	public Date getBookedTo() {
		return bookedTo;
	}

	public void setBookedTo(Date bookedTo) {
		this.bookedTo = bookedTo;
	}

	@JsonFormat(shape=JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm:ss", timezone="GMT+5:30")
	public Date getBookedTime() {
		return bookedTime;
	}
	public void setBookedTime(Date bookedTime) {
		this.bookedTime = bookedTime;
	}
	
	public double getTotalPrice() {
		return totalPrice;
	}

	public void setTotalPrice(double totalPrice) {
		this.totalPrice = totalPrice;
	}

	public String getPaymentMethod() {
		return paymentMethod;
	}
	public void setPaymentMethod(String paymentMethod) {
		this.paymentMethod = paymentMethod;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	
	public String getTypeName() {
		return typeName;
	}

	public void setTypeName(String typeName) {
		this.typeName = typeName;
	}

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public String getEmailId() {
		return emailId;
	}

	public void setEmailId(String emailId) {
		this.emailId = emailId;
	}

	public String getMobileNo() {
		return mobileNo;
	}

	public void setMobileNo(String mobileNo) {
		this.mobileNo = mobileNo;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public int getSeats() {
		return seats;
	}

	public void setSeats(int seats) {
		this.seats = seats;
	}
	
	
	
	

}
