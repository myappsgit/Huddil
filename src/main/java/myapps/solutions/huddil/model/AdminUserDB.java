package myapps.solutions.huddil.model;

import java.math.BigInteger;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class AdminUserDB {

	private String type;
	private BigInteger count;
	private BigInteger status;
	private int result;

	public AdminUserDB(int result) {
		this.result = result;
	}

	public AdminUserDB(String type, BigInteger count, BigInteger status) {
		this.type = type;
		this.count = count;
		this.status = status;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
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
