package myapps.solutions.huddil.model;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

public class CancellationResults {

	private int bookingId;
	private Date bookedTime;
	private int facilityId;
	private String paymentMethod;
	private String title;
	private String typeName;
	private String cityName;
	private String localityName;
	private String name;
	private String address;
	private String landmark;
	private Date bookedFrom;
	private Date bookedTo;
	private double totalPrice;
	private double refundAmount;
	private String cancelledStatus;
	private String refundId;
	private int seats;
	private String displayName;
	private String mobileNo;
	private String emailId;
	private Date cancelledDate;
	private String status;

	public CancellationResults() {

	}

	public CancellationResults(int bookingId) {
		this.bookingId = bookingId;
	}

	public CancellationResults(int bookingId, Date bookedTime, int facilityId, String paymentMethod, String title,
			String typeName, String cityName, String localityName, String name, String address, String lanmark,
			Date bookedFrom, Date bookedTo, double totaPrice, double refundAmount, String cancelledStatus,
			String refundId, int seats, String displayName, String mobileNo, String emailId, Date cancelledDate,
			String status) {
		this.bookingId = bookingId;
		this.bookedTime = bookedTime;
		this.facilityId = facilityId;
		this.paymentMethod = paymentMethod;
		this.title = title;
		this.typeName = typeName;
		this.cityName = cityName;
		this.localityName = localityName;
		this.name = name;
		this.address = address;
		this.landmark = lanmark;
		this.bookedFrom = bookedFrom;
		this.bookedTo = bookedTo;
		this.totalPrice = totaPrice;
		this.refundAmount = refundAmount;
		this.cancelledStatus = cancelledStatus;
		this.refundId = refundId;
		this.seats = seats;
		this.displayName = displayName;
		this.mobileNo = mobileNo;
		this.emailId = emailId;
		this.cancelledDate = cancelledDate;
		this.status = status;
	}

	public int getBookingId() {
		return bookingId;
	}

	public void setBookingId(int bookingId) {
		this.bookingId = bookingId;
	}

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+5:30")
	public Date getBookedTime() {
		return bookedTime;
	}

	public void setBookedTime(Date bookedTime) {
		this.bookedTime = bookedTime;
	}

	public int getFacilityId() {
		return facilityId;
	}

	public void setFacilityId(int facilityId) {
		this.facilityId = facilityId;
	}

	public String getPaymentMethod() {
		return paymentMethod;
	}

	public void setPaymentMethod(String paymentMethod) {
		this.paymentMethod = paymentMethod;
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

	public String getLandmark() {
		return landmark;
	}

	public void setLandmark(String landmark) {
		this.landmark = landmark;
	}

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+5:30")
	public Date getBookedFrom() {
		return bookedFrom;
	}

	public void setBookedFrom(Date bookedFrom) {
		this.bookedFrom = bookedFrom;
	}

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+5:30")
	public Date getBookedTo() {
		return bookedTo;
	}

	public void setBookedTo(Date bookedTo) {
		this.bookedTo = bookedTo;
	}

	public double getTotalPrice() {
		return totalPrice;
	}

	public void setTotalPrice(double totalPrice) {
		this.totalPrice = totalPrice;
	}

	public String getCancelledStatus() {
		return cancelledStatus;
	}

	public void setCancelledStatus(String cancelledStatus) {
		this.cancelledStatus = cancelledStatus;
	}

	public String getRefundId() {
		return refundId;
	}

	public void setRefundId(String refundId) {
		this.refundId = refundId;
	}

	public double getRefundAmount() {
		return refundAmount;
	}

	public void setRefundAmount(double refundAmount) {
		this.refundAmount = refundAmount;
	}

	public int getSeats() {
		return seats;
	}

	public void setSeats(int seats) {
		this.seats = seats;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public String getMobileNo() {
		return mobileNo;
	}

	public void setMobileNo(String mobileNo) {
		this.mobileNo = mobileNo;
	}

	public String getEmailId() {
		return emailId;
	}

	public void setEmailId(String emailId) {
		this.emailId = emailId;
	}

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+5:30")
	public Date getCancelledDate() {
		return cancelledDate;
	}

	public void setCancelledDate(Date cancelledDate) {
		this.cancelledDate = cancelledDate;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

}
