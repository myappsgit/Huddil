package myapps.solutions.huddil.model;

import java.util.Date;

import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.fasterxml.jackson.annotation.JsonFormat;

public class PaymentReport {

	private Date date;
	private String title;
	private String facilityType;
	private String buildingName;
	private String city;
	private String locality;
	private double payment;
	private String consumerName;
	private String serviceProvider;

	public PaymentReport() {

	}

	public PaymentReport(Date date, String title, String facilityType, String buildingName, String city, String locality,
			double payment, String consumerName, String serviceProvider) {
		this.date = date;
		this.title = title;
		this.facilityType = facilityType;
		this.buildingName = buildingName;
		this.city = city;
		this.locality = locality;
		this.payment = payment;
		this.consumerName = consumerName;
		this.serviceProvider = serviceProvider;

	}
	public PaymentReport(int i) {
		this.payment = Double.parseDouble(i + "");
	}

	@Temporal(TemporalType.DATE)
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy", timezone = "GMT+5:30")
	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getFacilityType() {
		return facilityType;
	}

	public void setFacilityType(String facilityType) {
		this.facilityType = facilityType;
	}

	public String getBuildingName() {
		return buildingName;
	}

	public void setBuildingName(String buildingName) {
		this.buildingName = buildingName;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getLocality() {
		return locality;
	}

	public void setLocality(String locality) {
		this.locality = locality;
	}

	public double getPayment() {
		return payment;
	}

	public void setPayment(double payment) {
		this.payment = payment;
	}

	public String getConsumerName() {
		return consumerName;
	}

	public void setConsumerName(String consumerName) {
		this.consumerName = consumerName;
	}

	public String getServiceProvider() {
		return serviceProvider;
	}

	public void setServiceProvider(String serviceProvider) {
		this.serviceProvider = serviceProvider;
	}	

}
