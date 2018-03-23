package myapps.solutions.huddil.model;

import java.util.Date;

public class CancellationNotificationTemplate {

	UserPref sp;
	UserPref consumer;
	FacilityCancellationCharges cancellationCharges;
	Double refundAmt;
	Date fromTime;
	String place;
	int bookingId;
	String paymentId;

	public CancellationNotificationTemplate(UserPref sp, UserPref consumer,
			FacilityCancellationCharges cancellationCharges, Double refundAmt, Date fromTime, String place,
			int bookingId) {
		this.sp = sp;
		this.consumer = consumer;
		this.cancellationCharges = cancellationCharges;
		this.refundAmt = refundAmt;
		this.fromTime = fromTime;
		this.place = place;
		this.bookingId = bookingId;
	}

	public CancellationNotificationTemplate(String spName, String spEmailId, String spMobileNo,
			boolean spMobileVerified, String cName, String cEmailId, String cMobileNo, boolean cMobileVerified,
			int duration1, double percentage1, int duration2, double percentage2, int duration3, double percentage3,
			int bookingId, Date fromTime, String paymentId, double totalPrice, String place) {
		this.sp = new UserPref(spName, spEmailId, spMobileNo, spMobileVerified);
		this.consumer = new UserPref(cName, cEmailId, cMobileNo, cMobileVerified);
		this.cancellationCharges = new FacilityCancellationCharges(duration1, percentage1, duration2, percentage2,
				duration3, percentage3);
		this.refundAmt = totalPrice;
		this.fromTime = fromTime;
		this.place = place;
		this.bookingId = bookingId;
		this.paymentId = paymentId;
	}

	public UserPref getSp() {
		return sp;
	}

	public void setSp(UserPref sp) {
		this.sp = sp;
	}

	public UserPref getConsumer() {
		return consumer;
	}

	public void setConsumer(UserPref consumer) {
		this.consumer = consumer;
	}

	public FacilityCancellationCharges getCancellationCharges() {
		return cancellationCharges;
	}

	public void setCancellationCharges(FacilityCancellationCharges cancellationCharges) {
		this.cancellationCharges = cancellationCharges;
	}

	public Double getRefundAmt() {
		return refundAmt;
	}

	public void setRefundAmt(Double refundAmt) {
		this.refundAmt = refundAmt;
	}

	public Date getFromTime() {
		return fromTime;
	}

	public void setFromTime(Date fromTime) {
		this.fromTime = fromTime;
	}

	public String getPlace() {
		return place;
	}

	public void setPlace(String place) {
		this.place = place;
	}

	public int getBookingId() {
		return bookingId;
	}

	public void setBookingId(int bookingId) {
		this.bookingId = bookingId;
	}

	public String getPaymentId() {
		return paymentId;
	}

	public void setPaymentId(String paymentId) {
		this.paymentId = paymentId;
	}
}
