package myapps.solutions.huddil.controller;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import javax.mail.MessagingException;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.multipart.MultipartFile;

import com.google.api.client.util.IOUtils;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import myapps.solutions.huddil.model.AdminFacilityDB;
import myapps.solutions.huddil.model.AdminPaymentDB;
import myapps.solutions.huddil.model.AdminUserDB;
import myapps.solutions.huddil.model.Amenity;
import myapps.solutions.huddil.model.CallMeBack;
import myapps.solutions.huddil.model.City;
import myapps.solutions.huddil.model.CommissionDetails;
import myapps.solutions.huddil.model.Events;
import myapps.solutions.huddil.model.FacilityType;
import myapps.solutions.huddil.model.FileInfo;
import myapps.solutions.huddil.model.Locality;
import myapps.solutions.huddil.model.Status;
import myapps.solutions.huddil.service.IMiscService;
import myapps.solutions.huddil.utils.ResponseCode;

@RestController
public class MiscController {

	@Autowired
	private IMiscService miscService;

	@Autowired
	ServletContext context;

	@ApiOperation(value = "Add Facility Type", notes = "To add a new facility type by administrator")
	@ApiResponses(value = { @ApiResponse(code = 2001, message = "Facility Type Add Successful"),
			@ApiResponse(code = 2002, message = "Facility Type Add Failure"),
			@ApiResponse(code = 2003, message = "Facility Type Already Added"),
			@ApiResponse(code = 9996, message = "Access Restricted"),
			@ApiResponse(code = 9999, message = "Invalid SessionId") })
	@RequestMapping(value = "/facilityType/{sessionId}/{facilityType}", method = RequestMethod.POST)
	public ResponseEntity<Void> facilityType(@PathVariable("facilityType") String facilityType,
			@PathVariable("sessionId") String sessionId) {
		HttpHeaders headers = new HttpHeaders();
		int facility = miscService.facilityType(sessionId, facilityType);
		if (facility == -1)
			headers.set("ResponseCode", ResponseCode.invalidSessionId);
		else if (facility == -2)
			headers.set("ResponseCode", ResponseCode.accessRestricted);
		if (facility == 0)
			headers.set("ResponseCode", ResponseCode.FacilityTypeAddSuccessful);
		else if (facility == 1)
			headers.set("ResponseCode", ResponseCode.FacilityTypeAlreadyAdded);
		else
			headers.set("ResponseCode", ResponseCode.FacilityTypeAddFailure);
		return new ResponseEntity<Void>(headers, HttpStatus.OK);

	}

	@ApiOperation(value = "Read Facility Type", notes = "To read facility type")
	@ApiResponses(value = { @ApiResponse(code = 2011, message = "Facility Type Read Successful"),
			@ApiResponse(code = 2012, message = "Facility Type Read Failure") })
	@RequestMapping(value = "/facilityType/", method = RequestMethod.GET)
	public ResponseEntity<List<FacilityType>> facilityType() {
		HttpHeaders headers = new HttpHeaders();
		List<FacilityType> facilityType = miscService.facilityType();
		if (facilityType != null)
			headers.set("ResponseCode", ResponseCode.FacilityTypeReadSuccessful);
		else
			headers.set("ResponseCode", ResponseCode.FacilityTypeReadFailure);
		return new ResponseEntity<List<FacilityType>>(facilityType, headers, HttpStatus.OK);

	}

	@ApiOperation(value = "Add City", notes = "To add a new city to the city list by product owner")
	@ApiResponses(value = { @ApiResponse(code = 2221, message = "City Add Successful"),
			@ApiResponse(code = 2222, message = "City Add Failure"),
			@ApiResponse(code = 2223, message = "City Already Exist"),
			@ApiResponse(code = 9996, message = "Access Restricted"),
			@ApiResponse(code = 9999, message = "Invalid SessionId") })
	@RequestMapping(value = "/city/{sessionId}", method = RequestMethod.POST)
	public ResponseEntity<Void> city(@RequestBody City city, @PathVariable("sessionId") String sessionId) {
		HttpHeaders headers = new HttpHeaders();
		int cit = miscService.city(city, sessionId);
		if (cit == -1)
			headers.set("ResponseCode", ResponseCode.invalidSessionId);
		else if (cit == -2)
			headers.set("ResponseCode", ResponseCode.accessRestricted);
		if (cit == 0)
			headers.set("ResponseCode", ResponseCode.CityAddSuccess);
		else if (cit == 1)
			headers.set("ResponseCode", ResponseCode.CityAlreadyAdded);
		else
			headers.set("ResponseCode", ResponseCode.CityAddFailure);
		return new ResponseEntity<Void>(headers, HttpStatus.OK);
	}

	@ApiOperation(value = "Read City", notes = "To Read the list of cities by users")
	@ApiResponses(value = { @ApiResponse(code = 2231, message = "City Read Successful"),
			@ApiResponse(code = 2232, message = "City Read Failure") })
	@RequestMapping(value = "/city/", method = RequestMethod.GET)
	public ResponseEntity<List<City>> city() {
		HttpHeaders headers = new HttpHeaders();
		headers.set("ResponseCode", ResponseCode.CityAddSuccess);
		List<City> city = miscService.city();
		if (city != null)
			headers.set("ResponseCode", ResponseCode.ReadCitySuccessful);
		else
			headers.set("ResponseCode", ResponseCode.ReadCityFailure);
		return new ResponseEntity<List<City>>(city, headers, HttpStatus.OK);
	}

	@ApiOperation(value = "Add Amenity", notes = "To add an amenity to the list of amenities by product owner")
	@ApiResponses(value = { @ApiResponse(code = 2271, message = "Amenity Add Successful"),
			@ApiResponse(code = 2272, message = "Amenity Add Failure"),
			@ApiResponse(code = 2273, message = "Amenity Already Exist"),
			@ApiResponse(code = 9996, message = "User is not allowed to perform this action"),
			@ApiResponse(code = 9999, message = "Session invalid/ does not exist") })
	@RequestMapping(value = "/amenity/{sessionId}", method = RequestMethod.POST)
	public ResponseEntity<Void> amenity(@RequestBody Amenity amenity, @PathVariable("sessionId") String sessionId) {
		HttpHeaders headers = new HttpHeaders();
		int amenities = miscService.amenity(amenity, sessionId);
		if (amenities == -1)
			headers.set("ResponseCode", ResponseCode.invalidSessionId);
		else if (amenities == -2)
			headers.set("ResponseCode", ResponseCode.accessRestricted);
		else if (amenities == 0)
			headers.set("ResponseCode", ResponseCode.AmenityAlreadyExist);
		else if (amenities == 1)
			headers.set("ResponseCode", ResponseCode.AmenityAddFailure);
		else if (amenities == 2)
			headers.set("ResponseCode", ResponseCode.AmenityAddSuccess);
		return new ResponseEntity<Void>(headers, HttpStatus.OK);
	}

	@ApiOperation(value = "Read Amenity", notes = "To read the list of amenities by users")
	@ApiResponses(value = { @ApiResponse(code = 2281, message = "Amenity Read Successful"),
			@ApiResponse(code = 2282, message = "Amenity Read Failure") })
	@RequestMapping(value = "/amenity/", method = RequestMethod.GET)
	public ResponseEntity<List<Amenity>> amenity() {
		HttpHeaders headers = new HttpHeaders();
		List<Amenity> amenities = miscService.amenity();
		if (amenities != null)
			headers.set("ResponseCode", ResponseCode.AmenityReadSuccess);
		else
			headers.set("ResponseCode", ResponseCode.AmenityReadFailure);
		return new ResponseEntity<List<Amenity>>(amenities, headers, HttpStatus.OK);
	}

	@ApiOperation(value = "Delete Amenity", notes = "To delete an amenity by product owner")
	@ApiResponses(value = { @ApiResponse(code = 2291, message = "Amenity Delete Successful"),
			@ApiResponse(code = 2292, message = "Amenity Delete Failure"),
			@ApiResponse(code = 9996, message = "Access Restricted"),
			@ApiResponse(code = 9999, message = "Invalid SessionId") })
	@RequestMapping(value = "/amenity/{sessionId}/{id}", method = RequestMethod.DELETE)
	public ResponseEntity<Void> amenity(@PathVariable("sessionId") String sessionId, @PathVariable("id") int id) {
		HttpHeaders headers = new HttpHeaders();
		int value = miscService.amenity(sessionId, id);
		if (value == -1)
			headers.set("ResponseCode", ResponseCode.invalidSessionId);
		else if (value == -2)
			headers.set("ResponseCode", ResponseCode.accessRestricted);
		else if (value == 0)
			headers.set("ResponseCode", ResponseCode.AmenityDeleteSuccess);
		else
			headers.set("ResponseCode", ResponseCode.AmenityDeleteFailure);
		return new ResponseEntity<Void>(headers, HttpStatus.OK);
	}

	@ApiOperation(value = "Add Locality", notes = "To add a new locality by administrator")
	@ApiResponses(value = { @ApiResponse(code = 2381, message = "Locality Add Successful"),
			@ApiResponse(code = 2382, message = "Locality Add Failure"),
			@ApiResponse(code = 2383, message = "Locality Already Added"),
			@ApiResponse(code = 9996, message = "Access Restricted"),
			@ApiResponse(code = 9999, message = "Invalid SessionId") })
	@RequestMapping(value = "/locality/{sessionId}", method = RequestMethod.POST)
	public ResponseEntity<Void> addlocality(@PathVariable("sessionId") String sessionId,
			@RequestBody Locality locality) {
		HttpHeaders headers = new HttpHeaders();
		int local = miscService.addLocality(sessionId, locality);
		if (local == -1)
			headers.set("ResponseCode", ResponseCode.invalidSessionId);
		else if (local == -2)
			headers.set("ResponseCode", ResponseCode.accessRestricted);
		else if (local == 0)
			headers.set("ResponseCode", ResponseCode.LocalityAddSuccessful);
		else if (local == 1)
			headers.set("ResponseCode", ResponseCode.LocalityAlreadyExist);
		else
			headers.set("ResponseCode", ResponseCode.LocalityAddFailure);
		return new ResponseEntity<Void>(headers, HttpStatus.OK);
	}

	@ApiOperation(value = "Read Locality", notes = "To read the localities in a city")
	@ApiResponses(value = { @ApiResponse(code = 2391, message = "Read Locality Successful"),
			@ApiResponse(code = 2392, message = "Read Locality Failure") })
	@RequestMapping(value = "/localities/{id}", method = RequestMethod.GET)
	public ResponseEntity<List<Locality>> getLocality(@PathVariable("id") int id) {
		HttpHeaders headers = new HttpHeaders();
		List<Locality> loc = miscService.getLocality(id);
		if (loc != null)
			headers.set("ResponseCode", ResponseCode.ReadLocalitySuccessful);
		else
			headers.set("ResponseCode", ResponseCode.ReadLocalityFailure);
		return new ResponseEntity<List<Locality>>(loc, headers, HttpStatus.OK);
	}

	@ApiOperation(value = "Upload Files", notes = "To upload images")
	@ApiResponses(value = { @ApiResponse(code = 2471, message = "File Upload Successful"),
			@ApiResponse(code = 2472, message = "File Upload Failure") })
	@RequestMapping(value = "/fileupload/", method = RequestMethod.POST)
	public ResponseEntity<List<String>> upload(@RequestParam MultipartFile[] inputFile)
			throws IllegalStateException, IOException, MultipartException {
		HttpHeaders headers = new HttpHeaders();
		if (inputFile.length != 0) {
			List<String> location = miscService.uploadFile(inputFile);
			if (location != null)
				headers.set("ResponseCode", ResponseCode.FileUploadSuccessful);
			else
				headers.set("ResponseCode", ResponseCode.FileUploadFailure);
			return new ResponseEntity<List<String>>(location, headers, HttpStatus.OK);

		} else {
			return new ResponseEntity<List<String>>(HttpStatus.OK);
		}
	}

	@ApiOperation(value = "To dowload file", notes = "To dowload files")
	@ApiResponses(value = { @ApiResponse(code = 2621, message = "Status Read successful"),
			@ApiResponse(code = 2473, message = "File Read Successful"),
			@ApiResponse(code = 2474, message = "File Read Failure") })
	@RequestMapping(value = "/downloadFile/", method = RequestMethod.POST)
	public void downloadFile(@RequestBody FileInfo fileInfo, HttpServletResponse response) throws IOException {
		InputStream file = null;
		HttpHeaders headers = new HttpHeaders();
		if (fileInfo.getFileName() != null) {
			file = miscService.downloadFile(fileInfo.getFileName());
			if (file != null) {
				response.setContentType(MediaType.IMAGE_JPEG_VALUE);
				response.addHeader("ResponseCode", ResponseCode.FileReadSuccessful);
				IOUtils.copy(file, response.getOutputStream());
			} else
				headers.set("ResponseCode", ResponseCode.FileReadFailure);
		} else
			headers.set("ResponseCode", ResponseCode.FileReadFailure);
	}

	@ApiOperation(value = "To Read Events", notes = "To get the events")
	@ApiResponses(value = { @ApiResponse(code = 2501, message = "Read Event Successful"),
			@ApiResponse(code = 9996, message = "User is not allowed to perform this action"),
			@ApiResponse(code = 9999, message = "Invalid sessionId/ no session exists") })

	@RequestMapping(value = "/events/", method = RequestMethod.GET)
	public ResponseEntity<List<Events>> eventsList(@RequestParam String sessionId, @RequestParam int pageNo) {
		HttpHeaders headers = new HttpHeaders();
		List<Events> events = miscService.getEvents(sessionId, pageNo);
		if (events.isEmpty() || events == null) {
			headers.set("ResponseCode", ResponseCode.ReadEventSuccessful);
			headers.set("count", "0");
		} else if (events.get(0).getId() == -1)
			headers.set("ResponseCode", ResponseCode.invalidSessionId);
		else if (events.get(0).getId() == -2)
			headers.set("ResponseCode", ResponseCode.invalidUserType);
		else {
			headers.set("ResponseCode", ResponseCode.ReadEventSuccessful);
			headers.set("count", Integer.toString(events.get(0).getCount()));
		}
		return new ResponseEntity<List<Events>>(events, headers, HttpStatus.OK);
	}

	@RequestMapping(value = "/events/", method = RequestMethod.PUT)
	public ResponseEntity<Void> events(@RequestParam String sessionId, @RequestParam int id) {
		return new ResponseEntity<Void>(HttpStatus.OK);
	}

	@ApiOperation(value = "To Read Status", notes = "To get all the status of facility by advisor")
	@ApiResponses(value = { @ApiResponse(code = 2621, message = "Status Read successful"),
			@ApiResponse(code = 2622, message = "Status Read Failure"),
			@ApiResponse(code = 9996, message = "User is not allowed to perform this action"),
			@ApiResponse(code = 9999, message = "Invalid sessionId/ no session exists") })
	@RequestMapping(value = "/status/{sessionId}", method = RequestMethod.GET)
	public ResponseEntity<List<Status>> events(@PathVariable("sessionId") String sessionId) {
		HttpHeaders headers = new HttpHeaders();
		List<Status> status = miscService.status(sessionId);
		if (status.isEmpty())
			headers.set("ResponseCode", ResponseCode.ReadStatusSuccessful);
		else if (status.get(0).getId() == -1)
			headers.set("ResponseCode", ResponseCode.invalidSessionId);
		else if (status.get(0).getId() == -2)
			headers.set("ResponseCode", ResponseCode.accessRestricted);
		else
			headers.set("ResponseCode", ResponseCode.ReadStatusSuccessful);
		return new ResponseEntity<List<Status>>(status, headers, HttpStatus.OK);
	}

	@ApiOperation(value = "Admin User Stats", notes = "To get user stats for admin dashboard")
	@ApiResponses(value = { @ApiResponse(code = 4001, message = "User stats read successful"),
			@ApiResponse(code = 9996, message = "User is not allowed to perform this action"),
			@ApiResponse(code = 9999, message = "Invalid sessionId/ no session exists") })
	@RequestMapping(value = "/statsUser/", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<AdminUserDB>> statsUser(@RequestParam String sessionId) {
		List<AdminUserDB> adminUserDBs = miscService.getStatsUser(sessionId);
		HttpHeaders headers = new HttpHeaders();
		if (adminUserDBs.get(0).getResult() == -2)
			headers.set("ResponseCode", ResponseCode.invalidSessionId);
		else if (adminUserDBs.get(0).getResult() == -1)
			headers.set("ResponseCode", ResponseCode.accessRestricted);
		else
			headers.set("ResponseCode", ResponseCode.AdminDBUserReadSuccessful);
		return new ResponseEntity<List<AdminUserDB>>(adminUserDBs, headers, HttpStatus.OK);
	}

	@ApiOperation(value = "Admin Facility Stats", notes = "To get facility stats for admin dashboard")
	@ApiResponses(value = { @ApiResponse(code = 4002, message = "Facility stats read successful"),
			@ApiResponse(code = 9996, message = "User is not allowed to perform this action"),
			@ApiResponse(code = 9999, message = "Invalid sessionId/ no session exists") })
	@RequestMapping(value = "/statsFacility/", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<AdminFacilityDB>> statsFacility(@RequestParam String sessionId) {
		List<AdminFacilityDB> adminFacilityDBs = miscService.getStatsFacility(sessionId);
		HttpHeaders headers = new HttpHeaders();
		if (adminFacilityDBs.get(0).getResult() == -2)
			headers.set("ResponseCode", ResponseCode.invalidSessionId);
		else if (adminFacilityDBs.get(0).getResult() == -1)
			headers.set("ResponseCode", ResponseCode.accessRestricted);
		else
			headers.set("ResponseCode", ResponseCode.AdminDBFacilityReadSuccessful);
		return new ResponseEntity<List<AdminFacilityDB>>(adminFacilityDBs, headers, HttpStatus.OK);
	}

	@ApiOperation(value = "Admin Payment Stats", notes = "To get payment stats for admin dashboard")
	@ApiResponses(value = { @ApiResponse(code = 4003, message = "Payment stats read successful"),
			@ApiResponse(code = 9996, message = "User is not allowed to perform this action"),
			@ApiResponse(code = 9999, message = "Invalid sessionId/ no session exists") })
	@RequestMapping(value = "/statsPayment/", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<AdminPaymentDB> paymentStats(@RequestParam String sessionId, @RequestParam int month,
			@RequestParam int year) {
		AdminPaymentDB adminPaymentDB = miscService.getStatsPayment(sessionId, month, year);
		HttpHeaders headers = new HttpHeaders();
		if (adminPaymentDB.getStatus() == -2)
			headers.set("ResponseCode", ResponseCode.invalidSessionId);
		else if (adminPaymentDB.getStatus() == -1)
			headers.set("ResponseCode", ResponseCode.accessRestricted);
		else
			headers.set("ResponseCode", ResponseCode.AdminDBPaymentReadSuccessful);
		return new ResponseEntity<AdminPaymentDB>(adminPaymentDB, headers, HttpStatus.OK);
	}

	@ApiOperation(value = "Admin Payment Stats", notes = "To get payment stats for admin dashboard based on month, city, sp name, sp id")
	@ApiResponses(value = { @ApiResponse(code = 4003, message = "Payment stats read successful"),
			@ApiResponse(code = 9996, message = "User is not allowed to perform this action"),
			@ApiResponse(code = 9999, message = "Invalid sessionId/ no session exists") })
	@RequestMapping(value = "/adminStatsPayments/", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<AdminPaymentDB>> adminPaymentStats(@RequestParam String sessionId,
			@RequestParam int month, @RequestParam int year, @RequestParam String city, @RequestParam String spName,
			@RequestParam int spId) {
		List<AdminPaymentDB> adminPaymentDBs = miscService.getPaymentsForAdmin(sessionId, month, year, city, spName,
				spId);
		HttpHeaders headers = new HttpHeaders();
		if (adminPaymentDBs.isEmpty())
			headers.set("ResponseCode", ResponseCode.AdminDBPaymentReadSuccessful);
		else if (adminPaymentDBs.get(0).getStatus() == -2)
			headers.set("ResponseCode", ResponseCode.invalidSessionId);
		else if (adminPaymentDBs.get(0).getStatus() == -1)
			headers.set("ResponseCode", ResponseCode.accessRestricted);
		else
			headers.set("ResponseCode", ResponseCode.AdminDBPaymentReadSuccessful);
		return new ResponseEntity<List<AdminPaymentDB>>(adminPaymentDBs, headers, HttpStatus.OK);
	}

	@ApiOperation(value = "Update Commission From SP", notes = "To update the SP commission percentage by admin")
	@ApiResponses(value = { @ApiResponse(code = 4011, message = "Commission Updated Successfully"),
			@ApiResponse(code = 4013, message = "Same Commission Exist For the Month"),
			@ApiResponse(code = 4012, message = "Commission Update Failure"),
			@ApiResponse(code = 9996, message = "User is not allowed to perform this action"),
			@ApiResponse(code = 9999, message = "Invalid sessionId/ no session exists") })
	@RequestMapping(value = "/updateSpCommission", method = RequestMethod.POST)
	public ResponseEntity<Void> updateSPCommission(@RequestParam String sessionId, @RequestParam int spUserId,
			@RequestParam int month, @RequestParam int year, @RequestParam double commission) {
		HttpHeaders headers = new HttpHeaders();
		int response = miscService.updateSPCommission(sessionId, spUserId, month, year, commission);
		System.out.println(response);
		if (response == -1)
			headers.set("ResponseCode", ResponseCode.invalidSessionId);
		else if (response == -2)
			headers.set("ResponseCode", ResponseCode.accessRestricted);
		else if (response == 1)
			headers.set("ResponseCode", ResponseCode.CommissionUpdatedSuccessfully);
		else if (response == 3)
			headers.set("ResponseCode", ResponseCode.SameCommissionExistForMonth);
		else
			headers.set("ResponseCode", ResponseCode.CommissionUpdationFailure);
		return new ResponseEntity<Void>(headers, HttpStatus.OK);
	}

	@ApiOperation(value = "Get Commission for SP's ByAdmin", notes = "To get the commission percentage for sp's by admin")
	@ApiResponses(value = { @ApiResponse(code = 4014, message = "Commission Read Successfull"),
			@ApiResponse(code = 4015, message = "Commission Read Failure") })
	@RequestMapping(value = "/getSPCommissionByAdmin", method = RequestMethod.GET)
	public ResponseEntity<List<CommissionDetails>> getCommissionByAdmin(@RequestParam String ids,
			@RequestParam int month, @RequestParam int year) {
		HttpHeaders headers = new HttpHeaders();
		List<CommissionDetails> commissionDetails = miscService.getCommissionByAdmin(ids, month, year);
		if (commissionDetails != null)
			headers.set("ResponseCode", ResponseCode.CommissionReadSuccessfull);
		else
			headers.set("ResponseCode", ResponseCode.CommissionReadFailure);
		return new ResponseEntity<List<CommissionDetails>>(commissionDetails, headers, HttpStatus.OK);
	}

	@ApiOperation(value = "Send an enquiry to SP for a facility")
	@ApiResponses(value = { @ApiResponse(code = 2485, message = "Facility not found"),
			@ApiResponse(code = 9995, message = "Enquiry send successfully") })
	@RequestMapping(value = "/sendEnquiry/", method = RequestMethod.POST)
	public ResponseEntity<Void> sendEnquiry(@RequestParam int facilityId, @RequestBody CallMeBack callMeBack)
			throws MessagingException, IOException {
		HttpHeaders headers = new HttpHeaders();
		int result = miscService.sendEnquiry(callMeBack, facilityId);
		if (result == 0)
			headers.set("ResponseCode", ResponseCode.FacilityNotFound);
		else
			headers.set("ResponseCode", ResponseCode.EnquiryMailSend);
		return new ResponseEntity<>(headers, HttpStatus.OK);
	}

}
