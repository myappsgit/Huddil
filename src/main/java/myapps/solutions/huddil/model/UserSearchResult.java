package myapps.solutions.huddil.model;

import java.math.BigInteger;

public class UserSearchResult {

	String userName;
	String emailId;
	String mobileNo;
	int active;
	Integer id;
	int userType;
	boolean mobileNoVerified;

	public UserSearchResult() {

	}

	public UserSearchResult(int id) {
		this.id = id;
	}

	public UserSearchResult(int id, String userName, String emailId, String mobileNo, int isActive, int userType) {
		this.id = id;
		this.userName = userName;
		this.emailId = emailId;
		this.mobileNo = mobileNo;
		this.active = isActive;
		this.userType = userType;
	}

	public UserSearchResult(int id, String userName, String emailId, String mobileNo, boolean mobileNoVerified, BigInteger userType) {
		this.id = id;
		this.userName = userName;
		this.emailId = emailId;
		this.mobileNo = mobileNo;
		this.mobileNoVerified = mobileNoVerified;
		this.userType = userType.intValue();
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getEmailId() {
		return emailId;
	}

	public void setEmailId(String emailId) {
		this.emailId = emailId;
	}

	public String getMobileNo() {
		return mobileNo;
	}

	public void setMobileNo(String mobileNo) {
		this.mobileNo = mobileNo;
	}

	public int getActive() {
		return active;
	}

	public void setActive(int isActive) {
		this.active = isActive;
	}

	public int getUserType() {
		return userType;
	}

	public void setUserType(int userType) {
		this.userType = userType;
	}

	public boolean isMobileNoVerified() {
		return mobileNoVerified;
	}

	public void setMobileNoVerified(boolean mobileNoVerified) {
		this.mobileNoVerified = mobileNoVerified;
	}
}