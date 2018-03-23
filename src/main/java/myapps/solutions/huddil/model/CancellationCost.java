package myapps.solutions.huddil.model;

public class CancellationCost {

	private String responseCode;
	private int id;
	private double refundAmt;
	private double cancellationAmt;
	private double totalAmt;
	private String refundId;

	public CancellationCost(String responseCode) {
		this.responseCode = responseCode;
	}

	public CancellationCost(String responseCode, int id, double refundAmt, double cancellationAmt, double totalAmt) {
		this.responseCode = responseCode;
		this.id = id;
		this.refundAmt = refundAmt;
		this.cancellationAmt = cancellationAmt;
		this.totalAmt = totalAmt;
	}

	public String getResponseCode() {
		return responseCode;
	}

	public void setResponseCode(String responseCode) {
		this.responseCode = responseCode;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public double getRefundAmt() {
		return refundAmt;
	}

	public void setRefundAmt(double refundAmt) {
		this.refundAmt = refundAmt;
	}

	public double getCancellationAmt() {
		return cancellationAmt;
	}

	public void setCancellationAmt(double cancellationAmt) {
		this.cancellationAmt = cancellationAmt;
	}

	public double getTotalAmt() {
		return totalAmt;
	}

	public void setTotalAmt(double totalAmt) {
		this.totalAmt = totalAmt;
	}

	public String getRefundId() {
		return refundId;
	}

	public void setRefundId(String refundId) {
		this.refundId = refundId;
	}

}
