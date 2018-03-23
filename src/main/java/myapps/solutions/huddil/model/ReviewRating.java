package myapps.solutions.huddil.model;

import java.math.BigInteger;

public class ReviewRating {

	private BigInteger count;
	private double sumRating;

	public ReviewRating(BigInteger count, double sumRating) {
		this.count = count;
		this.sumRating = sumRating;
	}

	public BigInteger getCount() {
		return count;
	}

	public void setCount(BigInteger count) {
		this.count = count;
	}

	public double getSumRating() {
		return sumRating;
	}

	public void setSumRating(double sumRating) {
		this.sumRating = sumRating;
	}

}
