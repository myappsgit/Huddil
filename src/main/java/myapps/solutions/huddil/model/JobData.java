package myapps.solutions.huddil.model;

import java.util.Date;

public class JobData {

	private boolean startTime;
	private Date date;
	private int bookingId;
	private String cityName;
	private String spName;
	private String spEmailId;
	private String spMobileNo;
	private boolean spMobileNoVerified;
	private String cName;
	private String cMobileNo;
	private String cEmailId;
	private boolean cMobileNoVerified;

	public JobData(int bookingId, String startTime, Date time, String cityName, String spName, String spEmailId,
			String spMobileNo, boolean spMobileNoVerified, String cName, String cMobileNo, boolean cMobileNoVerified,
			String cEmailId) {
		this.bookingId = bookingId;
		this.startTime = startTime.equals("y");
		this.date = time;
		this.cityName = cityName;
		this.spName = spName;
		this.spEmailId = spEmailId;
		this.spMobileNoVerified = spMobileNoVerified;
		this.spMobileNo = spMobileNo;
		this.cName = cName;
		this.cMobileNo = cMobileNo;
		this.cEmailId = cEmailId;
		this.cMobileNoVerified = cMobileNoVerified;
	}

	public JobData(int bookingId, Date date, String startTime) {
		this.startTime = startTime.equals("y");
		this.bookingId = bookingId;
		this.date = date;
	}

	public boolean isStartTime() {
		return startTime;
	}

	public void setStartTime(boolean startTime) {
		this.startTime = startTime;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public int getBookingId() {
		return bookingId;
	}

	public void setBookingId(int bookingId) {
		this.bookingId = bookingId;
	}

	public String getCityName() {
		return cityName;
	}

	public void setCityName(String cityName) {
		this.cityName = cityName;
	}

	public String getSpName() {
		return spName;
	}

	public void setSpName(String spName) {
		this.spName = spName;
	}

	public String getSpEmailId() {
		return spEmailId;
	}

	public void setSpEmailId(String spEmailId) {
		this.spEmailId = spEmailId;
	}

	public String getSpMobileNo() {
		return spMobileNo;
	}

	public void setSpMobileNo(String spMobileNo) {
		this.spMobileNo = spMobileNo;
	}

	public boolean isSpMobileNoVerified() {
		return spMobileNoVerified;
	}

	public void setSpMobileNoVerified(boolean spMobileNoVerified) {
		this.spMobileNoVerified = spMobileNoVerified;
	}

	public String getcName() {
		return cName;
	}

	public void setcName(String cName) {
		this.cName = cName;
	}

	public String getcMobileNo() {
		return cMobileNo;
	}

	public void setcMobileNo(String cMobileNo) {
		this.cMobileNo = cMobileNo;
	}

	public String getcEmailId() {
		return cEmailId;
	}

	public void setcEmailId(String cEmailId) {
		this.cEmailId = cEmailId;
	}

	public boolean iscMobileNoVerified() {
		return cMobileNoVerified;
	}

	public void setcMobileNoVerified(boolean cMobileNoVerified) {
		this.cMobileNoVerified = cMobileNoVerified;
	}

}
