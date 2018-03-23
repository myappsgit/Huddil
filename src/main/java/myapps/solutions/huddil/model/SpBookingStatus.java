package myapps.solutions.huddil.model;

public class SpBookingStatus {

	private int count;
	private String name;

	public SpBookingStatus(int count) {
		this.count = count;
	}

	public SpBookingStatus(String count, String name) {
		this.count = Integer.parseInt(count);
		this.name = name;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
