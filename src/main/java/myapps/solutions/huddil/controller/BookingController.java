package myapps.solutions.huddil.controller;

import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.Timestamp;
import java.util.List;

import javax.mail.MessagingException;
import javax.mail.internet.AddressException;

import org.apache.http.client.ClientProtocolException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import myapps.solutions.huddil.model.BookingCost;
import myapps.solutions.huddil.model.BookingResults;
import myapps.solutions.huddil.model.BookingResultsPagination;
import myapps.solutions.huddil.model.BookingView;
import myapps.solutions.huddil.model.BookingViewPagination;
import myapps.solutions.huddil.model.CalendarBookingDetails;
import myapps.solutions.huddil.model.CancellationResults;
import myapps.solutions.huddil.model.LeastCost;
import myapps.solutions.huddil.model.SpBookingStatus;
import myapps.solutions.huddil.model.UpdateBooking;
import myapps.solutions.huddil.service.IBookingService;
import myapps.solutions.huddil.utils.ResponseCode;

@RestController
public class BookingController {

	@Autowired
	private IBookingService bookingService;

	@ApiOperation(value = "To view all bookings by SP or Advisor", notes = "To view all the bookings by passing in different parameters by advisor, default will be current month")
	@ApiResponses(value = { @ApiResponse(code = 2341, message = "Booking Read Successful"),
			@ApiResponse(code = 2342, message = "Booking Read Failure"),
			@ApiResponse(code = 9999, message = "Invalid/ session does not exist") })
	@RequestMapping(value = "/bookings", method = RequestMethod.GET)
	public ResponseEntity<List<BookingView>> bookingFilter(@RequestParam String sessionId, @RequestParam int cityId,
			@RequestParam int localityId, @RequestParam int month, @RequestParam int status, @RequestParam int typeId,
			@RequestParam int pageNo, @RequestParam int count) {
		HttpHeaders headers = new HttpHeaders();
		BookingViewPagination bookings;
		if (status == 4)
			bookings = bookingService.getBookingsCancellationBySP(sessionId, cityId, localityId, month, status, typeId,
					pageNo, count);
		else if (status != 2)
			bookings = bookingService.bookingFilter(sessionId, cityId, localityId, month, status, typeId, pageNo,
					count);
		else
			bookings = bookingService.getBookingsCancellationBySP(sessionId, cityId, localityId, month, status, typeId,
					pageNo, count);
		if (bookings.getId() == 0) {
			headers.set("totalRecords", bookings.getCount());
			headers.set("ResponseCode", ResponseCode.BookingReadSuccessful);
		} else if (bookings.getId() == 1)
			headers.set("ResponseCode", ResponseCode.invalidSessionId);
		else if (bookings.getId() == 2)
			headers.set("ResponseCode", ResponseCode.accessRestricted);
		else
			headers.set("ResponseCode", ResponseCode.BookingReadFailure);
		return new ResponseEntity<List<BookingView>>(bookings.getBookingViews(), headers, HttpStatus.OK);
	}

	@ApiOperation(value = "To get booking details", notes = "To get booking details for a particular booking id")
	@ApiResponses(value = { @ApiResponse(code = 2101, message = "Read Booking Successful"),
			@ApiResponse(code = 2102, message = "Read Booking Failure"),
			@ApiResponse(code = 9996, message = "User is not allowed to perform this action"),
			@ApiResponse(code = 9999, message = "Invalid/ session does not exist") })
	@RequestMapping(value = "/booking/", method = RequestMethod.GET)
	public ResponseEntity<List<BookingView>> booking(@RequestParam String sessionId, @RequestParam int bookingId) {
		List<BookingView> booking = bookingService.getBooking(sessionId, bookingId);
		HttpHeaders headers = new HttpHeaders();
		if (booking.isEmpty())
			headers.set("ResponseCode", ResponseCode.BookingReadSuccessful);
		else if (booking.get(0).getBookingId() == -1)
			headers.set("ResponseCode", ResponseCode.invalidSessionId);
		else if (booking.get(0).getBookingId() == -2)
			headers.set("ResponseCode", ResponseCode.accessRestricted);
		else
			headers.set("ResponseCode", ResponseCode.BookingReadSuccessful);
		return new ResponseEntity<List<BookingView>>(booking, headers, HttpStatus.OK);
	}

	@ApiResponses(value = { @ApiResponse(code = 2101, message = "Search booking successful"),
			@ApiResponse(code = 9996, message = "User is not allowed to perform this action"),
			@ApiResponse(code = 9998, message = "Invalid user type"),
			@ApiResponse(code = 9999, message = "Session invalid/ does not exist"), })
	@RequestMapping(value = "/bookings/", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<BookingView>> bookings(@RequestParam String sessionId, @RequestParam String emailId,
			@RequestParam int pageNo, @RequestParam int count) {
		BookingViewPagination bookings = bookingService.getBookings(sessionId, emailId, pageNo, count);
		HttpHeaders headers = new HttpHeaders();
		if (bookings.getId() == -1)
			headers.set("ResponseCode", ResponseCode.invalidSessionId);
		else if (bookings.getId() == -2)
			headers.set("ResponseCode", ResponseCode.accessRestricted);
		else if (bookings.getBookingViews() == null || bookings.getBookingViews().isEmpty()) {
			headers.set("totalRecords", bookings.getCount());
			headers.set("ResponseCode", ResponseCode.BookingReadSuccessful);
		}
		 else
			headers.set("ResponseCode", ResponseCode.BookingReadSuccessful);
			headers.set("totalRecords", bookings.getCount());
		return new ResponseEntity<List<BookingView>>(bookings.getBookingViews(), headers, HttpStatus.OK);
	}

	@ApiOperation(value = "To update booking status by SP", notes = "To update booking status from pending to approved or cancelled etc by SP")
	@ApiResponses(value = { @ApiResponse(code = 2631, message = "Booking Status Update Successful"),
			@ApiResponse(code = 2632, message = "Booking Status Update Failure"),
			@ApiResponse(code = 2633, message = "No Enough Seats Available"),
			@ApiResponse(code = 2634, message = "Already Updated"),
			@ApiResponse(code = 2635, message = "Refund Amount Calculated Successfully"),
			@ApiResponse(code = 3003, message = "Invalid facility id"),
			@ApiResponse(code = 3004, message = "Facility is not in acitve state"),
			@ApiResponse(code = 3100, message = "Booking cancellation failed"),
			@ApiResponse(code = 3111, message = "Offline booking cancelled successfully"),
			@ApiResponse(code = 3113, message = "Online booking cancelled successfully"),
			@ApiResponse(code = 3103, message = "Invalid booking id"),
			@ApiResponse(code = 3114, message = "Current user is not the owner of the facility"),
			@ApiResponse(code = 9996, message = "Access Restricted"),
			@ApiResponse(code = 9997, message = "Invalid input parameters"),
			@ApiResponse(code = 9999, message = "Invalid Session") })
	@RequestMapping(value = "/updateBookingStatusBySp/", method = RequestMethod.PUT)
	public ResponseEntity<Void> updateBookingStatusBySP(@RequestParam String sessionId, @RequestParam int bookingId,
			@RequestParam boolean status, @RequestParam boolean confirm)
			throws AddressException, IOException, MessagingException {
		HttpHeaders headers = new HttpHeaders();
		UpdateBooking value = bookingService.updateBookingStatusBySP(sessionId, bookingId, status, confirm);
		if (value.getResult() == 1)
			headers.set("ResponseCode", ResponseCode.invalidSessionId);
		else if (value.getResult() == 2)
			headers.set("ResponseCode", ResponseCode.accessRestricted);
		else if (value.getResult() == 3)
			headers.set("ResponseCode", ResponseCode.CancelBookingInvalidBookingId);
		else if (value.getResult() == 4)
			headers.set("ResponseCode", ResponseCode.CreateBookingInvalidFacilityId);
		else if (value.getResult() == 5)
			headers.set("ResponseCode", ResponseCode.CreateBookingFacilityNotAvailable);
		else if (value.getResult() == 6)
			headers.set("ResponseCode", ResponseCode.CancelBookingNotOwner);
		else if (value.getResult() == 7)
			headers.set("ResponseCode", ResponseCode.BookingStatusUpdateSuccessful);
		else if (value.getResult() == 8)
			headers.set("ResponseCode", ResponseCode.invalidData);
		else if (value.getResult() == 9)
			headers.set("ResponseCode", ResponseCode.BookingStatusUpdateFailure);
		else if (value.getResult() == 10)
			headers.set("ResponseCode", ResponseCode.BookingStatusUpdateFailure);
		else if (value.getResult() == 11) {
			headers.set("RefundAmount", value.getRefundAmount());
			headers.set("ResponseCode", ResponseCode.RefundAmountCalculatedSucessfully);
		} else if (value.getResult() == 12)
			headers.set("ResponseCode", ResponseCode.CancelBookingFailed);
		else if (value.getResult() == 13)
			headers.set("ResponseCode", ResponseCode.CancelBookingOnlineCancelled);
		else if (value.getResult() == 14)
			headers.set("ResponseCode", ResponseCode.CancelBookingOfflineCancelled);
		else if(value.getResult() == 16)
			headers.set("ResponseCode", ResponseCode.CancelBookingMeetingInProgress);
		else
			headers.set("ResponseCode", ResponseCode.CancelBookingFailed);
		return new ResponseEntity<Void>(headers, HttpStatus.OK);
	}

	@ApiOperation(value = "Booking Count for SP", notes = "To get booking status count for SP Dashboard")
	@ApiResponses(value = { @ApiResponse(code = 2621, message = "Booking Status Read Successful"),
			@ApiResponse(code = 9996, message = "Invalid user type"),
			@ApiResponse(code = 9999, message = "Session invalid/ does not exist") })
	@RequestMapping(value = "/spBookingStatusCnt/", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<SpBookingStatus>> groupBookingStatus(@RequestParam String sessionId) {
		HttpHeaders headers = new HttpHeaders();
		List<SpBookingStatus> bookingStatus = bookingService.getBookingStatus(sessionId);
		if (bookingStatus.isEmpty())
			headers.set("ResponseCode", ResponseCode.ReadStatusSuccessful);
		else if (bookingStatus.get(0).getCount() == -1)
			headers.set("ResponseCode", ResponseCode.invalidSessionId);
		else if (bookingStatus.get(0).getCount() == -2)
			headers.set("ResponseCode", ResponseCode.accessRestricted);
		else
			headers.set("ResponseCode", ResponseCode.ReadStatusSuccessful);
		return new ResponseEntity<List<SpBookingStatus>>(bookingStatus, headers, HttpStatus.OK);
	}

	@ApiOperation(value = "Calculate cost of booking", notes = "To calculate booking cost based on from time, to time, facility and capacity")
	@ApiResponses(value = { @ApiResponse(code = 3000, message = "From time is after to time"),
			@ApiResponse(code = 3003, message = "Invalid facility id"),
			@ApiResponse(code = 3004, message = "Facility not available for booking"),
			@ApiResponse(code = 3005, message = "Facility is under maintenance"),
			@ApiResponse(code = 3006, message = "Booking start time is before facility opening time or after closing time"),
			@ApiResponse(code = 3007, message = "Booking end time is after facility closing time or before opening time"),
			@ApiResponse(code = 3008, message = "Facility does not have enough seats"),
			@ApiResponse(code = 3009, message = "Facility have enough seats for co-working space"),
			@ApiResponse(code = 3010, message = "Already booking exist for the time specified"),
			@ApiResponse(code = 3011, message = "Facility is available for booking for non co-working space"),
			@ApiResponse(code = 3018, message = "Facility closed on the selected date(s)"),
			@ApiResponse(code = 3019, message = "From and To time is not properly provided"),
			@ApiResponse(code = 3020, message = "Capacity should be non-zero"),
			@ApiResponse(code = 3021, message = "Facility price changed"),
			@ApiResponse(code = 9996, message = "User is not allowed to perform this action"),
			@ApiResponse(code = 9999, message = "Session invalid/ does not exist") })
	@RequestMapping(value = "/calculateCost/", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<BookingCost> calculateCost(@RequestParam Timestamp fromTime, @RequestParam Timestamp toTime,
			@RequestParam int capacity, @RequestParam int facilityId, @RequestParam(required = false) String sessionId)
			throws ClientProtocolException, IOException, URISyntaxException, MessagingException {
		BookingCost bookingCost = bookingService.calculateOrBook(fromTime, toTime, capacity, facilityId, sessionId, 0,
				"", "", 0, "");
		HttpHeaders headers = new HttpHeaders();
		headers.set("ResponseCode", bookingCost.getResponseCode());
		return new ResponseEntity<BookingCost>(bookingCost, headers, HttpStatus.OK);
	}

	@RequestMapping(value = "/bookNow/", method = RequestMethod.POST)
	@ApiResponses(value = { @ApiResponse(code = 3000, message = "From time is after to time"),
			@ApiResponse(code = 3003, message = "Invalid facility id"),
			@ApiResponse(code = 3004, message = "Facility not available for booking"),
			@ApiResponse(code = 3005, message = "Facility is under maintenance"),
			@ApiResponse(code = 3006, message = "Booking start time is before facility opening time"),
			@ApiResponse(code = 3007, message = "Booking end time is after facility closing time"),
			@ApiResponse(code = 3008, message = "Facility does not have enough seats"),
			@ApiResponse(code = 3009, message = "Facility have enough seats for co-working space"),
			@ApiResponse(code = 3010, message = "Already booking exist for the time specified"),
			@ApiResponse(code = 3014, message = "Booking is confirmed"),
			@ApiResponse(code = 3016, message = "Booking entry is created"),
			@ApiResponse(code = 3018, message = "Facility closed on the selected date(s)"),
			@ApiResponse(code = 3019, message = "From and To time is not properly provided"),
			@ApiResponse(code = 3020, message = "Capacity should be non-zero"),
			@ApiResponse(code = 9996, message = "User is not allowed to perform this action"),
			@ApiResponse(code = 9999, message = "Session invalid/ does not exist") })
	public ResponseEntity<BookingCost> bookingNow(@RequestParam Timestamp fromTime, @RequestParam Timestamp toTime,
			@RequestParam int capacity, @RequestParam int facilityId, @RequestParam String sessionId,
			@RequestParam String paymentMethod, @RequestParam String redirectUrl)
			throws ClientProtocolException, IOException, URISyntaxException, MessagingException {
		HttpHeaders headers = new HttpHeaders();
		BookingCost bookingCost = bookingService.calculateOrBook(fromTime, toTime, capacity, facilityId, sessionId, 1,
				paymentMethod, "", 0, redirectUrl);
		headers.set("ResponseCode", bookingCost.getResponseCode());
		return new ResponseEntity<BookingCost>(bookingCost, headers, HttpStatus.OK);
	}

	@ApiResponses(value = { @ApiResponse(code = 3000, message = "From time is after to time"),
			@ApiResponse(code = 3003, message = "Invalid facility id"),
			@ApiResponse(code = 3004, message = "Facility not available for booking"),
			@ApiResponse(code = 3005, message = "Facility is under maintenance"),
			@ApiResponse(code = 3006, message = "Booking start time is before facility opening time"),
			@ApiResponse(code = 3007, message = "Booking end time is after facility closing time"),
			@ApiResponse(code = 3008, message = "Facility does not have enough seats"),
			@ApiResponse(code = 3009, message = "Facility have enough seats"),
			@ApiResponse(code = 3010, message = "Already booking exist for the time specified"),
			@ApiResponse(code = 3012, message = "Booking id is invalid"),
			@ApiResponse(code = 3013, message = "User id does not match"),
			@ApiResponse(code = 3014, message = "Booking is confirmed"),
			@ApiResponse(code = 3015, message = "Duplicate payment id"),
			@ApiResponse(code = 3017, message = "Invalid payment id"),
			@ApiResponse(code = 3018, message = "Facility closed on the selected date(s)"),
			@ApiResponse(code = 3019, message = "From and To time is not properly provided"),
			@ApiResponse(code = 3020, message = "Capacity should be non-zero"),
			@ApiResponse(code = 9996, message = "User is not allowed to perform this action"),
			@ApiResponse(code = 9999, message = "Session invalid/ does not exist") })
	@RequestMapping(value = "/confirmBooking/", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<BookingCost> confirmBooking(@RequestParam Timestamp fromTime, @RequestParam Timestamp toTime,
			@RequestParam int capacity, @RequestParam int facilityId, @RequestParam String sessionId,
			@RequestParam String paymentId, @RequestParam int bookingId)
			throws ClientProtocolException, IOException, URISyntaxException, MessagingException {
		HttpHeaders headers = new HttpHeaders();
		BookingCost bookingCost = bookingService.calculateOrBook(fromTime, toTime, capacity, facilityId, sessionId, 2,
				"", paymentId, bookingId, "");
		headers.set("ResponseCode", bookingCost.getResponseCode());
		return new ResponseEntity<BookingCost>(bookingCost, headers, HttpStatus.OK);
	}

	@ApiOperation(value = "Read Booking Details by Consumer", notes = "To get booking details by consumer")
	@ApiResponses(value = { @ApiResponse(code = 2101, message = "Booking Details Read Successful"),
			@ApiResponse(code = 2102, message = "Booking Details Read Failure"),
			@ApiResponse(code = 9996, message = "User is not allowed to perform this action"),
			@ApiResponse(code = 9999, message = "Invalid/ session does not exist") })
	@RequestMapping(value = "/bookingDetailsByConsumer/", method = RequestMethod.GET)
	public ResponseEntity<List<BookingResults>> getBookingDetails(@RequestParam String sessionId,
			@RequestParam int count, @RequestParam int pageNo) {
		HttpHeaders headers = new HttpHeaders();
		BookingResultsPagination results = bookingService.getBookingsByConsumer(sessionId, count, pageNo);
		if (results.getId().equals("-1"))
			headers.set("ResponseCode", ResponseCode.invalidSessionId);
		else if (results.getId().equals("-2"))
			headers.set("ResponseCode", ResponseCode.accessRestricted);
		else if (results.getBookingResults().isEmpty()) {
			headers.set("totalRecords", results.getId());
			headers.set("ResponseCode", ResponseCode.BookingReadSuccessful);
		} 
		else {
			headers.set("totalRecords", results.getId());
			headers.set("ResponseCode", ResponseCode.BookingReadSuccessful);
		}
		return new ResponseEntity<List<BookingResults>>(results.getBookingResults(), headers, HttpStatus.OK);
	}

	@ApiOperation(value = "Read Booking History Details by Consumer", notes = "To get booking history details by consumer")
	@ApiResponses(value = { @ApiResponse(code = 2101, message = "Booking Details Read Successful"),
			@ApiResponse(code = 2102, message = "Booking Details Read Failure"),
			@ApiResponse(code = 9996, message = "User is not allowed to perform this action"),
			@ApiResponse(code = 9999, message = "Invalid/ session does not exist") })
	@RequestMapping(value = "/bookingHistoryDetailsByConsumer/", method = RequestMethod.GET)
	public ResponseEntity<List<BookingResults>> getBookingHistory(@RequestParam String sessionId,
			@RequestParam int count, @RequestParam int pageNo) {
		HttpHeaders headers = new HttpHeaders();
		BookingResultsPagination results = bookingService.getBookingsHistoryByConsumer(sessionId, count, pageNo);
		if (results.getId().equals("-1"))
			headers.set("ResponseCode", ResponseCode.invalidSessionId);
		else if (results.getId().equals("-2"))
			headers.set("ResponseCode", ResponseCode.accessRestricted);
		else if (results.getBookingResults().isEmpty()) {
			headers.set("totalRecords", results.getId());
			headers.set("ResponseCode", ResponseCode.BookingReadSuccessful);
		}
		else{
			headers.set("ResponseCode", ResponseCode.BookingReadSuccessful);
			headers.set("totalRecords", results.getId());
		}
		return new ResponseEntity<List<BookingResults>>(results.getBookingResults(), headers, HttpStatus.OK);
	}

	@ApiOperation(value = "Read Booking Cancellation Details by Consumer", notes = "To get booking cancellation details by consumer")
	@ApiResponses(value = { @ApiResponse(code = 2103, message = "Booking Cancellation Details Read Successful"),
			@ApiResponse(code = 2104, message = "Booking Cancellation Details Read Failure"),
			@ApiResponse(code = 9996, message = "User is not allowed to perform this action"),
			@ApiResponse(code = 9999, message = "Invalid/ session does not exist") })
	@RequestMapping(value = "/bookingCancellationDetailsByConsumer/", method = RequestMethod.GET)
	public ResponseEntity<List<BookingResults>> getBookingCancellation(@RequestParam String sessionId,
			@RequestParam int count, @RequestParam int pageNo) {
		HttpHeaders headers = new HttpHeaders();
		BookingResultsPagination results = bookingService.getBookingsCancellationByConsumer(sessionId, count, pageNo);
		if (results.getId().equals("-1"))
			headers.set("ResponseCode", ResponseCode.invalidSessionId);
		else if (results.getId().equals("-2"))
			headers.set("ResponseCode", ResponseCode.accessRestricted);
		else if (results.getBookingResults().isEmpty()) {
			headers.set("totalRecords", results.getId());
			headers.set("ResponseCode", ResponseCode.BookingReadSuccessful);
		} 
		else{
			headers.set("totalRecords", results.getId());
		headers.set("ResponseCode", ResponseCode.BookingReadSuccessful);
		}
		return new ResponseEntity<List<BookingResults>>(results.getBookingResults(), headers, HttpStatus.OK);
	}

	@ApiOperation(value = "Read Booking Cancellation Details by SP", notes = "To get booking cancellation details by SP")
	@ApiResponses(value = { @ApiResponse(code = 2103, message = "Booking Cancellation Details Read Successful"),
			@ApiResponse(code = 2104, message = "Booking Cancellation Details Read Failure"),
			@ApiResponse(code = 9996, message = "User is not allowed to perform this action"),
			@ApiResponse(code = 9999, message = "Invalid/ session does not exist") })
	@RequestMapping(value = "/bookingCancellationDetailsBySP/", method = RequestMethod.GET)
	public ResponseEntity<List<BookingView>> getBookingCancellationBySP(@RequestParam String sessionId,
			@RequestParam int cityId, @RequestParam int localityId, @RequestParam int month, @RequestParam int status,
			@RequestParam int typeId, @RequestParam int pageNo, @RequestParam int count) {
		HttpHeaders headers = new HttpHeaders();
		BookingViewPagination results = bookingService.getBookingsCancellationBySP(sessionId, cityId, localityId, month,
				status, typeId, pageNo, count);
		if (results.getId() == 0) {
			headers.set("totalRecords", results.getCount());
			headers.set("ResponseCode", ResponseCode.CancellationReadSuccessful);
		} else if (results.getId() == 1)
			headers.set("ResponseCode", ResponseCode.invalidSessionId);
		else if (results.getId() == 2)
			headers.set("ResponseCode", ResponseCode.accessRestricted);
		else
			headers.set("ResponseCode", ResponseCode.CancellationReadFailure);
		return new ResponseEntity<List<BookingView>>(results.getBookingViews(), headers, HttpStatus.OK);
	}

	@ApiOperation(value = "Read Booking Details by Month&Year", notes = "To get booking details by Month&Year based on facilityId")
	@ApiResponses(value = { @ApiResponse(code = 2101, message = "Booking Details Read Successful"),
			@ApiResponse(code = 2102, message = "Booking Details Read Failure") })
	@RequestMapping(value = "/calendarbookings/", method = RequestMethod.GET)
	public ResponseEntity<CalendarBookingDetails> calendarBookings(@RequestParam Timestamp fromTime,
			@RequestParam int facilityId) {
		CalendarBookingDetails booking = bookingService.calendarBookings(fromTime, facilityId);
		HttpHeaders headers = new HttpHeaders();
		if (booking != null)
			headers.set("ResponseCode", ResponseCode.BookingReadSuccessful);
		else
			headers.set("ResponseCode", ResponseCode.BookingReadFailure);
		return new ResponseEntity<CalendarBookingDetails>(booking, headers, HttpStatus.OK);
	}

	@RequestMapping(value = "/leastCost/", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<LeastCost>> getLeastCost() {
		return new ResponseEntity<List<LeastCost>>(bookingService.leastCost(), HttpStatus.OK);
	}

	@ApiOperation(value = "Read Cancellation Details by SP & Advisor", notes = "To get booking cancellation details by SP & Advisor")
	@ApiResponses(value = { @ApiResponse(code = 9999, message = "Invalid SessionId"),
			@ApiResponse(code = 2101, message = "Access Restricted"),
			@ApiResponse(code = 3201, message = "Cancellation Details Read Successful"),
			@ApiResponse(code = 3202, message = "Cancellation Details Read Failure") })
	@RequestMapping(value = "/getCancellationDetailsBySP/", method = RequestMethod.GET)
	public ResponseEntity<CancellationResults> getCancellationsBySP(@RequestParam String sessionId, @RequestParam int id) {
		HttpHeaders headers = new HttpHeaders();
		CancellationResults cancellationDetails = bookingService.getCancellationDetails(sessionId, id);
		if (cancellationDetails == null)
			headers.set("ResponseCode", ResponseCode.CancellationReadFailure);
		else if (cancellationDetails.getBookingId() == -1)
			headers.set("ResponseCode", ResponseCode.invalidSessionId);
		else if (cancellationDetails.getBookingId() == -2)
			headers.set("ResponseCode", ResponseCode.accessRestricted);
		else if (cancellationDetails.getBookingId() == -3)
			headers.set("ResponseCode", ResponseCode.CancellationReadFailure);
		else
			headers.set("ResponseCode", ResponseCode.CancellationReadSuccessful);
		return new ResponseEntity<CancellationResults>(cancellationDetails, headers, HttpStatus.OK);
	}

	@ApiOperation(value = "Read Completed Booking Details by SP & Advisor", notes = "To get completed booking details by SP & Advisor")
	@ApiResponses(value = { @ApiResponse(code = 9999, message = "Invalid SessionId"),
			@ApiResponse(code = 2101, message = "Access Restricted"),
			@ApiResponse(code = 2101, message = "Booking Completion Details Read Successful"),
			@ApiResponse(code = 2102, message = "Booking Completion Details Read Failure") })
	@RequestMapping(value = "/getCompletedBookingDetailsBySP/", method = RequestMethod.GET)
	public ResponseEntity<BookingView> getCompletedBookingDetailsBySP(@RequestParam String sessionId,
			@RequestParam int id) {
		HttpHeaders headers = new HttpHeaders();
		BookingView completedDetails = bookingService.getCompletedBookingDetails(sessionId, id);
		if (completedDetails == null)
			headers.set("ResponseCode", ResponseCode.BookingReadFailure);
		else if (completedDetails.getBookingId() == -1)
			headers.set("ResponseCode", ResponseCode.invalidSessionId);
		else if (completedDetails.getBookingId() == -2)
			headers.set("ResponseCode", ResponseCode.accessRestricted);
		else if (completedDetails.getBookingId() == -3)
			headers.set("ResponseCode", ResponseCode.BookingReadFailure);
		else
			headers.set("ResponseCode", ResponseCode.BookingReadSuccessful);
		return new ResponseEntity<BookingView>(completedDetails, headers, HttpStatus.OK);

	}
}
