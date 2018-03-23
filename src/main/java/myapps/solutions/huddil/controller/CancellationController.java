package myapps.solutions.huddil.controller;

import java.io.IOException;

import javax.mail.MessagingException;
import javax.mail.internet.AddressException;

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
import myapps.solutions.huddil.model.CancellationCost;
import myapps.solutions.huddil.service.ICancellationService;

@RestController
public class CancellationController {

	@Autowired
	private ICancellationService cancellationService;

	@ApiOperation(value = "Calculate cancellation charges", notes = "To get the cancellation charges for a booking")
	@ApiResponses(value = { @ApiResponse(code = 3100, message = "Cancellation failed"),
			@ApiResponse(code = 3103, message = "Invalid booking id"),
			@ApiResponse(code = 3105, message = "Invalid operation type"),
			@ApiResponse(code = 3106, message = "Cannot cancel as the payment is in progress"),
			@ApiResponse(code = 3107, message = "Cannot cancel as the meeting is in progress"),
			@ApiResponse(code = 3108, message = "Booking status is neither confirmed nor pending"),
			@ApiResponse(code = 3109, message = "User is not authorized to cancel the booking"),
			@ApiResponse(code = 3110, message = "Payment mode is offline, so no refund will be made"),
			@ApiResponse(code = 3111, message = "Booking with offline mode is cancelled"),
			@ApiResponse(code = 3112, message = "Refund amount is calculated"),
			@ApiResponse(code = 3113, message = "Booking is cancelled and refund is inprogress"),
			@ApiResponse(code = 9996, message = "User is not allowed to perform this action"),
			@ApiResponse(code = 9999, message = "Session invalid/ does not exist") })
	@RequestMapping(value = "/calculateCancellationCost/", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<CancellationCost> calculateCancellationCost(@RequestParam int bookingId,
			@RequestParam String sessionId, @RequestParam String reason) throws IOException, AddressException, MessagingException {
		CancellationCost cost = cancellationService.calculateOrCancel(1, 1, bookingId, sessionId, reason);
		HttpHeaders headers = new HttpHeaders();
		headers.set("ResponseCode", cost.getResponseCode());
		return new ResponseEntity<CancellationCost>(cost, headers, HttpStatus.OK);
	}
	
	@ApiOperation(value = "Confirm Cancellation", notes = "To confirm the cancellation of a booking")
	@ApiResponses(value = { @ApiResponse(code = 3100, message = "Cancellation failed"),
			@ApiResponse(code = 3103, message = "Invalid booking id"),
			@ApiResponse(code = 3105, message = "Invalid operation type"),
			@ApiResponse(code = 3106, message = "Cannot cancel as the payment is in progress"),
			@ApiResponse(code = 3107, message = "Cannot cancel as the meeting is in progress"),
			@ApiResponse(code = 3108, message = "Booking status is neither confirmed nor pending"),
			@ApiResponse(code = 3109, message = "User is not authorized to cancel the booking"),
			@ApiResponse(code = 3110, message = "Payment mode is offline, so no refund will be made"),
			@ApiResponse(code = 3111, message = "Booking with offline mode is cancelled"),
			@ApiResponse(code = 3112, message = "Refund amount is calculated"),
			@ApiResponse(code = 3113, message = "Booking is cancelled and refund is inprogress"),
			@ApiResponse(code = 9996, message = "User is not allowed to perform this action"),
			@ApiResponse(code = 9999, message = "Session invalid/ does not exist") })
	@RequestMapping(value = "/confirmCancel/", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<CancellationCost> confirmCancel(@RequestParam int bookingId, @RequestParam String sessionId,
			@RequestParam String reason) throws IOException, AddressException, MessagingException {
		CancellationCost cost = cancellationService.calculateOrCancel(1, 2, bookingId, sessionId, reason);
		HttpHeaders headers = new HttpHeaders();
		headers.set("ResponseCode", cost.getResponseCode());
		return new ResponseEntity<CancellationCost>(cost, headers, HttpStatus.OK);
	}
}
