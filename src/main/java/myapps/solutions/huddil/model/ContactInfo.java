package myapps.solutions.huddil.model;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

import org.apache.http.util.TextUtils;

public class ContactInfo {

	private InternetAddress emailId;
	private String phoneNo;

	public ContactInfo(String emailId, String phoneNo) throws AddressException {
		if (!TextUtils.isEmpty(emailId)) 
			this.emailId = new InternetAddress(emailId);
		this.phoneNo = phoneNo;
	}

	public InternetAddress getEmailId() {
		return emailId;
	}

	public void setEmailId(InternetAddress emailId) {
		this.emailId = emailId;
	}

	public String getphoneNo() {
		return phoneNo;
	}

	public void setphoneNo(String phoneNo) {
		this.phoneNo = phoneNo;
	}
}
