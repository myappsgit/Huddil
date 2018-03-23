package myapps.solutions.huddil.model;

public class BookingCost {

	private String responseCode;
	private int bookingId;
	private double basePrice;
	private double cGst;
	private double sGst;
	private double cGstCost;
	private double sGstCost;
	private double offer;
	private double totalCost;
	private String paymentUrl;

	public BookingCost(String responseCode) {
		this.responseCode = responseCode;
	}

	public BookingCost(String responseCode, int bookingId, double basePrice, double cGst, double sGst, double cGstCost,
			double sGstCost, double offer, double totalCost) {
		this.responseCode = responseCode;
		this.bookingId = bookingId;
		this.basePrice = basePrice;
		this.cGst = cGst;
		this.sGst = sGst;
		this.cGstCost = cGstCost;
		this.sGstCost = sGstCost;
		this.offer = offer;
		this.totalCost = totalCost;
	}

	public String getResponseCode() {
		return responseCode;
	}

	public void setResponseCode(String responseCode) {
		this.responseCode = responseCode;
	}

	public int getBookingId() {
		return bookingId;
	}

	public void setBookingId(int bookingId) {
		this.bookingId = bookingId;
	}

	public double getBasePrice() {
		return basePrice;
	}

	public void setBasePrice(double basePrice) {
		this.basePrice = basePrice;
	}

	public double getcGst() {
		return cGst;
	}

	public void setcGst(double cGst) {
		this.cGst = cGst;
	}

	public double getsGst() {
		return sGst;
	}

	public void setsGst(double sGst) {
		this.sGst = sGst;
	}

	public double getcGstCost() {
		return cGstCost;
	}

	public void setcGstCost(double cGstCost) {
		this.cGstCost = cGstCost;
	}

	public double getsGstCost() {
		return sGstCost;
	}

	public void setsGstCost(double sGstCost) {
		this.sGstCost = sGstCost;
	}

	public double getOffer() {
		return offer;
	}

	public void setOffer(double offer) {
		this.offer = offer;
	}

	public double getTotalCost() {
		return totalCost;
	}

	public void setTotalCost(double totalCost) {
		this.totalCost = totalCost;
	}

	public String getPaymentUrl() {
		return paymentUrl;
	}

	public void setPaymentUrl(String paymentUrl) {
		this.paymentUrl = paymentUrl;
	}

}
