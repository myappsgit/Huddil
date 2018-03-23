package myapps.solutions.huddil.model;

import java.math.BigInteger;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;

public class StatusCount {

	private int id;
	private BigInteger count;
	private String name;

	public StatusCount(int id) {
		this.id = id;
	}

	public StatusCount(BigInteger count, String name) {
		this.name = name;
		this.count = count;
	}

	@JsonProperty(access = Access.WRITE_ONLY)
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public BigInteger getCount() {
		return count;
	}

	public void setCount(BigInteger count) {
		this.count = count;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
