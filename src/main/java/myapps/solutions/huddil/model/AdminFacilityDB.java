package myapps.solutions.huddil.model;

import java.math.BigInteger;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class AdminFacilityDB {

	private BigInteger count;
	private BigInteger status;
	private int result;

	public AdminFacilityDB(int result) {
		this.result = result;
	}

	public AdminFacilityDB(BigInteger status) {
		this.status = status;
	}

	public AdminFacilityDB(BigInteger count, BigInteger status) {
		this.count = count;
		this.status = status;
	}

	public BigInteger getCount() {
		return count;
	}

	public void setCount(BigInteger count) {
		this.count = count;
	}

	public BigInteger getStatus() {
		return status;
	}

	public void setStatus(BigInteger status) {
		this.status = status;
	}

	@JsonIgnore
	public int getResult() {
		return result;
	}

	public void setResult(int result) {
		this.result = result;
	}
}
