package myapps.solutions.huddil.model;

public class UpdateBooking {

	int result;
	String refundAmount;

	public UpdateBooking(int result) {
		this.result = result;
	}

	public UpdateBooking(int result, String refundAmount) {
		this.result = result;
		this.refundAmount = refundAmount;
	}

	public int getResult() {
		return result;
	}

	public void setResult(int result) {
		this.result = result;
	}

	public String getRefundAmount() {
		return refundAmount;
	}

	public void setRefundAmount(String refundAmount) {
		this.refundAmount = refundAmount;
	}

}
