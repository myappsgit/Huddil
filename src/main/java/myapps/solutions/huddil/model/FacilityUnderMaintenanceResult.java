package myapps.solutions.huddil.model;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

public class FacilityUnderMaintenanceResult {

	private int id;
	private Date fromDateTime;
	private Date toDateTime;
	private String reason;
	private int facilityId;

	public FacilityUnderMaintenanceResult() {

	}

	public FacilityUnderMaintenanceResult(int id, Date fromDateTime, Date toDateTime, String reason, int facilityId) {
		this.id = id;
		this.fromDateTime =fromDateTime;
		this.toDateTime =toDateTime;
		this.reason= reason;
		this.facilityId = facilityId;
	}
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
	public Date getFromDateTime() {
		return fromDateTime;
	}

	public void setFromDateTime(Date fromDateTime) {
		this.fromDateTime = fromDateTime;
	}

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
	public Date getToDateTime() {
		return toDateTime;
	}

	public void setToDateTime(Date toDateTime) {
		this.toDateTime = toDateTime;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	public int getFacilityId() {
		return facilityId;
	}

	public void setFacilityId(int facilityId) {
		this.facilityId = facilityId;
	}

}
