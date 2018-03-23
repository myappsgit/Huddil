package myapps.solutions.huddil.model;

public class CommissionDetails {

	private int spUserId;
	private double currentMonthCommission;
	private double nextMonthCommission;

	public CommissionDetails() {

	}

	public CommissionDetails(int spUserId, double currentMonthCommission, double nextMonthCommission) {
		this.spUserId = spUserId;
		this.currentMonthCommission = currentMonthCommission;
		this.nextMonthCommission = nextMonthCommission;
	}

	public int getSpUserId() {
		return spUserId;
	}

	public void setSpUserId(int spUserId) {
		this.spUserId = spUserId;
	}

	public double getCurrentMonthCommission() {
		return currentMonthCommission;
	}

	public void setCurrentMonthCommission(double currentMonthCommission) {
		this.currentMonthCommission = currentMonthCommission;
	}

	public double getNextMonthCommission() {
		return nextMonthCommission;
	}

	public void setNextMonthCommission(double nextMonthCommission) {
		this.nextMonthCommission = nextMonthCommission;
	}

}
