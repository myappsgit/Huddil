package myapps.solutions.huddil.dao.impl;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.mail.MessagingException;
import javax.mail.internet.AddressException;
import javax.persistence.EntityManager;
import javax.persistence.ParameterMode;
import javax.persistence.PersistenceContext;
import javax.persistence.StoredProcedureQuery;

import org.apache.http.client.ClientProtocolException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import myapps.payment.service.InstaMojoService;
import myapps.payment.service.model.InstaMojoPayment;
import myapps.solutions.huddil.dao.IBookingDAO;
import myapps.solutions.huddil.dao.ICancellationDAO;
import myapps.solutions.huddil.model.BookingCost;
import myapps.solutions.huddil.model.BookingNotificationTemplate;
import myapps.solutions.huddil.model.BookingResultsPagination;
import myapps.solutions.huddil.model.BookingView;
import myapps.solutions.huddil.model.BookingViewPagination;
import myapps.solutions.huddil.model.CalendarBooking;
import myapps.solutions.huddil.model.CalendarBookingDetails;
import myapps.solutions.huddil.model.CancellationCost;
import myapps.solutions.huddil.model.CancellationResults;
import myapps.solutions.huddil.model.Facility;
import myapps.solutions.huddil.model.FacilityTiming;
import myapps.solutions.huddil.model.JobData;
import myapps.solutions.huddil.model.LeastCost;
import myapps.solutions.huddil.model.SpBookingStatus;
import myapps.solutions.huddil.model.UpdateBooking;
import myapps.solutions.huddil.model.UserPref;
import myapps.solutions.huddil.model.UserSearchResult;
import myapps.solutions.huddil.service.impl.CustomScheduler;
import myapps.solutions.huddil.utils.Notifications;
import myapps.solutions.huddil.utils.ResponseCode;
import myapps.solutions.huddil.utils.UserType;

@Transactional(value = "huddilTranscationManager")
@Repository
public class BookingDAOImpl implements IBookingDAO {

	@PersistenceContext(unitName = "huddil")
	private EntityManager huddilEM;

	@Autowired
	ICancellationDAO cancellationDAO;

	private UserSearchResult getUserPreference(String sessionId) {
		Object obj = huddilEM.createNativeQuery(
				"SELECT userId AS id, displayName AS name, emailId, mobileNo, mobileNoVerified, CAST(userType AS SIGNED) AS userType FROM user_pref WHERE sessionId = :sessionId",
				"user_pref").setParameter("sessionId", sessionId).getSingleResult();
		if (obj == null)
			return null;
		return (UserSearchResult) obj;
	}

	@SuppressWarnings("unchecked")
	@Override
	public BookingViewPagination bookingFilter(String sessionId, int cityId, int localityId, int month, int status,
			int typeId, int pageNo, int count) {
		StoredProcedureQuery spQuery;
		if (status != 0) {
			spQuery = huddilEM.createStoredProcedureQuery("booking", "booking_filter")
					.registerStoredProcedureParameter("v_sessionId", String.class, ParameterMode.IN)
					.registerStoredProcedureParameter("v_cityId", Integer.class, ParameterMode.IN)
					.registerStoredProcedureParameter("v_localityId", Integer.class, ParameterMode.IN)
					.registerStoredProcedureParameter("v_month", Integer.class, ParameterMode.IN)
					.registerStoredProcedureParameter("v_status", Integer.class, ParameterMode.IN)
					.registerStoredProcedureParameter("v_type", Integer.class, ParameterMode.IN)
					.registerStoredProcedureParameter("v_pageNo", Integer.class, ParameterMode.IN)
					.registerStoredProcedureParameter("v_count", Integer.class, ParameterMode.INOUT)
					.setParameter("v_sessionId", sessionId).setParameter("v_cityId", cityId)
					.setParameter("v_localityId", localityId).setParameter("v_month", month)
					.setParameter("v_status", status).setParameter("v_type", typeId).setParameter("v_pageNo", pageNo)
					.setParameter("v_count", count);
			spQuery.execute();
		} else {
			spQuery = huddilEM.createStoredProcedureQuery("getBookingAndCancellation", "booking_filter")
					.registerStoredProcedureParameter("v_sessionId", String.class, ParameterMode.IN)
					.registerStoredProcedureParameter("v_cityId", Integer.class, ParameterMode.IN)
					.registerStoredProcedureParameter("v_localityId", Integer.class, ParameterMode.IN)
					.registerStoredProcedureParameter("v_month", Integer.class, ParameterMode.IN)
					.registerStoredProcedureParameter("v_type", Integer.class, ParameterMode.IN)
					.registerStoredProcedureParameter("v_pageNo", Integer.class, ParameterMode.IN)
					.registerStoredProcedureParameter("v_count", Integer.class, ParameterMode.INOUT)
					.setParameter("v_sessionId", sessionId).setParameter("v_cityId", cityId)
					.setParameter("v_localityId", localityId).setParameter("v_month", month)
					.setParameter("v_type", typeId).setParameter("v_pageNo", pageNo).setParameter("v_count", count);
			spQuery.execute();
		}

		int value = Integer.parseInt(spQuery.getOutputParameterValue("v_count").toString());
		if (value >= 0)
			return new BookingViewPagination(spQuery.getOutputParameterValue("v_count").toString(),
					spQuery.getResultList());
		else if (value == -1)
			return new BookingViewPagination(2);
		else
			return new BookingViewPagination(1);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<BookingView> getBooking(String sessionId, int bookingId) {
		UserSearchResult userPref = getUserPreference(sessionId);
		List<BookingView> bookings = new ArrayList<BookingView>();
		if (userPref == null)
			bookings.add(new BookingView(-1));
		else if (userPref.getUserType() == UserType.administrator)
			bookings.add(new BookingView(-2));
		else if (userPref.getUserType() == UserType.serviceprovider)
			bookings = huddilEM.createNativeQuery(
					"SELECT DISTINCT b.id as bookingId, b.fromTime as bookedFrom, b.toTime as bookedTo, b.bookedTime, b.totalPrice, b.paymentMethod, b.status, f.title, f.typeName, lo.name, lo.address, p.displayName, p.emailId, p.mobileNo, b.seats from huddil.booking b JOIN huddil.facility f ON f.id = b.facilityId "
							+ "JOIN huddil.location lo ON lo.id = f.locationId JOIN huddil.user_pref p ON b.userId = p.userId "
							+ "WHERE b.id = :id AND f.spUserId = :spUserId AND b.status <> 0 UNION SELECT DISTINCT b.bookingId, b.fromDateTime as bookedFrom, b.toDateTime as bookedTo, b.bookedTime, b.price as totalPrice, b.paymentMethod, 5 as status, f.title, f.typeName, lo.name, lo.address, p.displayName, p.emailId, "
							+ "p.mobileNo, b.seats from huddil.booking_history b JOIN huddil.facility f ON f.id = b.facilityId "
							+ "JOIN huddil.location lo ON lo.id = f.locationId JOIN huddil.user_pref p ON b.userId = p.userId "
							+ "WHERE b.bookingId = :id AND f.spUserId = :spUserId  UNION SELECT DISTINCT b.bookingId, b.bookedFrom, b.bookedTo, b.bookedTime, b.totalPrice, b.paymentMethod, IF(b.bookedStatus = 4, 4, 2) as status , f.title, f.typeName, lo.name, lo.address, "
							+ "p.displayName, p.emailId, p.mobileNo, b.seats from huddil.cancellation b JOIN huddil.facility f ON f.id = b.facilityId "
							+ "JOIN huddil.location lo ON lo.id = f.locationId JOIN huddil.user_pref p ON b.bookedUserId = p.userId "
							+ "WHERE b.bookingId = :id AND f.spUserId = :spUserId",
					"booking_filter").setParameter("id", bookingId).setParameter("spUserId", userPref.getId())
					.getResultList();
		else if (userPref.getUserType() == UserType.consumer)
			bookings = huddilEM.createNativeQuery(
					"SELECT DISTINCT b.id as bookingId, b.fromTime as bookedFrom, b.toTime as bookedTo, b.bookedTime, b.totalPrice, b.paymentMethod, b.status, f.title, f.typeName, lo.name, lo.address, "
							+ "p.displayName, p.emailId, p.mobileNo, b.seats from huddil.booking b JOIN huddil.facility f ON f.id = b.facilityId "
							+ "JOIN huddil.location lo ON lo.id = f.locationId JOIN huddil.user_pref p ON b.userId = p.userId "
							+ "WHERE b.id = :id AND b.userId = :userId AND b.status <> 0 UNION SELECT DISTINCT b.bookingId, b.fromDateTime as bookedFrom, b.toDateTime as bookedTo, b.bookedTime, b.price as totalPrice, b.paymentMethod, 5 as status, f.title, f.typeName, lo.name, lo.address, p.displayName, p.emailId, "
							+ "p.mobileNo, b.seats from huddil.booking_history b JOIN huddil.facility f ON f.id = b.facilityId "
							+ "JOIN huddil.location lo ON lo.id = f.locationId JOIN huddil.user_pref p ON b.userId = p.userId "
							+ "WHERE b.bookingId = :id AND b.userId = :userId UNION SELECT DISTINCT b.bookingId, b.bookedFrom, b.bookedTo, b.bookedTime, b.totalPrice, b.paymentMethod, IF(b.bookedStatus = 4, 4, 2) as status, "
							+ "f.title, f.typeName, lo.name, lo.address, p.displayName, p.emailId, p.mobileNo, b.seats from huddil.cancellation b "
							+ "JOIN huddil.facility f ON f.id = b.facilityId "
							+ "JOIN huddil.location lo ON lo.id = f.locationId JOIN huddil.user_pref p ON b.bookedUserId = p.userId "
							+ "WHERE b.bookingId = :id AND b.bookedUserId = :userId",
					"booking_filter").setParameter("id", bookingId).setParameter("userId", userPref.getId())
					.getResultList();
		else
			bookings = huddilEM.createNativeQuery(
					"SELECT DISTINCT b.id as bookingId, b.fromTime as bookedFrom, b.toTime as bookedTo, b.bookedTime, b.totalPrice, b.paymentMethod, b.status, f.title, f.typeName, lo.name, lo.address, p.displayName, p.emailId, p.mobileNo, b.seats from huddil.booking b JOIN huddil.facility f ON f.id = b.facilityId "
							+ "JOIN huddil.location lo ON lo.id = f.locationId JOIN huddil.user_pref p ON b.userId = p.userId "
							+ "WHERE b.id = :id AND b.status <> 0 UNION SELECT DISTINCT b.bookingId, b.fromDateTime as bookedFrom, b.toDateTime as bookedTo, b.bookedTime, b.price as totalPrice, b.paymentMethod, 5 as status, f.title, f.typeName, lo.name, lo.address, p.displayName, p.emailId, "
							+ "p.mobileNo, b.seats from huddil.booking_history b JOIN huddil.facility f ON f.id = b.facilityId "
							+ "JOIN huddil.location lo ON lo.id = f.locationId JOIN huddil.user_pref p ON b.userId = p.userId "
							+ "WHERE b.bookingId = :id UNION SELECT DISTINCT b.bookingId, b.bookedFrom, b.bookedTo, b.bookedTime, b.totalPrice, b.paymentMethod, IF(b.bookedStatus = 4, 4, 2) as status , f.title, f.typeName, lo.name, lo.address, "
							+ "p.displayName, p.emailId, p.mobileNo, b.seats from huddil.cancellation b JOIN huddil.facility f ON f.id = b.facilityId "
							+ "JOIN huddil.location lo ON lo.id = f.locationId JOIN huddil.user_pref p ON b.bookedUserId = p.userId "
							+ "WHERE b.bookingId = :id",
					"booking_filter").setParameter("id", bookingId).getResultList();
		return bookings;
	}

	@SuppressWarnings("unchecked")
	@Override
	public BookingViewPagination getBookings(String sessionId, String emailId, int pageNo, int count) {
		StoredProcedureQuery spQuery = null;
		Object obj = huddilEM.createQuery("SELECT u FROM UserPref u WHERE u.sessionId = :sessionId")
				.setParameter("sessionId", sessionId).getSingleResult();
		BookingViewPagination bookings = new BookingViewPagination();
		if (obj == null)
			bookings.setId(-1);
		else if (((UserPref) obj).getUserType() != UserType.advisor)
			bookings.setId(-2);
		else {
			spQuery = huddilEM.createStoredProcedureQuery("bookingsPagination", "booking_filter")
					.registerStoredProcedureParameter("v_sessionId", String.class, ParameterMode.IN)
					.registerStoredProcedureParameter("v_operation", Integer.class, ParameterMode.IN)
					.registerStoredProcedureParameter("v_search", String.class, ParameterMode.IN)
					.registerStoredProcedureParameter("v_pageNo", Integer.class, ParameterMode.IN)
					.registerStoredProcedureParameter("v_count", Integer.class, ParameterMode.INOUT)
					.setParameter("v_sessionId", sessionId).setParameter("v_operation", 4)
					.setParameter("v_search", emailId).setParameter("v_pageNo", pageNo).setParameter("v_count", count);
			spQuery.execute();
			bookings = new BookingViewPagination(spQuery.getOutputParameterValue("v_count").toString(),
					spQuery.getResultList());
		}
		return bookings;
	}

	@Override
	public UpdateBooking updateBookingStatusBySP(String sessionId, int bookingId, boolean status, boolean confirm)
			throws AddressException, IOException, MessagingException {
		int flag = 0;
		Double refundAmount = 0.0;
		if (status) {
			StoredProcedureQuery spQuery = huddilEM.createStoredProcedureQuery("updateBookingStatusBySP")
					.registerStoredProcedureParameter("p_sessionId", String.class, ParameterMode.IN)
					.registerStoredProcedureParameter("p_bookingId", Integer.class, ParameterMode.IN)
					.registerStoredProcedureParameter("p_status", Boolean.class, ParameterMode.IN)
					.registerStoredProcedureParameter("p_flag", Integer.class, ParameterMode.OUT)
					.setParameter("p_sessionId", sessionId).setParameter("p_bookingId", bookingId)
					.setParameter("p_status", status);
			spQuery.execute();
			flag = (int) spQuery.getOutputParameterValue("p_flag");
			return new UpdateBooking(flag);
		} else {
			if (!confirm) {
				CancellationCost result = cancellationDAO.calculateOrCancel(1, 1, bookingId, sessionId, "");
				if (result.getResponseCode() == ResponseCode.invalidSessionId)
					return new UpdateBooking(1);
				else if (result.getResponseCode() == ResponseCode.accessRestricted)
					return new UpdateBooking(2);
				refundAmount = result.getRefundAmt();
				return new UpdateBooking(11, refundAmount.toString());
			} else {
				CancellationCost p_result = cancellationDAO.calculateOrCancel(1, 2, bookingId, sessionId, "");
                if (p_result.getResponseCode() == ResponseCode.CancelBookingFailed) {
                    cancellationDAO.calculateOrCancel(4, 1, bookingId, sessionId, "");
                    return new UpdateBooking(12);
                } else if (p_result.getResponseCode() == ResponseCode.CancelBookingOnlineCancelled)
                    return new UpdateBooking(13);
                else if (p_result.getResponseCode() == ResponseCode.CancelBookingOfflineCancelled)
                    return new UpdateBooking(14);
                else if (p_result.getResponseCode() == ResponseCode.CancelBookingMeetingInProgress)
                    return new UpdateBooking(16);
                else
                    return new UpdateBooking(15);
			}
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<SpBookingStatus> getBookingStatus(String sessionId) {
		UserSearchResult user = getUserPreference(sessionId);
		List<SpBookingStatus> bookingStatus = new ArrayList<SpBookingStatus>();
		if (user == null)
			bookingStatus.add(new SpBookingStatus(-1));
		else if (user.getUserType() != UserType.serviceprovider)
			bookingStatus.add(new SpBookingStatus(-2));
		else
			bookingStatus = huddilEM.createNativeQuery(
					"SELECT CAST(COUNT(b.id) AS CHAR) AS count, 'Pending' AS name FROM booking b JOIN facility f ON f.id = b.facilityId "
							+ "WHERE f.spUserId = :id AND b.status = 1 UNION ALL "
							+ "SELECT COUNT(c.id) AS count, 'Cancelled' AS name FROM cancellation c JOIN facility f ON f.id = c.facilityId "
							+ "WHERE f.spUserId = :id AND c.bookedStatus <> 4 UNION ALL "
							+ "SELECT COUNT(b.id) AS count, 'Confirmed' AS name FROM booking b JOIN facility f ON f.id = b.facilityId "
							+ "WHERE f.spUserId = :id AND b.status = 3 UNION ALL "
							+ "SELECT COUNT(b.id) AS count,'Denied' AS name FROM cancellation b JOIN facility f ON f.id = b.facilityId "
							+ "WHERE f.spUserId = :id AND b.bookedStatus = 4",
					"sp_booking_status").setParameter("id", user.getId()).getResultList();
		return bookingStatus;
	}

	@Override
	public BookingCost calculateOrBook(Date fromTime, Date toTime, int capacity, int facilityId, String sessionId,
			int operation, String paymentMethod, String paymentId, int bookingId, String redirectUrl)
			throws ClientProtocolException, IOException, URISyntaxException, MessagingException {
		InstaMojoPayment payment = null;
		if (!paymentId.isEmpty()) {
			payment = InstaMojoService.getInstance().getPaymentResponse(paymentId, "huddil");
			if (payment == null)
				return new BookingCost(ResponseCode.CreateBookingInvalidPaymentId);
			paymentMethod = payment.getMethod();
		}
		StoredProcedureQuery query = huddilEM.createStoredProcedureQuery("createBooking", "bookingConfirm")
				.registerStoredProcedureParameter("p_fromDateTime", Date.class, ParameterMode.IN)
				.registerStoredProcedureParameter("p_toDateTime", Date.class, ParameterMode.IN)
				.registerStoredProcedureParameter("p_capacity", Integer.class, ParameterMode.IN)
				.registerStoredProcedureParameter("p_facilityId", Integer.class, ParameterMode.IN)
				.registerStoredProcedureParameter("p_sessionId", String.class, ParameterMode.IN)
				.registerStoredProcedureParameter("p_operation", Integer.class, ParameterMode.IN)
				.registerStoredProcedureParameter("p_paymentMethod", String.class, ParameterMode.IN)
				.registerStoredProcedureParameter("p_paymentId", String.class, ParameterMode.IN)
				.registerStoredProcedureParameter("p_book", Integer.class, ParameterMode.INOUT)
				.registerStoredProcedureParameter("p_result", Integer.class, ParameterMode.OUT)
				.registerStoredProcedureParameter("p_cost", Double.class, ParameterMode.OUT)
				.registerStoredProcedureParameter("p_cgst", Double.class, ParameterMode.OUT)
				.registerStoredProcedureParameter("p_sgst", Double.class, ParameterMode.OUT)
				.registerStoredProcedureParameter("p_cgstCost", Double.class, ParameterMode.OUT)
				.registerStoredProcedureParameter("p_sgstCost", Double.class, ParameterMode.OUT)
				.registerStoredProcedureParameter("p_offer", Double.class, ParameterMode.OUT)
				.registerStoredProcedureParameter("p_totalCost", Double.class, ParameterMode.OUT)
				.setParameter("p_fromDateTime", fromTime).setParameter("p_toDateTime", toTime)
				.setParameter("p_capacity", capacity).setParameter("p_facilityId", facilityId)
				.setParameter("p_sessionId", sessionId == null ? "0" : sessionId).setParameter("p_operation", operation)
				.setParameter("p_paymentMethod", paymentMethod).setParameter("p_paymentId", paymentId)
				.setParameter("p_book", bookingId);
		query.execute();
		int result = Integer.parseInt(query.getOutputParameterValue("p_result").toString());
		bookingId = Integer.parseInt(query.getOutputParameterValue("p_book").toString());
		BookingCost bookingCost = null;
		BookingNotificationTemplate notificationTemplate;
		UserSearchResult user;
		Facility f;
		switch (result) {
		case -2:
			return new BookingCost(ResponseCode.CreateBookingInvalidCapacity);
		case -1:
			return new BookingCost(ResponseCode.CreateBookingInvalidTime);
		case 0:
			return new BookingCost(ResponseCode.CreateBookingFromTimeAfterToTime);
		case 1:
			return new BookingCost(ResponseCode.invalidSessionId);
		case 2:
			return new BookingCost(ResponseCode.accessRestricted);
		case 3:
			return new BookingCost(ResponseCode.CreateBookingInvalidFacilityId);
		case 4:
			return new BookingCost(ResponseCode.CreateBookingFacilityNotAvailable);
		case 5:
			return new BookingCost(ResponseCode.CreateBookingFacilityUnderMaintenance);
		case 6:
			return new BookingCost(ResponseCode.CreateBookingFromBeforeOpening);
		case 7:
			return new BookingCost(ResponseCode.CreateBookingEndAfterClosing);
		case 8:
			return new BookingCost(ResponseCode.CreateBookingCoWorkNotEnoughSeats);
		case 9:
			bookingCost = new BookingCost(ResponseCode.CreateBookingCoWorkSeats,
					Integer.parseInt(query.getOutputParameterValue("p_book").toString()),
					Double.parseDouble(query.getOutputParameterValue("p_cost").toString()),
					Double.parseDouble(query.getOutputParameterValue("p_cgst").toString()),
					Double.parseDouble(query.getOutputParameterValue("p_sgst").toString()),
					Double.parseDouble(query.getOutputParameterValue("p_cgstCost").toString()),
					Double.parseDouble(query.getOutputParameterValue("p_sgstCost").toString()),
					Double.parseDouble(query.getOutputParameterValue("p_offer").toString()),
					Double.parseDouble(query.getOutputParameterValue("p_totalCost").toString()));
			if (sessionId != null) {
				f = (Facility) query.getSingleResult();
				notificationTemplate = new BookingNotificationTemplate(fromTime, toTime, f, bookingCost);
				Notifications.sendBookingConfirmation(notificationTemplate);
				CustomScheduler.getInstance().start(getTimeDiff(toTime, true));
			}
			return bookingCost;
		case 10:
			return new BookingCost(ResponseCode.CreateBookingExists);
		case 11:
			bookingCost = new BookingCost(ResponseCode.CreateBookingAvailable,
					Integer.parseInt(query.getOutputParameterValue("p_book").toString()),
					Double.parseDouble(query.getOutputParameterValue("p_cost").toString()),
					Double.parseDouble(query.getOutputParameterValue("p_cgst").toString()),
					Double.parseDouble(query.getOutputParameterValue("p_sgst").toString()),
					Double.parseDouble(query.getOutputParameterValue("p_cgstCost").toString()),
					Double.parseDouble(query.getOutputParameterValue("p_sgstCost").toString()),
					Double.parseDouble(query.getOutputParameterValue("p_offer").toString()),
					Double.parseDouble(query.getOutputParameterValue("p_totalCost").toString()));
			if (bookingId != 0) {
				user = getUserPreference(sessionId);
				if (!paymentMethod.equals("offline")) {
					bookingCost.setPaymentUrl(InstaMojoService.getInstance().createPaymentRequest(user.getUserName(),
							user.getEmailId(), user.isMobileNoVerified() ? user.getMobileNo() : "", "Paying to Huddil",
							bookingCost.getTotalCost(), redirectUrl, "huddil"));
					CustomScheduler.getInstance().start(7);
					bookingCost.setResponseCode(ResponseCode.CreateBookingCreated);
				} else {
					bookingCost.setResponseCode(ResponseCode.CreateBookingConfirmed);
					f = (Facility) query.getSingleResult();
					notificationTemplate = new BookingNotificationTemplate(fromTime, toTime, f, bookingCost);
					Notifications.sendBookingConfirmation(notificationTemplate);
					CustomScheduler.getInstance().start(getTimeDiff(toTime, true));
				}
			}
			return bookingCost;
		case 12:
			return new BookingCost(ResponseCode.CreateBookingInvalidBookingId);
		case 13:
			return new BookingCost(ResponseCode.CreateBookingInvalidUserId);
		case 14:
			bookingCost = new BookingCost(ResponseCode.CreateBookingConfirmed,
					Integer.parseInt(query.getOutputParameterValue("p_book").toString()),
					Double.parseDouble(query.getOutputParameterValue("p_cost").toString()),
					Double.parseDouble(query.getOutputParameterValue("p_cgst").toString()),
					Double.parseDouble(query.getOutputParameterValue("p_sgst").toString()),
					Double.parseDouble(query.getOutputParameterValue("p_cgstCost").toString()),
					Double.parseDouble(query.getOutputParameterValue("p_sgstCost").toString()),
					Double.parseDouble(query.getOutputParameterValue("p_offer").toString()),
					Double.parseDouble(query.getOutputParameterValue("p_totalCost").toString()));
			f = (Facility) query.getSingleResult();
			notificationTemplate = new BookingNotificationTemplate(fromTime, toTime, f, bookingCost);
			Notifications.sendBookingConfirmation(notificationTemplate);
			CustomScheduler.getInstance().start(getTimeDiff(toTime, true));
			return bookingCost;
		case 15:
			return new BookingCost(ResponseCode.CreateBookingDupPaymentId);
		case 16:
			return new BookingCost(ResponseCode.CreateBookingFacilityClosed);
		case 17:
			return new BookingCost(ResponseCode.CreateBookingFacilityPriceChanged);
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	@Override
	public BookingResultsPagination getBookingsByConsumer(String sessionId, int count, int pageNo) {
		StoredProcedureQuery spQuery = null;
		UserSearchResult user = getUserPreference(sessionId);
		if (user == null)
			return new BookingResultsPagination(-1);
		else if (user.getUserType() != UserType.consumer)
			return new BookingResultsPagination(-2);
		else {
			spQuery = huddilEM.createStoredProcedureQuery("bookingsPagination", "booking_details_consumer")
					.registerStoredProcedureParameter("v_sessionId", String.class, ParameterMode.IN)
					.registerStoredProcedureParameter("v_operation", Integer.class, ParameterMode.IN)
					.registerStoredProcedureParameter("v_search", String.class, ParameterMode.IN)
					.registerStoredProcedureParameter("v_pageNo", Integer.class, ParameterMode.IN)
					.registerStoredProcedureParameter("v_count", Integer.class, ParameterMode.INOUT)
					.setParameter("v_sessionId", sessionId).setParameter("v_operation", 1).setParameter("v_search", "")
					.setParameter("v_pageNo", pageNo).setParameter("v_count", count);
			spQuery.execute();
			return new BookingResultsPagination(spQuery.getOutputParameterValue("v_count").toString(),
					spQuery.getResultList());
		}
		/*
		 * results = huddilEM.createNativeQuery(
		 * "SELECT b.id,b.bookedTime, b.facilityId as facilityId, CAST(0 AS SIGNED) AS bookingId, IF(b.paymentMethod = 'offline', 'Offline', 'Online') AS paymentMode, f.title, f.typeName as typeName, f.cityName, f.localityName, lo.name as locationName, lo.address, lo.landmark, b.fromTime, b.toTime, b.totalPrice, s.name as status FROM huddil.booking b JOIN huddil.facility f ON b.facilityId = f.id JOIN huddil.location lo ON f.locationId = lo.id JOIN huddil.user_pref p ON b.userId = p.userId JOIN huddil.booking_status s ON b.status = s.id WHERE p.sessionId =:sessionId AND (b.status = 1 OR b.status = 3 OR b.status =5)"
		 * , "booking_details_consumer").setParameter("sessionId",
		 * sessionId).getResultList();
		 */
	}

	@SuppressWarnings("unchecked")
	@Override
	public BookingResultsPagination getBookingsHistoryByConsumer(String sessionId, int count, int pageNo) {
		// List<BookingResults> results = new ArrayList<BookingResults>();
		StoredProcedureQuery spQuery = null;
		UserSearchResult user = getUserPreference(sessionId);
		if (user == null)
			return new BookingResultsPagination(-1);
		else if (user.getUserType() != UserType.consumer)
			return new BookingResultsPagination(-2);
		else
			spQuery = huddilEM.createStoredProcedureQuery("bookingsPagination", "booking_details_consumer")
					.registerStoredProcedureParameter("v_sessionId", String.class, ParameterMode.IN)
					.registerStoredProcedureParameter("v_operation", Integer.class, ParameterMode.IN)
					.registerStoredProcedureParameter("v_search", String.class, ParameterMode.IN)
					.registerStoredProcedureParameter("v_pageNo", Integer.class, ParameterMode.IN)
					.registerStoredProcedureParameter("v_count", Integer.class, ParameterMode.INOUT)
					.setParameter("v_sessionId", sessionId).setParameter("v_operation", 2).setParameter("v_search", "")
					.setParameter("v_pageNo", pageNo).setParameter("v_count", count);
		spQuery.execute();

		return new BookingResultsPagination(spQuery.getOutputParameterValue("v_count").toString(),
				spQuery.getResultList());

		/*
		 * results = huddilEM.createNativeQuery(
		 * "SELECT b.id,b.bookedTime, b.facilityId as facilityId, CAST(b.bookingId AS SIGNED) AS bookingId, IF(b.paymentMethod = 'offline', 'Offline', 'Online') AS paymentMode, f.title, f.typeName as typeName, f.cityName, f.localityName, lo.name as locationName, lo.address, lo.landmark, b.fromDateTime as fromTime, b.toDateTime as toTime, b.price as totalPrice, 'confirmed' as status FROM huddil.booking_history b JOIN huddil.facility f ON b.facilityId = f.id JOIN huddil.location lo ON f.locationId = lo.id JOIN huddil.user_pref p ON b.userId = p.userId WHERE p.sessionId =:sessionId"
		 * , "booking_details_consumer").setParameter("sessionId",
		 * sessionId).getResultList();
		 */
	}

	@SuppressWarnings("unchecked")
	@Override
	public BookingResultsPagination getBookingsCancellationByConsumer(String sessionId, int count, int pageNo) {
		// List<BookingResults> results = new ArrayList<BookingResults>();
		StoredProcedureQuery spQuery = null;
		UserSearchResult user = getUserPreference(sessionId);
		if (user == null)
			return new BookingResultsPagination(-1);
		else if (user.getUserType() != UserType.consumer)
			return new BookingResultsPagination(-2);
		else
			spQuery = huddilEM.createStoredProcedureQuery("bookingsPagination", "cancel_details_consumer")
					.registerStoredProcedureParameter("v_sessionId", String.class, ParameterMode.IN)
					.registerStoredProcedureParameter("v_operation", Integer.class, ParameterMode.IN)
					.registerStoredProcedureParameter("v_search", String.class, ParameterMode.IN)
					.registerStoredProcedureParameter("v_pageNo", Integer.class, ParameterMode.IN)
					.registerStoredProcedureParameter("v_count", Integer.class, ParameterMode.INOUT)
					.setParameter("v_sessionId", sessionId).setParameter("v_operation", 3).setParameter("v_search", "")
					.setParameter("v_pageNo", pageNo).setParameter("v_count", count);
		spQuery.execute();
		return new BookingResultsPagination(spQuery.getOutputParameterValue("v_count").toString(),
				spQuery.getResultList());

		/*
		 * results = huddilEM.createNativeQuery(
		 * "SELECT b.id,b.bookedTime, b.facilityId as facilityId, CAST(0 AS SIGNED) AS bookingId, IF(b.paymentMethod = 'offline', 'Offline', 'Online') AS paymentMode, f.title, f.typeName as typeName,  f.cityName, f.localityName, lo.name as locationName, lo.address, lo.landmark, b.bookedFrom as fromTime, b.bookedTo as toTime, b.totalPrice, b.refundAmount, IF(b.refundId != 'null', 'Refund', 'No Refund') as status FROM huddil.cancellation b JOIN huddil.facility f ON b.facilityId = f.id JOIN huddil.location lo ON f.locationId = lo.id JOIN huddil.user_pref p ON b.bookedUserId = p.userId WHERE p.sessionId =:sessionId"
		 * , "cancel_details_consumer").setParameter("sessionId",
		 * sessionId).getResultList(); return results
		 */
	}

	@SuppressWarnings("unchecked")
	@Override
	public CalendarBookingDetails calendarBookings(Date fromTime, int facilityId) {
		StoredProcedureQuery spQuery = huddilEM.createStoredProcedureQuery("calendar", "calendar_bookings")
				.registerStoredProcedureParameter("v_startdate", Date.class, ParameterMode.IN)
				.registerStoredProcedureParameter("v_facilityId", Integer.class, ParameterMode.IN)
				.setParameter("v_startdate", fromTime).setParameter("v_facilityId", facilityId);
		spQuery.execute();
		List<CalendarBooking> booking = spQuery.getResultList();
		List<FacilityTiming> timing = huddilEM
				.createQuery("SELECT t FROM FacilityTiming t JOIN FETCH t.facility f WHERE f.id =:facilityId")
				.setParameter("facilityId", facilityId).getResultList();
		return new CalendarBookingDetails(booking, timing);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<JobData> getNextJob(boolean sendMail) {
		StoredProcedureQuery query;
		try {
			if (sendMail) {
				query = huddilEM.createStoredProcedureQuery("getDataForReminder", "reminderData")
						.registerStoredProcedureParameter("p_sendMail", Boolean.class, ParameterMode.IN)
						.setParameter("p_sendMail", true);
				query.execute();
				return query.getResultList();
			} else {
				query = huddilEM.createStoredProcedureQuery("getDataForReminder", "jobData")
						.registerStoredProcedureParameter("p_sendMail", Boolean.class, ParameterMode.IN)
						.setParameter("p_sendMail", false);
				query.execute();
				return query.getResultList();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public void moveCompleteBooking(String bookingId) {
		huddilEM.createStoredProcedureQuery("moveBookingData")
				.registerStoredProcedureParameter("p_bookingId", String.class, ParameterMode.IN)
				.setParameter("p_bookingId", bookingId).execute();
	}

	@SuppressWarnings("unchecked")
	@Override
	public BookingViewPagination getBookingsCancellationBySP(String sessionId, int cityId, int localityId, int month,
			int status, int typeId, int pageNo, int count) {
		StoredProcedureQuery spQuery = huddilEM.createStoredProcedureQuery("cancellationDetails", "booking_filter")
				.registerStoredProcedureParameter("v_sessionId", String.class, ParameterMode.IN)
				.registerStoredProcedureParameter("v_cityId", Integer.class, ParameterMode.IN)
				.registerStoredProcedureParameter("v_localityId", Integer.class, ParameterMode.IN)
				.registerStoredProcedureParameter("v_month", Integer.class, ParameterMode.IN)
				.registerStoredProcedureParameter("v_status", Integer.class, ParameterMode.IN)
				.registerStoredProcedureParameter("v_typeId", Integer.class, ParameterMode.IN)
				.registerStoredProcedureParameter("v_pageNo", Integer.class, ParameterMode.IN)
				.registerStoredProcedureParameter("v_count", Integer.class, ParameterMode.INOUT)
				.setParameter("v_sessionId", sessionId).setParameter("v_cityId", cityId)
				.setParameter("v_localityId", localityId).setParameter("v_month", month)
				.setParameter("v_status", status).setParameter("v_typeId", typeId).setParameter("v_pageNo", pageNo)
				.setParameter("v_count", count);
		spQuery.execute();
		int value = Integer.parseInt(spQuery.getOutputParameterValue("v_count").toString());
		if (value >= 0)
			return new BookingViewPagination(spQuery.getOutputParameterValue("v_count").toString(),
					spQuery.getResultList());
		else if (value == -1)
			return new BookingViewPagination(1);
		else
			return new BookingViewPagination(2);

	}

	private long getTimeDiff(Date date, boolean minute) {
		if (minute)
			return ((date.getTime() - new Date().getTime()) / 60000);
		else
			return ((date.getTime() - new Date().getTime()) / 3600000);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<LeastCost> leastCost(boolean populate) {
		StoredProcedureQuery query = huddilEM.createStoredProcedureQuery("populateLeastCost")
				.registerStoredProcedureParameter("populate", Boolean.class, ParameterMode.IN)
				.setParameter("populate", populate);
		query.execute();
		if (populate)
			return null;
		else
			return query.getResultList();
	}

	@Override
	public CancellationResults getCancellationDetails(String sessionId, int id) {
		UserSearchResult user = getUserPreference(sessionId);
		if (user == null)
			return new CancellationResults(-1);
		else if (user.getUserType() != UserType.serviceprovider && user.getUserType() != UserType.advisor)
			return new CancellationResults(-2);
		Object obj;
		if (user.getUserType() == UserType.serviceprovider)
			obj = huddilEM.createNativeQuery(
					"SELECT c.bookingId, c.bookedTime, c.facilityId as facilityId, IF(c.paymentMethod = 'offline', 'Offline', 'Online') AS paymentMethod, f.title, f.typeName as typeName, f.cityName, "
							+ "f.localityName, lo.name as name, lo.address, lo.landmark, c.bookedFrom, c.bookedTo, "
							+ "c.totalPrice, c.refundAmount, IF(c.refundId != 'null', 'Refund', 'No Refund') as cancelledStatus, IF(c.refundId != 'null', c.refundId, null) as refundId, c.seats, p.displayName, p.mobileNo, p.emailId, c.cancelledDateTime as cancelledDate, IF(c.bookedStatus = 4, 'Denied', 'Cancelled') as status FROM huddil.cancellation c "
							+ "JOIN huddil.facility f ON f.id = c.facilityId "
							+ "JOIN huddil.location lo ON lo.id = f.locationId "
							+ "JOIN huddil.user_pref p ON c.bookedUserId = p.userId "
							+ "WHERE c.bookingId =:id AND f.spUserId =:spId",
					"cancel_details_consumer").setParameter("id", id).setParameter("spId", user.getId())
					.getSingleResult();

		else
			obj = huddilEM.createNativeQuery(
					"SELECT c.bookingId, c.bookedTime, c.facilityId as facilityId, IF(c.paymentMethod = 'offline', 'Offline', 'Online') AS paymentMethod, f.title, f.typeName as typeName, f.cityName, "
							+ "f.localityName, lo.name as name, lo.address, lo.landmark, c.bookedFrom, c.bookedTo, "
							+ "c.totalPrice, c.refundAmount, IF(c.refundId != 'null', 'Refund', 'No Refund') as cancelledStatus, IF(c.refundId != 'null', c.refundId, null) as refundId, c.seats, p.displayName, p.mobileNo, p.emailId, c.cancelledDateTime as cancelledDate, IF(c.bookedStatus = 4, 'Denied', 'Cancelled') as status FROM huddil.cancellation c "
							+ "JOIN huddil.facility f ON f.id = c.facilityId "
							+ "JOIN huddil.location lo ON lo.id = f.locationId "
							+ "JOIN huddil.user_pref p ON c.bookedUserId = p.userId " + "WHERE c.bookingId =:id",
					"cancel_details_consumer").setParameter("id", id).getSingleResult();

		if (obj == null)
			return new CancellationResults(-3);
		else
			return (CancellationResults) obj;

	}

	@Override
	public BookingView getCompletedBookingDetails(String sessionId, int id) {
		UserSearchResult user = getUserPreference(sessionId);
		if (user == null)
			return new BookingView(-1);
		else if (user.getUserType() != UserType.serviceprovider && user.getUserType() != UserType.advisor)
			return new BookingView(-2);
		Object obj;
		if (user.getUserType() == UserType.serviceprovider)
			obj = huddilEM.createNativeQuery(
					"SELECT h.bookingId, h.fromDateTime as bookedFrom, h.toDateTime as bookedTo, h.bookedTime, h.price as totalPrice, h.paymentMethod, 5 as status, f.title, f.typeName, lo.name, lo.address, p.displayName, p.emailId, p.mobileNo, h.seats FROM huddil.booking_history h JOIN huddil.facility f ON f.id = h.facilityId JOIN huddil.location lo ON lo.id = f.locationId JOIN huddil.user_pref p ON h.userId = p.userId WHERE h.bookingId =:id AND f.spUserId =:spId",
					"booking_filter").setParameter("id", id).setParameter("spId", user.getId()).getSingleResult();

		else
			obj = huddilEM.createNativeQuery(
					"SELECT h.bookingId, h.fromDateTime as bookedFrom, h.toDateTime as bookedTo, h.bookedTime, h.price as totalPrice, h.paymentMethod, 5 as status, f.title, f.typeName, lo.name, lo.address, p.displayName, p.emailId, p.mobileNo, h.seats FROM huddil.booking_history h JOIN huddil.facility f ON f.id = h.facilityId JOIN huddil.location lo ON lo.id = f.locationId JOIN huddil.user_pref p ON h.userId = p.userId WHERE h.bookingId =:id",
					"booking_filter").setParameter("id", id).getSingleResult();
		if (obj == null)
			return new BookingView(-3);
		else
			return (BookingView) obj;
	}

	@Override
	public void deleteOffersaddCommission() {
		huddilEM.createStoredProcedureQuery("schedulerHelper").execute();
	}

}
