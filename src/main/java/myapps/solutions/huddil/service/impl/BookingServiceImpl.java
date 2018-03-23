package myapps.solutions.huddil.service.impl;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Date;
import java.util.List;

import javax.mail.MessagingException;
import javax.mail.internet.AddressException;

import org.apache.http.client.ClientProtocolException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import myapps.solutions.huddil.dao.IBookingDAO;
import myapps.solutions.huddil.model.BookingCost;
import myapps.solutions.huddil.model.BookingResultsPagination;
import myapps.solutions.huddil.model.BookingView;
import myapps.solutions.huddil.model.BookingViewPagination;
import myapps.solutions.huddil.model.CalendarBookingDetails;
import myapps.solutions.huddil.model.CancellationResults;
import myapps.solutions.huddil.model.LeastCost;
import myapps.solutions.huddil.model.SpBookingStatus;
import myapps.solutions.huddil.model.UpdateBooking;
import myapps.solutions.huddil.service.IBookingService;

@Service
public class BookingServiceImpl implements IBookingService {

	@Autowired
	private IBookingDAO bookingDao;

	@Override
	public BookingViewPagination bookingFilter(String sessionId, int cityId, int localityId, int month, int status,
			int typeId, int pageNo, int count) {
		return bookingDao.bookingFilter(sessionId, cityId, localityId, month, status, typeId, pageNo, count);
	}

	@Override
	public List<BookingView> getBooking(String sessionId, int bookingId) {
		return bookingDao.getBooking(sessionId, bookingId);
	}

	@Override
	public BookingViewPagination getBookings(String sessionId, String emailId, int pageNo, int count) {
		return bookingDao.getBookings(sessionId, emailId, pageNo, count);
	}

	@Override
	public UpdateBooking updateBookingStatusBySP(String sessionId, int bookingId, boolean status, boolean confirm)
			throws AddressException, IOException, MessagingException {
		return bookingDao.updateBookingStatusBySP(sessionId, bookingId, status, confirm);
	}

	@Override
	public List<SpBookingStatus> getBookingStatus(String sessionId) {
		return bookingDao.getBookingStatus(sessionId);
	}

	@Override
	public BookingCost calculateOrBook(Date fromTime, Date toTime, int capacity, int facilityId, String sessionId,
			int operation, String paymentMethod, String paymentId, int bookingId, String redirectUrl)
			throws ClientProtocolException, IOException, URISyntaxException, MessagingException {
		return bookingDao.calculateOrBook(fromTime, toTime, capacity, facilityId, sessionId, operation, paymentMethod,
				paymentId, bookingId, redirectUrl);
	}

	@Override
	public BookingResultsPagination getBookingsByConsumer(String sessionId, int count, int pageNo) {
		return bookingDao.getBookingsByConsumer(sessionId, count, pageNo);
	}

	@Override
	public BookingResultsPagination getBookingsHistoryByConsumer(String sessionId, int count, int pageNo) {
		return bookingDao.getBookingsHistoryByConsumer(sessionId, count, pageNo);
	}

	@Override
	public BookingResultsPagination getBookingsCancellationByConsumer(String sessionId, int count, int pageNo) {
		return bookingDao.getBookingsCancellationByConsumer(sessionId, count, pageNo);
	}

	@Override
	public CalendarBookingDetails calendarBookings(Date fromTime, int facilityId) {
		return bookingDao.calendarBookings(fromTime, facilityId);
	}

	@Override
	public BookingViewPagination getBookingsCancellationBySP(String sessionId, int cityId, int localityId, int month,
			int status, int typeId, int pageNo, int count) {
		return bookingDao.getBookingsCancellationBySP(sessionId, cityId, localityId, month, status, typeId, pageNo,
				count);

	}

	@Override
	public List<LeastCost> leastCost() {
		return bookingDao.leastCost(false);
	}

	@Override
	public CancellationResults getCancellationDetails(String sessionId, int id) {
		return 	bookingDao.getCancellationDetails(sessionId, id);
	}

	@Override
	public BookingView getCompletedBookingDetails(String sessionId, int id) {
		return bookingDao.getCompletedBookingDetails(sessionId, id);
	}
}
