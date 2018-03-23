package myapps.solutions.huddil.model;
// Generated Nov 17, 2017 4:19:00 PM by Hibernate Tools 5.2.1.Final

import static javax.persistence.GenerationType.IDENTITY;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.ColumnResult;
import javax.persistence.ConstructorResult;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.SqlResultSetMapping;
import javax.persistence.SqlResultSetMappings;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * Booking generated by hbm2java
 */
@SqlResultSetMappings({
		@SqlResultSetMapping(name = "booking_filter", classes = {
				@ConstructorResult(targetClass = BookingView.class, columns = { @ColumnResult(name = "bookingId"),
						@ColumnResult(name = "bookedFrom"), @ColumnResult(name = "bookedTo"),
						@ColumnResult(name = "bookedTime"), @ColumnResult(name = "totalPrice"),
						@ColumnResult(name = "paymentMethod"), @ColumnResult(name = "status"),
						@ColumnResult(name = "title"), @ColumnResult(name = "typeName"), @ColumnResult(name = "name"),
						@ColumnResult(name = "address"), @ColumnResult(name = "displayName"),
						@ColumnResult(name = "emailId"), @ColumnResult(name = "mobileNo"),
						@ColumnResult(name = "seats") }) }),
		@SqlResultSetMapping(name = "booking_details_consumer", classes = {
				@ConstructorResult(targetClass = BookingResults.class, columns = { @ColumnResult(name = "bookingId"),
						@ColumnResult(name = "bookedTime"), @ColumnResult(name = "facilityId"),
						@ColumnResult(name = "paymentMethod"), @ColumnResult(name = "title"),
						@ColumnResult(name = "typeName"), @ColumnResult(name = "cityName"),
						@ColumnResult(name = "localityName"), @ColumnResult(name = "locationName"),
						@ColumnResult(name = "address"), @ColumnResult(name = "landmark"),
						@ColumnResult(name = "bookedFrom"), @ColumnResult(name = "bookedTo"),
						@ColumnResult(name = "totalPrice"), @ColumnResult(name = "status"),
						@ColumnResult(name = "seats"), @ColumnResult(name = "displayName"),
						@ColumnResult(name = "mobileNo"), @ColumnResult(name = "emailId") }) }),
		@SqlResultSetMapping(name = "cancel_details_consumer", classes = {
				@ConstructorResult(targetClass = CancellationResults.class, columns = {
						@ColumnResult(name = "bookingId"), @ColumnResult(name = "bookedTime"),
						@ColumnResult(name = "facilityId"), @ColumnResult(name = "paymentMethod"),
						@ColumnResult(name = "title"), @ColumnResult(name = "typeName"),
						@ColumnResult(name = "cityName"), @ColumnResult(name = "localityName"),
						@ColumnResult(name = "name"), @ColumnResult(name = "address"), @ColumnResult(name = "landmark"),
						@ColumnResult(name = "bookedFrom"), @ColumnResult(name = "bookedTo"),
						@ColumnResult(name = "totalPrice"), @ColumnResult(name = "refundAmount"),
						@ColumnResult(name = "cancelledStatus"), @ColumnResult(name = "refundId"),
						@ColumnResult(name = "seats"), @ColumnResult(name = "displayName"),
						@ColumnResult(name = "mobileNo"), @ColumnResult(name = "emailId"),
						@ColumnResult(name = "cancelledDate"), @ColumnResult(name = "status") }) }),
		@SqlResultSetMapping(name = "sp_booking_status", classes = {
				@ConstructorResult(targetClass = SpBookingStatus.class, columns = { @ColumnResult(name = "count"),
						@ColumnResult(name = "name") }) }),
		@SqlResultSetMapping(name = "spBookingConfirmCheck", classes = {
				@ConstructorResult(targetClass = SPBookingConfirmCheck.class, columns = {
						@ColumnResult(name = "facilityId"), @ColumnResult(name = "facilityType"),
						@ColumnResult(name = "facilityTypeId"), @ColumnResult(name = "bookingId"),
						@ColumnResult(name = "bookedSeats"), @ColumnResult(name = "facilitySeats"),
						@ColumnResult(name = "fromTime"), @ColumnResult(name = "toTime") }) }),
		@SqlResultSetMapping(name = "adminPaymentDB", classes = {
				@ConstructorResult(targetClass = AdminPaymentDB.class, columns = { @ColumnResult(name = "userId"),
						@ColumnResult(name = "dName"), @ColumnResult(name = "fName"), @ColumnResult(name = "lName"),
						@ColumnResult(name = "cName"), @ColumnResult(name = "onlinePay"),
						@ColumnResult(name = "onlineRef"), @ColumnResult(name = "onlineCancelCharge"),
						@ColumnResult(name = "offlinePay"), @ColumnResult(name = "offlineRef"),
						@ColumnResult(name = "tranCharge"), @ColumnResult(name = "commission"),
						@ColumnResult(name = "settlement") }) }),
		@SqlResultSetMapping(name = "jobData", classes = { @ConstructorResult(targetClass = JobData.class, columns = {
				@ColumnResult(name = "id"), @ColumnResult(name = "time"), @ColumnResult(name = "startTime") }) }),
		@SqlResultSetMapping(name = "calendar_bookings", classes = {
				@ConstructorResult(targetClass = CalendarBooking.class, columns = { @ColumnResult(name = "bookingId"),
						@ColumnResult(name = "roomType"), @ColumnResult(name = "fromTime"),
						@ColumnResult(name = "toTime"), @ColumnResult(name = "bookedSeats"),
						@ColumnResult(name = "remainingSeats"), @ColumnResult(name = "bookedBySp") }) }),
		@SqlResultSetMapping(name = "reminderData", classes = {
				@ConstructorResult(targetClass = JobData.class, columns = { @ColumnResult(name = "bookingId"),
						@ColumnResult(name = "startTime"), @ColumnResult(name = "time"),
						@ColumnResult(name = "cityName"), @ColumnResult(name = "spName"),
						@ColumnResult(name = "spEmailId"), @ColumnResult(name = "spMobileNo"),
						@ColumnResult(name = "spMobileVerified"), @ColumnResult(name = "cName"),
						@ColumnResult(name = "cMobileNo"), @ColumnResult(name = "cMobileNoVerified"),
						@ColumnResult(name = "cEmailId") }) }) })
@Entity
@Table(name = "booking", catalog = "huddil")
public class Booking implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2800270545496624931L;
	private Integer id;
	private Facility facility;
	private FacilityCancellationCharges facilityCancellationCharges;
	private UserPref userPref;
	private Date fromTime;
	private Date toTime;
	private int seats;
	private double price;
	private double totalPrice;
	private String paymentMethod;
	private String paymentId;
	private Date bookedTime;
	private int status;
	private List<Meeting> meetings = new ArrayList<Meeting>(0);

	public Booking() {
	}

	public Booking(int id) {
		this.id = id;
	}

	public Booking(Facility facility, FacilityCancellationCharges facilityCancellationCharges, UserPref userPref,
			Date fromTime, Date toTime, int seats, double price, double totalPrice, Date bookedTime, int status) {
		this.facility = facility;
		this.facilityCancellationCharges = facilityCancellationCharges;
		this.userPref = userPref;
		this.fromTime = fromTime;
		this.toTime = toTime;
		this.seats = seats;
		this.price = price;
		this.totalPrice = totalPrice;
		this.bookedTime = bookedTime;
		this.status = status;
	}

	public Booking(Facility facility, FacilityCancellationCharges facilityCancellationCharges, UserPref userPref,
			Date fromTime, Date toTime, int seats, double price, double totalPrice, String paymentMethod,
			String paymentId, Date bookedTime, int status, List<Meeting> meetings) {
		this.facility = facility;
		this.facilityCancellationCharges = facilityCancellationCharges;
		this.userPref = userPref;
		this.fromTime = fromTime;
		this.toTime = toTime;
		this.seats = seats;
		this.price = price;
		this.totalPrice = totalPrice;
		this.paymentMethod = paymentMethod;
		this.paymentId = paymentId;
		this.bookedTime = bookedTime;
		this.status = status;
		this.meetings = meetings;
	}

	@Id
	@GeneratedValue(strategy = IDENTITY)

	@Column(name = "id", unique = true, nullable = false)
	public Integer getId() {
		return this.id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "facilityId", nullable = false)
	@JsonIgnore
	public Facility getFacility() {
		return this.facility;
	}

	public void setFacility(Facility facility) {
		this.facility = facility;
	}

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "cancellationPolicyId", nullable = false)
	public FacilityCancellationCharges getFacilityCancellationCharges() {
		return this.facilityCancellationCharges;
	}

	public void setFacilityCancellationCharges(FacilityCancellationCharges facilityCancellationCharges) {
		this.facilityCancellationCharges = facilityCancellationCharges;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "userId", nullable = false)
	@JsonIgnore
	public UserPref getUserPref() {
		return this.userPref;
	}

	public void setUserPref(UserPref userPref) {
		this.userPref = userPref;
	}

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm")
	@Column(name = "fromTime", nullable = false, length = 19)
	public Date getFromTime() {
		return this.fromTime;
	}

	public void setFromTime(Date fromTime) {
		this.fromTime = fromTime;
	}

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm")
	@Column(name = "toTime", nullable = false, length = 19)
	public Date getToTime() {
		return this.toTime;
	}

	public void setToTime(Date toTime) {
		this.toTime = toTime;
	}

	@Column(name = "seats", nullable = false)
	public int getSeats() {
		return this.seats;
	}

	public void setSeats(int seats) {
		this.seats = seats;
	}

	@Column(name = "price", nullable = false, precision = 22, scale = 0)
	public double getPrice() {
		return this.price;
	}

	public void setPrice(double price) {
		this.price = price;
	}

	@Column(name = "totalPrice", nullable = false, precision = 22, scale = 0)
	public double getTotalPrice() {
		return this.totalPrice;
	}

	public void setTotalPrice(double totalPrice) {
		this.totalPrice = totalPrice;
	}

	@Column(name = "paymentMethod", length = 20)
	public String getPaymentMethod() {
		return this.paymentMethod;
	}

	public void setPaymentMethod(String paymentMethod) {
		this.paymentMethod = paymentMethod;
	}

	@Column(name = "paymentId", length = 45)
	public String getPaymentId() {
		return this.paymentId;
	}

	public void setPaymentId(String paymentId) {
		this.paymentId = paymentId;
	}

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm")
	@Column(name = "bookedTime", nullable = false, length = 19)
	public Date getBookedTime() {
		return this.bookedTime;
	}

	public void setBookedTime(Date bookedTime) {
		this.bookedTime = bookedTime;
	}

	@Column(name = "status", nullable = false)
	public int getStatus() {
		return this.status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	@OneToMany(fetch = FetchType.EAGER, mappedBy = "booking")
	public List<Meeting> getMeetings() {
		return this.meetings;
	}

	public void setMeetings(List<Meeting> meetings) {
		this.meetings = meetings;
	}

}
