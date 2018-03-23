package myapps.solutions.huddil.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class AdminPaymentDB {

	private int userId;
	private String dName;
	private String fName;
	private String lName;
	private String cName;
	private double online;
	private double onlineRefund;
	private double onlineCancelCharge;
	private double offline;
	private double offlineRefund;
	private double transcationCharge;
	private double commission;
	private double settlement;
	private int status;

	public AdminPaymentDB(int status) {
		this.status = status;
	}

	public AdminPaymentDB(double online, double onlineRefund, double onlineCancelCharge, double offline, double offlineRefund,
			double transcationCharge, double commission, double settlement) {
		this.online = online;
		this.onlineRefund = onlineRefund;
		this.onlineCancelCharge = onlineCancelCharge;
		this.offline = offline;
		this.offlineRefund = offlineRefund;
		this.transcationCharge = transcationCharge;
		this.commission = commission;
		this.settlement = settlement;
	}

	public AdminPaymentDB(int userId, String dName, String fName, String lName, String cName, double online,
			double onlineRefund, double onlineCancelCharge, double offline, double offlineRefund, double transcationCharge, double commission,
			double settlement) {
		this.userId = userId;
		this.dName = dName;
		this.fName = fName;
		this.lName = lName;
		this.cName = cName;
		this.online = online;
		this.onlineRefund = onlineRefund;
		this.onlineCancelCharge = onlineCancelCharge;
		this.offline = offline;
		this.offlineRefund = offlineRefund;
		this.transcationCharge = transcationCharge;
		this.commission = commission;
		this.settlement = settlement;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public String getdName() {
		return dName;
	}

	public void setdName(String dName) {
		this.dName = dName;
	}

	public String getfName() {
		return fName;
	}

	public void setfName(String fName) {
		this.fName = fName;
	}

	public String getlName() {
		return lName;
	}

	public void setlName(String lName) {
		this.lName = lName;
	}

	public String getcName() {
		return cName;
	}

	public void setcName(String cName) {
		this.cName = cName;
	}

	public double getOnline() {
		return online;
	}

	public void setOnline(double online) {
		this.online = online;
	}

	public double getOnlineRefund() {
		return onlineRefund;
	}

	public void setOnlineRefund(double onlineRefund) {
		this.onlineRefund = onlineRefund;
	}

	public double getOnlineCancelCharge() {
		return onlineCancelCharge;
	}

	public void setOnlineCancelCharge(double onlineCancelCharge) {
		this.onlineCancelCharge = onlineCancelCharge;
	}

	public double getOffline() {
		return offline;
	}

	public void setOffline(double offline) {
		this.offline = offline;
	}

	public double getOfflineRefund() {
		return offlineRefund;
	}

	public void setOfflineRefund(double offlineRefund) {
		this.offlineRefund = offlineRefund;
	}

	public double getTranscationCharge() {
		return transcationCharge;
	}

	public void setTranscationCharge(double transcationCharge) {
		this.transcationCharge = transcationCharge;
	}

	public double getCommission() {
		return commission;
	}

	public void setCommission(double commission) {
		this.commission = commission;
	}

	public double getSettlement() {
		return settlement;
	}

	public void setSettlement(double settlement) {
		this.settlement = settlement;
	}

	@JsonIgnore
	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

}
