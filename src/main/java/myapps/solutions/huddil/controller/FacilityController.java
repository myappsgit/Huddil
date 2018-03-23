package myapps.solutions.huddil.controller;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import javax.mail.MessagingException;
import javax.mail.internet.AddressException;

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

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import myapps.solutions.huddil.model.AdminFacilityDB;
import myapps.solutions.huddil.model.AdminPaymentDB;
import myapps.solutions.huddil.model.Booking;
import myapps.solutions.huddil.model.CustomerReview;
import myapps.solutions.huddil.model.Facility;
import myapps.solutions.huddil.model.FacilityAdditionalCost;
import myapps.solutions.huddil.model.FacilityFilterResult;
import myapps.solutions.huddil.model.FacilityFilterResultPagination;
import myapps.solutions.huddil.model.FacilityHistory;
import myapps.solutions.huddil.model.FacilityOffers;
import myapps.solutions.huddil.model.FacilityUnderMaintenance;
import myapps.solutions.huddil.model.Location;
import myapps.solutions.huddil.model.LocationDetails;
import myapps.solutions.huddil.model.MultiTenant;
import myapps.solutions.huddil.model.Report;
import myapps.solutions.huddil.model.Review;
import myapps.solutions.huddil.model.SavedFacility;
import myapps.solutions.huddil.model.StatusCount;
import myapps.solutions.huddil.service.IFacilityService;
import myapps.solutions.huddil.utils.ResponseCode;

@RestController
public class FacilityController {

	@Autowired
	private IFacilityService facilityService;

	@ApiOperation(value = "Create Multitenant", notes = "To crete a multitenant by service provider")
	@ApiResponses(value = { @ApiResponse(code = 2021, message = "Multitenant Creation Successfull"),
			@ApiResponse(code = 2022, message = "Multitenant Creation Failure"),
			@ApiResponse(code = 9996, message = "User is not allowed to perform this action"),
			@ApiResponse(code = 9999, message = "Invalid/ session does not exist") })
	@RequestMapping(value = "/multitenant/{sessionId}", method = RequestMethod.POST)
	public ResponseEntity<Void> multitenant(@RequestBody MultiTenant multitenant,
			@PathVariable("sessionId") String sessionId) {
		HttpHeaders headers = new HttpHeaders();
		int result = facilityService.addMultitenant(multitenant, sessionId);
		if (result == -1)
			headers.set("ResponseCode", ResponseCode.invalidSessionId);
		else if (result == -2)
			headers.set("ResponseCode", ResponseCode.accessRestricted);
		else
			headers.set("ResponseCode", ResponseCode.MultitenantAddSuccessful);
		return new ResponseEntity<Void>(headers, HttpStatus.OK);
	}

	@ApiOperation(value = "Get Multitenant List", notes = "To get prefered multitenant list")
	@ApiResponses(value = { @ApiResponse(code = 2031, message = "Read Multitenant List Successful"),
			@ApiResponse(code = 2032, message = "Read Multitenant List Failure"),
			@ApiResponse(code = 9996, message = "User is not allowed to perform this action"),
			@ApiResponse(code = 9999, message = "Invalid/ session does not exist") })
	@RequestMapping(value = "/multitenant/{sessionId}", method = RequestMethod.GET)
	public ResponseEntity<List<MultiTenant>> multitenant(@PathVariable("sessionId") String sessionId) {
		HttpHeaders headers = new HttpHeaders();
		List<MultiTenant> multitenant = facilityService.getMultitenant(sessionId);
		if (multitenant.isEmpty())
			headers.set("ResponseCode", ResponseCode.MultitenantReadSuccessful);
		else if (multitenant.get(0).getId() == -1)
			headers.set("ResponseCode", ResponseCode.invalidSessionId);
		else if (multitenant.get(0).getId() == -2)
			headers.set("ResponseCode", ResponseCode.accessRestricted);
		else
			headers.set("ResponseCode", ResponseCode.MultitenantReadSuccessful);
		return new ResponseEntity<List<MultiTenant>>(multitenant, headers, HttpStatus.OK);
	}

	@ApiOperation(value = "Update Multitenant", notes = "To Update a Multitenant")
	@ApiResponses(value = { @ApiResponse(code = 2041, message = "Multitenant Update Successful"),
			@ApiResponse(code = 2042, message = "Multitenant Update Failure"),
			@ApiResponse(code = 9996, message = "User is not allowed to perform this action"),
			@ApiResponse(code = 9999, message = "Invalid/ session does not exist") })
	@RequestMapping(value = "/multitenant/{sessionId}", method = RequestMethod.PUT)
	public ResponseEntity<Void> updateMultitenant(@RequestBody MultiTenant multitenant,
			@PathVariable("sessionId") String sessionId) {
		HttpHeaders headers = new HttpHeaders();
		int result = facilityService.updateMultitenant(multitenant, sessionId);
		if (result == -1)
			headers.set("ResponseCode", ResponseCode.invalidSessionId);
		else if (result == -2)
			headers.set("ResponseCode", ResponseCode.accessRestricted);
		else if (result == 0)
			headers.set("ResponseCode", ResponseCode.MultitenantUpdateFailure);
		else
			headers.set("ResponseCode", ResponseCode.MultitenantUpdateSuccessful);
		return new ResponseEntity<Void>(headers, HttpStatus.OK);
	}

	/*
	 * @ApiOperation(value = "Delete Multitenant", notes =
	 * "To delete a multitenant")
	 * 
	 * @ApiResponses(value = { @ApiResponse(code = 2051, message =
	 * "Multitenant Deletion Successful"),
	 * 
	 * @ApiResponse(code = 2052, message = "Multitenant Deletion Failure") })
	 * 
	 * @RequestMapping(value = "/multitenant/{id}/{sessionId}", method =
	 * RequestMethod.DELETE) public ResponseEntity<Void>
	 * multitenant(@PathVariable("id") int id, @PathVariable("sessionId") String
	 * sessionId) { HttpHeaders headers = new HttpHeaders(); if
	 * (facilityService.deleteMultitenant(id, sessionId))
	 * headers.set("ResponseCode", ResponseCode.MultitenantDeleteSuccessful); else
	 * headers.set("ResponseCode", ResponseCode.MultitenantDeleteFailure); return
	 * new ResponseEntity<Void>(headers, HttpStatus.OK); }
	 */

	@ApiOperation(value = "Create Facility", notes = " To create facility by service provider")
	@ApiResponses(value = { @ApiResponse(code = 2111, message = "Facility Creation Successful"),
			@ApiResponse(code = 2112, message = "Facility Creation Failure"),
			@ApiResponse(code = 9996, message = "User is not allowed to perform this action"),
			@ApiResponse(code = 9999, message = "Invalid/ session does not exist") })
	@RequestMapping(value = "/facility/", method = RequestMethod.POST)
	public ResponseEntity<Void> facility(@RequestBody Facility facility, @RequestParam String sessionId)
			throws IOException {
		HttpHeaders headers = new HttpHeaders();
		int fac = facilityService.addFacility(facility, sessionId);
		if (fac == -1)
			headers.set("ResponseCode", ResponseCode.invalidSessionId);
		else if (fac == -2)
			headers.set("ResponseCode", ResponseCode.accessRestricted);
		else if (fac == 0)
			headers.set("ResponseCode", ResponseCode.FacilityAddFailure);
		else {
			headers.set("ResponseCode", ResponseCode.FacilityAddSuccessful);
			headers.set("facilityId", Integer.toString(fac));
		}
		return new ResponseEntity<Void>(headers, HttpStatus.OK);
	}

	@ApiOperation(value = "GET Facility", notes = "To Read a facility based on id")
	@ApiResponses(value = { @ApiResponse(code = 2121, message = "Facility Read Successful"),
			@ApiResponse(code = 2122, message = "Facility Read Failure"),
			@ApiResponse(code = 9999, message = "Invalid/ session does not exist") })
	@RequestMapping(value = "/facility/", method = RequestMethod.GET)
	public ResponseEntity<Facility> facility(@RequestParam int id, @RequestParam(required = false) String sessionId) {
		HttpHeaders headers = new HttpHeaders();
		Facility facility = facilityService.getFacility(sessionId, id);
		if (facility == null)
			headers.set("ResponseCode", ResponseCode.FacilityReadFailure);
		else if (facility.getId() == -1)
			headers.set("ResponseCode", ResponseCode.invalidSessionId);
		else
			headers.set("ResponseCode", ResponseCode.FacilityReadSuccessful);
		return new ResponseEntity<Facility>(facility, headers, HttpStatus.OK);
	}

	@ApiOperation(value = "GET Facility", notes = "To get all facilities by a SP")
	@ApiResponses(value = { @ApiResponse(code = 2121, message = "Facility Read Successful"),
			@ApiResponse(code = 2122, message = "Facility Read Failure"),
			@ApiResponse(code = 9996, message = "User is not allowed to perform this action"),
			@ApiResponse(code = 9999, message = "Invalid/ session does not exist") })
	@RequestMapping(value = "/facilities/", method = RequestMethod.GET)
	public ResponseEntity<List<FacilityFilterResult>> facility(@RequestParam String sessionId, @RequestParam int pageNo,
			@RequestParam int count) {
		HttpHeaders headers = new HttpHeaders();
		FacilityFilterResultPagination facility = facilityService.getFacility(sessionId, pageNo, count);
		if (facility == null)
			headers.set("ResponseCode", ResponseCode.invalidSessionId);
		else {
			headers.set("totalRecords", facility.getCount());
			headers.set("ResponseCode", ResponseCode.FacilityReadSuccessful);
		}
		return new ResponseEntity<List<FacilityFilterResult>>(facility.getFacilityFilterView(), headers, HttpStatus.OK);
	}

	@ApiOperation(value = "Read Facility based on location", notes = " To read facilities in a particular location")
	@ApiResponses(value = { @ApiResponse(code = 2121, message = "Facility Read Successful"),
			@ApiResponse(code = 2122, message = "Facility Read Failure"),
			@ApiResponse(code = 9996, message = "User is not allowed to perform this action"),
			@ApiResponse(code = 9999, message = "Invalid/ session does not exist") })
	@RequestMapping(value = "/facilities/{sessionId}/{locationId}", method = RequestMethod.GET)
	public ResponseEntity<List<Facility>> getFacilities(@PathVariable("sessionId") String sessionId,
			@PathVariable("locationId") int locationId) {
		HttpHeaders headers = new HttpHeaders();
		List<Facility> facility = facilityService.getFacilities(sessionId, locationId);
		if (facility != null)
			headers.set("ResponseCode", ResponseCode.FacilityReadSuccessful);
		else
			headers.set("ResponseCode", ResponseCode.invalidSessionId);
		return new ResponseEntity<List<Facility>>(facility, headers, HttpStatus.OK);

	}

	@ApiOperation(value = "Filter Facilities", notes = "To filter facilities based on city, locality and location")
	@ApiResponses(value = { @ApiResponse(code = 2121, message = "Facility Read Successful"),
			@ApiResponse(code = 2122, message = "Facility Read Failure"),
			@ApiResponse(code = 9996, message = "User is not allowed to perform this action"),
			@ApiResponse(code = 9999, message = "Invalid/ session does not exist") })
	@RequestMapping(value = "/filterFacility", method = RequestMethod.GET)
	public ResponseEntity<List<FacilityFilterResult>> getfacility(@RequestParam String sessionId,
			@RequestParam int cityId, @RequestParam(value = "localityId", required = false) int localityId,
			@RequestParam(value = "locationId", required = false) int locationId,
			@RequestParam(value = "typeId", required = false) int typeId,
			@RequestParam(value = "search", required = false) String search, @RequestParam(value = "status") int status,
			@RequestParam int pageNo, @RequestParam int count) {
		HttpHeaders headers = new HttpHeaders();
		FacilityFilterResultPagination facility = facilityService.getFacility(sessionId, cityId, localityId, locationId,
				typeId, search, status, pageNo, count);
		if (facility == null)
			headers.set("ResponseCode", ResponseCode.FacilityReadFailure);
		else if (facility.getId() == -1)
			headers.set("ResponseCode", ResponseCode.invalidSessionId);
		else if (facility.getId() == -2)
			headers.set("ResponseCode", ResponseCode.accessRestricted);
		else {
			headers.set("totalRecords", facility.getCount());
			headers.set("ResponseCode", ResponseCode.FacilityReadSuccessful);
		}
		return new ResponseEntity<List<FacilityFilterResult>>(facility.getFacilityFilterView(), headers, HttpStatus.OK);
	}

	@ApiOperation(value = "Facility Resubmit or Update", notes = "To resubmit or update a facility after being rejected")
	@ApiResponses(value = { @ApiResponse(code = 2481, message = "Facility Update Successful"),
			@ApiResponse(code = 2482, message = "Facility Update Failure"),
			@ApiResponse(code = 9996, message = "User is not allowed to perform this action"),
			@ApiResponse(code = 9999, message = "Invalid/ session does not exist") })
	@RequestMapping(value = "/facility/", method = RequestMethod.PUT)
	public ResponseEntity<Void> facilityUpdate(@RequestBody Facility facility, @RequestParam String sessionId) {
		HttpHeaders headers = new HttpHeaders();
		int result = facilityService.updateFacility(facility, sessionId);
		if (result == -1)
			headers.set("ResponseCode", ResponseCode.invalidSessionId);
		else if (result == -2)
			headers.set("ResponseCode", ResponseCode.accessRestricted);
		else if (result == -3)
			headers.set("ResponseCode", ResponseCode.FacilityNotFound);
		else if (result == -4)
			headers.set("ResponseCode", ResponseCode.ReadLocationFailure);
		else if (result == -5)
			headers.set("ResponseCode", ResponseCode.FacilityStatusCannotBeChanged);
		else if (result == 1)
			headers.set("ResponseCode", ResponseCode.FacilityUpdateSuccessful);
		else
			headers.set("ResponseCode", ResponseCode.FacilityUpdateFailure);
		return new ResponseEntity<Void>(headers, HttpStatus.OK);
	}

	@ApiOperation(value = "To delete a facility in saved state")
	@ApiResponses(value = { @ApiResponse(code = 2131, message = "Deleted facility successfully"),
			@ApiResponse(code = 2132, message = "Unable to delete the facility"),
			@ApiResponse(code = 2133, message = "Facility not in saved state"),
			@ApiResponse(code = 2134, message = "Current user is not the owner of the facility"),
			@ApiResponse(code = 2485, message = "Facility not found"),
			@ApiResponse(code = 9996, message = "User is not allowed to perform this action"),
			@ApiResponse(code = 9999, message = "Invalid/ session does not exist") })
	@RequestMapping(value = "/facility/", method = RequestMethod.DELETE)
	public ResponseEntity<Void> facility(@RequestParam String sessionId, @RequestParam int facilityId) {
		HttpHeaders headers = new HttpHeaders();
		int result = facilityService.deleteFacility(sessionId, facilityId);
		if (result == -1)
			headers.set("ResponseCode", ResponseCode.invalidSessionId);
		else if (result == -2)
			headers.set("ResponseCode", ResponseCode.accessRestricted);
		else if (result == -3)
			headers.set("ResponseCode", ResponseCode.FacilityNotFound);
		else if (result == -4)
			headers.set("ResponseCode", ResponseCode.FacilityDeleteNotSavedState);
		else if (result == -5)
			headers.set("ResponseCode", ResponseCode.FacilityDeleteNotOwner);
		else if (result == 0)
			headers.set("ResponseCode", ResponseCode.FacilityDeleteFailure);
		else
			headers.set("ResponseCode", ResponseCode.FacilityDeleteSuccessful);
		return new ResponseEntity<Void>(headers, HttpStatus.OK);
	}

	@ApiOperation(value = "Create Facility Offer", notes = "To create an offer for a facility by the facility owner")
	@ApiResponses(value = { @ApiResponse(code = 2131, message = "Offer Creation Successful"),
			@ApiResponse(code = 2132, message = "Offer Creation Failure"),
			@ApiResponse(code = 2143, message = "Offer Already Exist For The Given Period"),
			@ApiResponse(code = 2144, message = "FacilityIsBlocked"),
			@ApiResponse(code = 9996, message = "User is not allowed to perform this action"),
			@ApiResponse(code = 9999, message = "Invalid/ session does not exist") })
	@RequestMapping(value = "/offer/{sessionId}", method = RequestMethod.POST)
	public ResponseEntity<Void> offer(@RequestBody FacilityOffers facilityOffers,
			@PathVariable("sessionId") String sessionId) {
		HttpHeaders headers = new HttpHeaders();
		int result = facilityService.addOffer(facilityOffers, sessionId);
		if (result == -1)
			headers.set("ResponseCode", ResponseCode.invalidSessionId);
		else if (result == -2)
			headers.set("ResponseCode", ResponseCode.accessRestricted);
		else if (result == 1)
			headers.set("ResponseCode", ResponseCode.OfferAddSuccessful);
		else if (result == 2)
			headers.set("ResponseCode", ResponseCode.FacilityIsBlocked);
		else if (result == 3)
			headers.set("ResponseCode", ResponseCode.OfferAlreadyExistForTheGivenPeriod);
		else
			headers.set("ResponseCode", ResponseCode.OfferAddFailure);
		return new ResponseEntity<Void>(headers, HttpStatus.OK);
	}

	@ApiOperation(value = "Get Facility Offer as list", notes = "To get a list of offers available for a particular facility by users")
	@ApiResponses(value = { @ApiResponse(code = 2141, message = "Offer Read Successful"),
			@ApiResponse(code = 2142, message = "Offer Read Failure"),
			@ApiResponse(code = 9996, message = "User is not allowed to perform this action"),
			@ApiResponse(code = 9999, message = "Invalid/ session does not exist") })
	@RequestMapping(value = "/facilityOffer/{sessionId}/{facilityId}", method = RequestMethod.GET)
	public ResponseEntity<List<FacilityOffers>> getOffers(@PathVariable("sessionId") String sessionId,
			@PathVariable("facilityId") int facilityId) {
		HttpHeaders headers = new HttpHeaders();
		List<FacilityOffers> offers = facilityService.getOffers(sessionId, facilityId);
		if (offers.isEmpty())
			headers.set("ResponseCode", ResponseCode.FacilityOfferReadSuccess);
		else if (offers.get(0).getId() == -1)
			headers.set("ResponseCode", ResponseCode.invalidSessionId);
		else if (offers.get(0).getId() == -1)
			headers.set("ResponseCode", ResponseCode.accessRestricted);
		else
			headers.set("ResponseCode", ResponseCode.FacilityOfferReadSuccess);
		return new ResponseEntity<List<FacilityOffers>>(offers, headers, HttpStatus.OK);
	}

	/*
	 * @ApiOperation(value = "Delete Facility Offer", notes =
	 * "To delete an offer for a particular facility by the facility owner")
	 * 
	 * @ApiResponses(value = { @ApiResponse(code = 2151, message =
	 * "Offer Deletion Successful"),
	 * 
	 * @ApiResponse(code = 2152, message = "Offer Deletion Failure"),
	 * 
	 * @ApiResponse(code = 9996, message =
	 * "User is not allowed to perform this action"),
	 * 
	 * @ApiResponse(code = 9999, message = "Invalid/ session does not exist") })
	 * 
	 * @RequestMapping(value = "/facilityOffer/{sessionId}/{facilityId}/{offerId}",
	 * method = RequestMethod.DELETE) public ResponseEntity<Void>
	 * deleteFacilityOffer(@PathVariable("sessionId") String sessionId,
	 * 
	 * @PathVariable("facilityId") int facilityId, @PathVariable("offerId") int
	 * offerId) { HttpHeaders headers = new HttpHeaders(); int result =
	 * facilityService.deleteOffer(facilityId, sessionId, offerId); if (result ==
	 * -1) headers.set("ResponseCode", ResponseCode.invalidSessionId); else if
	 * (result == -2) headers.set("ResponseCode", ResponseCode.accessRestricted);
	 * else if (result == 1) headers.set("ResponseCode",
	 * ResponseCode.OfferDeleteSuccessful); else headers.set("ResponseCode",
	 * ResponseCode.OfferDeleteFailure); return new ResponseEntity<Void>(headers,
	 * HttpStatus.OK); }
	 */

	@ApiOperation(value = "Add Facility Under Maintenance", notes = "To add a facility to maintenance list")
	@ApiResponses(value = { @ApiResponse(code = 2301, message = "Facility Added to Maintenance List Successful"),
			@ApiResponse(code = 2302, message = "Facility Added to Maintenance List Failure"),
			@ApiResponse(code = 9996, message = "User is not allowed to perform this action"),
			@ApiResponse(code = 9999, message = "Invalid/ session does not exist") })
	@RequestMapping(value = "/facilityUnderMaintenance/{sessionId}", method = RequestMethod.POST)
	public ResponseEntity<Void> facilityUnderMaintenance(@RequestBody FacilityUnderMaintenance maintenance,
			@PathVariable("sessionId") String sessionId) {
		HttpHeaders headers = new HttpHeaders();
		int main = facilityService.facilityundermaintenance(maintenance, sessionId);
		if (main == -2)
			headers.set("ResponseCode", ResponseCode.invalidSessionId);
		else if (main == -1)
			headers.set("ResponseCode", ResponseCode.accessRestricted);
		else if (main == 1)
			headers.set("ResponseCode", ResponseCode.MaintenanceAddSuccessful);
		else
			headers.set("ResponseCode", ResponseCode.MaintenanceAddFailure);
		return new ResponseEntity<Void>(headers, HttpStatus.OK);
	}

	@ApiOperation(value = "Read Facility Under Maintenance", notes = "To read the list of facilities under maintenance by users")
	@ApiResponses(value = { @ApiResponse(code = 2311, message = "Facility Maintenance List Read Successful"),
			@ApiResponse(code = 2312, message = "Facility Maintenance List Read Failure"),
			@ApiResponse(code = 9996, message = "User is not allowed to perform this action"),
			@ApiResponse(code = 9999, message = "Invalid/ session does not exist") })
	@RequestMapping(value = "/facilityUnderMaintenance/{sessionId}", method = RequestMethod.GET)
	public ResponseEntity<List<FacilityUnderMaintenance>> facilityUnderMaintenance(
			@PathVariable("sessionId") String sessionId) {
		HttpHeaders headers = new HttpHeaders();
		List<FacilityUnderMaintenance> main = facilityService.facilityundermaintenance(sessionId);
		if (main.isEmpty())
			headers.set("ResponseCode", ResponseCode.MaintenanceReadSuccessful);
		else if (main.get(0).getId() == -1)
			headers.set("ResponseCode", ResponseCode.invalidSessionId);
		else if (main.get(0).getId() == -2)
			headers.set("ResponseCode", ResponseCode.accessRestricted);
		else
			headers.set("ResponseCode", ResponseCode.MaintenanceReadSuccessful);
		return new ResponseEntity<List<FacilityUnderMaintenance>>(main, headers, HttpStatus.OK);
	}

	@ApiOperation(value = "Update Facility Under Maintenance", notes = "To update facility under maintenance details by facility owner")
	@ApiResponses(value = { @ApiResponse(code = 2321, message = "Facility Maintenance Update Successful"),
			@ApiResponse(code = 2322, message = "Facility Maintenance Update Failure"),
			@ApiResponse(code = 9996, message = "User is not allowed to perform this action"),
			@ApiResponse(code = 9999, message = "Invalid/ session does not exist") })
	@RequestMapping(value = "/facilityUnderMaintenance/{sessionId}", method = RequestMethod.PUT)
	public ResponseEntity<Void> updateFacilityUnderMaintenance(@RequestBody FacilityUnderMaintenance maintenance,
			@PathVariable("sessionId") String sessionId) {
		HttpHeaders headers = new HttpHeaders();
		int result = facilityService.updateFacilityUnderMaintenance(maintenance, sessionId);
		if (result == -1)
			headers.set("ResponseCode", ResponseCode.invalidSessionId);
		else if (result == -2)
			headers.set("ResponseCode", ResponseCode.accessRestricted);
		else if (result == 1)
			headers.set("ResponseCode", ResponseCode.MaintenanceUpdateSuccessful);
		else
			headers.set("ResponseCode", ResponseCode.MaintenanceUpdateFailure);
		return new ResponseEntity<Void>(headers, HttpStatus.OK);
	}

	@ApiOperation(value = "Delete Facility Under Maintenance", notes = "To delete facility from under maintenance list by facility owner")
	@ApiResponses(value = { @ApiResponse(code = 2331, message = "Facility Maintenance delete Successful"),
			@ApiResponse(code = 2332, message = "Facility Maintenance delete Failure"),
			@ApiResponse(code = 9996, message = "User is not allowed to perform this action"),
			@ApiResponse(code = 9999, message = "Invalid/ session does not exist") })
	@RequestMapping(value = "/facilityUnderMaintenance/{sessionId}/{id}", method = RequestMethod.DELETE)
	public ResponseEntity<Void> updateFacilityUnderMaintenance(@PathVariable("sessionId") String sessionId,
			@PathVariable("id") int id) {
		HttpHeaders headers = new HttpHeaders();
		int result = facilityService.facilityundermaintenance(sessionId, id);
		if (result == -1)
			headers.set("ResponseCode", ResponseCode.invalidSessionId);
		else if (result == -2)
			headers.set("ResponseCode", ResponseCode.accessRestricted);
		else if (result == 1)
			headers.set("ResponseCode", ResponseCode.MaintenanceDeleteSuccessful);
		else
			headers.set("ResponseCode", ResponseCode.MaintenanceDeleteFailure);
		return new ResponseEntity<Void>(headers, HttpStatus.OK);
	}

	@ApiOperation(value = "Add Additional Cost", notes = " To add miscellaneous/additional cost to a facility by service provider")
	@ApiResponses(value = { @ApiResponse(code = 2361, message = "Additional Cost Add Successful"),
			@ApiResponse(code = 2362, message = "Additional Cost Add Failure"),
			@ApiResponse(code = 9996, message = "User is not allowed to perform this action"),
			@ApiResponse(code = 9999, message = "Invalid/ session does not exist") })
	@RequestMapping(value = "/additionalCharges/{sessionId}", method = RequestMethod.POST)
	public ResponseEntity<Void> additionalCharges(@PathVariable("sessionId") String sessionId,
			@RequestBody FacilityAdditionalCost additionalCost) {
		HttpHeaders headers = new HttpHeaders();
		int result = facilityService.additionalCost(sessionId, additionalCost);
		if (result == -1)
			headers.set("ResponseCode", ResponseCode.invalidSessionId);
		else if (result == -2)
			headers.set("ResponseCode", ResponseCode.accessRestricted);
		else if (result == 1)
			headers.set("ResponseCode", ResponseCode.AdditionalCostAddedSuccessfully);
		else
			headers.set("ResponseCode", ResponseCode.AdditionalCostAddedFailure);
		return new ResponseEntity<Void>(headers, HttpStatus.OK);
	}

	@ApiOperation(value = "Read Additional Cost", notes = " To read miscellaneous/additional cost of a facility by any user")
	@ApiResponses(value = { @ApiResponse(code = 2371, message = "Additional Cost Read Successful"),
			@ApiResponse(code = 2372, message = "Additional Cost Read Failure"),
			@ApiResponse(code = 9996, message = "User is not allowed to perform this action"),
			@ApiResponse(code = 9999, message = "Invalid/ session does not exist") })
	@RequestMapping(value = "/additionalCharges/{sessionId}/{facilityId}", method = RequestMethod.GET)
	public ResponseEntity<List<FacilityAdditionalCost>> additionalCharges(@PathVariable("sessionId") String sessionId,
			@PathVariable("facilityId") int facilityId) {
		HttpHeaders headers = new HttpHeaders();
		List<FacilityAdditionalCost> additionalCost = facilityService.additionalCost(sessionId, facilityId);
		if (additionalCost.isEmpty())
			headers.set("ResponseCode", ResponseCode.AdditionalCostReadSuccessfully);
		else if (additionalCost.get(0).getId() == -1)
			headers.set("ResponseCode", ResponseCode.invalidSessionId);
		else if (additionalCost.get(0).getId() == -2)
			headers.set("ResponseCode", ResponseCode.accessRestricted);
		else
			headers.set("ResponseCode", ResponseCode.AdditionalCostReadSuccessfully);
		return new ResponseEntity<List<FacilityAdditionalCost>>(additionalCost, headers, HttpStatus.OK);
	}

	@ApiOperation(value = "To Add Review", notes = " To write a review about the facility by a consumer who has booked the particular facility")
	@ApiResponses(value = { @ApiResponse(code = 2211, message = "Review Add Successful"),
			@ApiResponse(code = 2212, message = "Review Add Failure"),
			@ApiResponse(code = 9996, message = "User is not allowed to perform this action"),
			@ApiResponse(code = 9999, message = "Invalid/ session does not exist") })
	@RequestMapping(value = "/reviews/{sessionId}", method = RequestMethod.POST)
	public ResponseEntity<Void> review(@RequestBody CustomerReview reviews,
			@PathVariable("sessionId") String sessionId) {
		HttpHeaders headers = new HttpHeaders();
		int review = facilityService.review(reviews, sessionId);
		if (review == -1)
			headers.set("ResponseCode", ResponseCode.invalidSessionId);
		else if (review == -2)
			headers.set("ResponseCode", ResponseCode.accessRestricted);
		else if (review == 0)
			headers.set("ResponseCode", ResponseCode.ReviewAddSuccesssful);
		else
			headers.set("ResponseCode", ResponseCode.ReviewAddFailure);
		return new ResponseEntity<Void>(headers, HttpStatus.OK);
	}

	@ApiOperation(value = "To Read Review", notes = " To read reviews about the facility")
	@ApiResponses(value = { @ApiResponse(code = 2261, message = "Review Read Successful"),
			@ApiResponse(code = 2262, message = "Review Read Failure") })
	@RequestMapping(value = "/reviewsForFacility/", method = RequestMethod.GET)
	public ResponseEntity<List<Review>> review(@RequestParam int facilityId) {
		HttpHeaders headers = new HttpHeaders();
		List<Review> review = facilityService.review(facilityId);
		if (review != null)
			headers.set("ResponseCode", ResponseCode.ReviewReadSuccesssful);
		else
			headers.set("ResponseCode", ResponseCode.ReviewReadFailure);
		return new ResponseEntity<List<Review>>(review, headers, HttpStatus.OK);
	}

	@ApiOperation(value = "To Read Review", notes = " To read reviews based on bookingId by consumer")
	@ApiResponses(value = { @ApiResponse(code = 2261, message = "Review Read Successful"),
			@ApiResponse(code = 2262, message = "Review Read Failure") })
	@RequestMapping(value = "/reviews/", method = RequestMethod.GET)
	public ResponseEntity<List<Review>> reviewbyBookingId(@RequestParam int bookingId) {
		HttpHeaders headers = new HttpHeaders();
		List<Review> review = facilityService.reviewbyBookingId(bookingId);
		if (review != null)
			headers.set("ResponseCode", ResponseCode.ReviewReadSuccesssful);
		else
			headers.set("ResponseCode", ResponseCode.ReviewReadFailure);
		return new ResponseEntity<List<Review>>(review, headers, HttpStatus.OK);
	}

	// update
	@ApiOperation(value = "To Delete Review", notes = " To delete a review about the facility")
	@ApiResponses(value = { @ApiResponse(code = 2451, message = "Review Delete Successful"),
			@ApiResponse(code = 2452, message = "Review Delete Failure"),
			@ApiResponse(code = 9996, message = "User is not allowed to perform this action"),
			@ApiResponse(code = 9999, message = "Invalid/ session does not exist") })
	@RequestMapping(value = "/reviews/{sessionId}/{id}", method = RequestMethod.DELETE)
	public ResponseEntity<Void> delreview(@PathVariable("sessionId") String sessionId, @PathVariable("id") int id) {
		HttpHeaders headers = new HttpHeaders();
		if (facilityService.delReview(sessionId, id))
			headers.set("ResponseCode", ResponseCode.ReviewDeleteSuccessful);
		else
			headers.set("ResponseCode", ResponseCode.ReviewDeleteFailure);
		return new ResponseEntity<Void>(headers, HttpStatus.OK);
	}

	@ApiOperation(value = "To Add Location", notes = " To add a location by service provider")
	@ApiResponses(value = { @ApiResponse(code = 2411, message = "Location Add Successful"),
			@ApiResponse(code = 2412, message = "Location Add Failure"),
			@ApiResponse(code = 9996, message = "User is not allowed to perform this action"),
			@ApiResponse(code = 9999, message = "Invalid/ session does not exist") })
	@RequestMapping(value = "/location/{sessionId}", method = RequestMethod.POST)
	public ResponseEntity<Void> addLocation(@PathVariable("sessionId") String sessionId,
			@RequestBody Location location) {
		HttpHeaders headers = new HttpHeaders();
		int local = facilityService.addLocation(sessionId, location);
		if (local == -1)
			headers.set("ResponseCode", ResponseCode.invalidSessionId);
		else if (local == -2)
			headers.set("ResponseCode", ResponseCode.accessRestricted);
		else if (local == 0)
			headers.set("ResponseCode", ResponseCode.AddLocationSuccessful);
		else
			headers.set("ResponseCode", ResponseCode.AddLocationFailure);
		return new ResponseEntity<Void>(headers, HttpStatus.OK);
	}

	@ApiOperation(value = "To Read Location", notes = " To read location details")
	@ApiResponses(value = { @ApiResponse(code = 2421, message = "Read Location Successful"),
			@ApiResponse(code = 2422, message = "Read Location Failure") })
	@RequestMapping(value = "/getLocation/{id}", method = RequestMethod.GET)
	public ResponseEntity<List<Location>> getLocation(@PathVariable("id") int id) {
		HttpHeaders headers = new HttpHeaders();
		List<Location> loc = facilityService.getLocation(id);
		if (loc != null)
			headers.set("ResponseCode", ResponseCode.ReadLocationSuccessful);
		else
			headers.set("ResponseCode", ResponseCode.ReadLocationFailure);
		return new ResponseEntity<List<Location>>(loc, headers, HttpStatus.OK);

	}

	@ApiOperation(value = "To Update Facility Status By Advisor", notes = " To update status of facility by advisor")
	@ApiResponses(value = { @ApiResponse(code = 2341, message = "Update Facility Status Successful"),
			@ApiResponse(code = 2342, message = "Update Facility Status Failure"),
			@ApiResponse(code = 9996, message = "User is not allowed to perform this action"),
			@ApiResponse(code = 9999, message = "Invalid/ session does not exist") })
	@RequestMapping(value = "/updateFacilityStatusByAdvisor", method = RequestMethod.PUT)
	public ResponseEntity<Void> updateFacilityStatus(@RequestParam String sessionId, @RequestParam int id,
			@RequestParam int status, @RequestParam String comments) throws MessagingException, IOException {
		HttpHeaders headers = new HttpHeaders();
		int update = facilityService.updateFacilityStatus(sessionId, id, status, comments);
		if (update == -1)
			headers.set("ResponseCode", ResponseCode.invalidSessionId);
		else if (update == -2)
			headers.set("ResponseCode", ResponseCode.accessRestricted);
		else if (update == 1)
			headers.set("ResponseCode", ResponseCode.UpdateFacilityStatusSuccessful);
		else if (update == 2)
			headers.set("ResponseCode", ResponseCode.ServiceProviderIsBlocked);
		else
			headers.set("ResponseCode", ResponseCode.UpdateFacilityStatusFailure);
		return new ResponseEntity<Void>(headers, HttpStatus.OK);

	}

	@ApiOperation(value = "To get count of facility status", notes = " To get the count of facility approved,pending & rejected by advisor")
	@ApiResponses(value = { @ApiResponse(code = 2461, message = "Facility Status Count Read Successful"),
			@ApiResponse(code = 2462, message = "Facility Status Count Read Failure"),
			@ApiResponse(code = 9996, message = "User is not allowed to perform this action"),
			@ApiResponse(code = 9999, message = "Invalid/ session does not exist") })
	@RequestMapping(value = "/facilityStatusByAdvsr/{sessionId}", method = RequestMethod.GET)
	public ResponseEntity<List<AdminFacilityDB>> facilityStatus(@PathVariable("sessionId") String sessionId) {
		HttpHeaders headers = new HttpHeaders();
		List<AdminFacilityDB> count = facilityService.statusCount(sessionId);
		if (count.isEmpty())
			headers.set("ResponseCode", ResponseCode.ReadFacilityStatusCountSuccessful);
		else if (count.get(0).getResult() == -1)
			headers.set("ResponseCode", ResponseCode.invalidSessionId);
		else if (count.get(0).getResult() == -2)
			headers.set("ResponseCode", ResponseCode.accessRestricted);
		else
			headers.set("ResponseCode", ResponseCode.ReadFacilityStatusCountSuccessful);
		return new ResponseEntity<List<AdminFacilityDB>>(count, headers, HttpStatus.OK);
	}

	@ApiOperation(value = "To get count of facility status", notes = " To get the count of facility approved,pending & rejected by service provider")
	@ApiResponses(value = { @ApiResponse(code = 2461, message = "ReadFacilityStatusCountSuccessful"),
			@ApiResponse(code = 2462, message = "Update Facility Status Failure"),
			@ApiResponse(code = 9996, message = "User is not allowed to perform this action"),
			@ApiResponse(code = 9999, message = "Invalid/ session does not exist") })
	@RequestMapping(value = "/facilityStatusBySP/{sessionId}", method = RequestMethod.GET)
	public ResponseEntity<List<StatusCount>> facilityStatusBySP(@PathVariable("sessionId") String sessionId) {
		HttpHeaders headers = new HttpHeaders();
		List<StatusCount> count = facilityService.statusCountBySP(sessionId);
		if (count.get(0).getId() == -1)
			headers.set("ResponseCode", ResponseCode.invalidSessionId);
		else if (count.get(0).getId() == -2)
			headers.set("ResponseCode", ResponseCode.accessRestricted);
		else if (count.isEmpty())
			headers.set("ResponseCode", ResponseCode.ReadFacilityStatusCountSuccessful);
		else
			headers.set("ResponseCode", ResponseCode.ReadFacilityStatusCountSuccessful);
		return new ResponseEntity<List<StatusCount>>(count, headers, HttpStatus.OK);
	}

	@ApiOperation(value = "Read Locations by SP", notes = " To read all the locations of a SP")
	@ApiResponses(value = { @ApiResponse(code = 2421, message = "Read Location Successful"),
			@ApiResponse(code = 2422, message = "Review Location Failure"),
			@ApiResponse(code = 9996, message = "User is not allowed to perform this action"),
			@ApiResponse(code = 9999, message = "Invalid/ session does not exist") })
	@RequestMapping(value = "/getLocationBySP/{sessionId}", method = RequestMethod.GET)
	public ResponseEntity<List<LocationDetails>> getLocationBySP(@PathVariable("sessionId") String sessionId) {
		HttpHeaders headers = new HttpHeaders();
		List<LocationDetails> loc = facilityService.getLocation(sessionId);
		if (loc.isEmpty())
			headers.set("ResponseCode", ResponseCode.ReadLocationSuccessful);
		else if (loc.get(0).getId() == -1)
			headers.set("ResponseCode", ResponseCode.invalidSessionId);
		else if (loc.get(0).getId() == -2)
			headers.set("ResponseCode", ResponseCode.accessRestricted);
		else
			headers.set("ResponseCode", ResponseCode.ReadLocationSuccessful);
		return new ResponseEntity<List<LocationDetails>>(loc, headers, HttpStatus.OK);
	}

	@ApiOperation(value = "To view bookings of a facility", notes = "To view all bookings of a facility by SP of that facility")
	@ApiResponses(value = { @ApiResponse(code = 2491, message = "View Booking Successful"),
			@ApiResponse(code = 2492, message = "View Booking Failure"),
			@ApiResponse(code = 9996, message = "User is not allowed to perform this action"),
			@ApiResponse(code = 9999, message = "Invalid/ session does not exist") })
	@RequestMapping(value = "/viewBookings/{sessionId}/{facilityId}", method = RequestMethod.GET)
	public ResponseEntity<List<Booking>> getBookings(@PathVariable("sessionId") String sessionId,
			@PathVariable("facilityId") int facilityId) {
		HttpHeaders headers = new HttpHeaders();
		List<Booking> book = facilityService.getBookings(sessionId, facilityId);
		if (book.isEmpty())
			headers.set("ResponseCode", ResponseCode.BookingReadSuccessful);
		else if (book.get(0).getId() == -1)
			headers.set("ResponseCode", ResponseCode.invalidSessionId);
		else if (book.get(0).getId() == -2)
			headers.set("ResponseCode", ResponseCode.accessRestricted);
		else
			headers.set("ResponseCode", ResponseCode.BookingReadSuccessful);
		return new ResponseEntity<List<Booking>>(book, headers, HttpStatus.OK);
	}

	@ApiOperation(value = "To view all booking for a service provider", notes = "To view all bookings for all facilities of a SP")
	@ApiResponses(value = { @ApiResponse(code = 2491, message = "View Booking Successful"),
			@ApiResponse(code = 2492, message = "View Booking Failure"),
			@ApiResponse(code = 9996, message = "User is not allowed to perform this action"),
			@ApiResponse(code = 9999, message = "Invalid/ session does not exist") })
	@RequestMapping(value = "/viewBookings/{sessionId}", method = RequestMethod.GET)
	public ResponseEntity<List<Booking>> readBookings(@PathVariable("sessionId") String sessionId) {
		HttpHeaders headers = new HttpHeaders();
		List<Booking> book = facilityService.getBookings(sessionId);
		if (book.isEmpty())
			headers.set("ResponseCode", ResponseCode.BookingReadSuccessful);
		else if (book.get(0).getId() == -1)
			headers.set("ResponseCode", ResponseCode.invalidSessionId);
		else if (book.get(0).getId() == -2)
			headers.set("ResponseCode", ResponseCode.accessRestricted);
		else
			headers.set("ResponseCode", ResponseCode.BookingReadSuccessful);
		return new ResponseEntity<List<Booking>>(book, headers, HttpStatus.OK);
	}

	@RequestMapping(value = "/filterBookingView/", method = RequestMethod.GET)
	public ResponseEntity<List<Booking>> getfacility(@PathVariable("sessionId") String sessionId,
			@PathVariable("locationId") Optional<Integer> locationId,
			@PathVariable("localityId") Optional<Integer> localityId, @PathVariable("cityId") Optional<Integer> cityId,
			@PathVariable("fromDate") Optional<Date> fromDate, @PathVariable("toDate") Optional<Date> toDate,
			@PathVariable("id") Optional<Integer> id) {
		HttpHeaders headers = new HttpHeaders();
		List<Booking> booking = facilityService.viewBookings(sessionId, locationId, localityId, cityId, fromDate,
				toDate, id);
		if (booking != null)
			headers.set("ResponseCode", ResponseCode.BookingReadSuccessful);
		else
			headers.set("ResponseCode", ResponseCode.BookingReadFailure);
		return new ResponseEntity<List<Booking>>(booking, headers, HttpStatus.OK);
	}

	@ApiOperation(value = "To view revenue by SP", notes = "To view the by SP based on month & facility type")
	@ApiResponses(value = { @ApiResponse(code = 2611, message = "Paymnet Read Successful"),
			@ApiResponse(code = 2612, message = "Payment Read Failure"),
			@ApiResponse(code = 9996, message = "User is not allowed to perform this action"),
			@ApiResponse(code = 9999, message = "Invalid/ session does not exist") })
	@RequestMapping(value = "/filterRevenue/", method = RequestMethod.GET)
	public ResponseEntity<List<Report>> getfacility(@RequestParam String sessionId, @RequestParam int month,
			@RequestParam int year, @RequestParam int facilityType, @RequestParam int selection) {
		HttpHeaders headers = new HttpHeaders();
		List<Report> booking = facilityService.viewReport(sessionId, month, year, facilityType, selection);
		if (booking.isEmpty())
			headers.set("ResponseCode", ResponseCode.PaymentReadSuccess);
		else if (booking.get(0).getSum() == -1)
			headers.set("ResponseCode", ResponseCode.invalidSessionId);
		else if (booking.get(0).getSum() == -2)
			headers.set("ResponseCode", ResponseCode.accessRestricted);
		else
			headers.set("ResponseCode", ResponseCode.PaymentReadSuccess);
		return new ResponseEntity<List<Report>>(booking, headers, HttpStatus.OK);
	}

	/*
	 * @RequestMapping(value="/Location/{sessionId}/{id}", method =
	 * RequestMethod.PUT) public ResponseEntity<Void> updateLocation(@RequestBody
	 * Location location,
	 * 
	 * @PathVariable("sessionId") String sessionId, @PathVariable("id") int id) {
	 * HttpHeaders headers = new HttpHeaders(); if
	 * (facilityService.updateLocation(location, sessionId, id))
	 * headers.set("ResponseCode", ResponseCode.UpdateLocationSuccessful); else
	 * headers.set("ResponseCode", ResponseCode.UpdateLocationFailure); return new
	 * ResponseEntity<Void>(headers, HttpStatus.OK);
	 * 
	 * }
	 * 
	 * @RequestMapping(value = "/deleteLocation/{id}/{sessionId}", method =
	 * RequestMethod.DELETE) public ResponseEntity<Void>
	 * deleteLocation(@PathVariable("sessionId") String sessionId,
	 * 
	 * @PathVariable("id") int id) { HttpHeaders headers = new HttpHeaders(); if
	 * (facilityService.deleteLocation(id, sessionId)) headers.set("ResponseCode",
	 * ResponseCode.DeleteLocationSuccessful); else headers.set("ResponseCode",
	 * ResponseCode.DeleteLocationFailure); return new ResponseEntity<Void>(headers,
	 * HttpStatus.OK); }
	 */

	@ApiOperation(value = "To view all facilities for a advisor", notes = "To view all facilities by an advisor in state Pending For Approval or Pending For Approval with Verification Request")
	@ApiResponses(value = { @ApiResponse(code = 2121, message = "Facility Read Successful"),
			@ApiResponse(code = 2122, message = "Facility Read Failure"),
			@ApiResponse(code = 9996, message = "User is not allowed to perform this action"),
			@ApiResponse(code = 9999, message = "Invalid session id") })
	@RequestMapping(value = "/facilitiesForAdvisor/", method = RequestMethod.GET)
	public ResponseEntity<List<FacilityFilterResult>> getFacilityByAdvisor(@RequestParam String sessionId,
			@RequestParam int pageNo, @RequestParam int count) {
		HttpHeaders headers = new HttpHeaders();
		FacilityFilterResultPagination facility = facilityService.facilityByAdvisor(sessionId, pageNo, count);
		if (facility == null) {
			headers.set("ResponseCode", ResponseCode.invalidSessionId);
			return new ResponseEntity<List<FacilityFilterResult>>(headers, HttpStatus.OK);
		} else {
			headers.set("totalRecords", facility.getCount());
			headers.set("ResponseCode", ResponseCode.FacilityReadSuccessful);
			return new ResponseEntity<List<FacilityFilterResult>>(facility.getFacilityFilterView(), headers,
					HttpStatus.OK);
		}
	}

	@ApiOperation(value = "To update status of facility", notes = "To update the status of facility by service provider")
	@ApiResponses(value = { @ApiResponse(code = 2341, message = "Update Facility Successfully"),
			@ApiResponse(code = 2342, message = "Update Facility Failure"),
			@ApiResponse(code = 2513, message = "Booking Exist"),
			@ApiResponse(code = 2514, message = "BookingDoesNotExist"),
			@ApiResponse(code = 3100, message = "Cancel booking failed"),
			@ApiResponse(code = 9996, message = "User is not allowed to perform this action"),
			@ApiResponse(code = 9999, message = "Invalid session id") })
	@RequestMapping(value = "/updateStatusBySP/{sessionId}/{comments}/{id}/{status}/{confirm}", method = RequestMethod.PUT)
	public ResponseEntity<Void> addHistory(@PathVariable("comments") String comments,
			@PathVariable("sessionId") String sessionId, @PathVariable("id") int id, @PathVariable("status") int status,
			@PathVariable("confirm") int confirm) throws IOException, AddressException, MessagingException {
		HttpHeaders headers = new HttpHeaders();
		int value = facilityService.addHistory(comments, sessionId, id, status, confirm);
		if (value == -1)
			headers.set("ResponseCode", ResponseCode.invalidSessionId);
		else if (value == -2)
			headers.set("ResponseCode", ResponseCode.accessRestricted);
		else if (value == 1)
			headers.set("ResponseCode", ResponseCode.BookingExist);
		else if (value == 2)
			headers.set("ResponseCode", ResponseCode.BookingDoesNotExist);
		else if (value == 3)
			headers.set("ResponseCode", ResponseCode.UpdateFacilityStatusSuccessful);
		else if (value == 4)
			headers.set("ResponseCode", ResponseCode.CancelBookingFailed);
		else
			headers.set("ResponseCode", ResponseCode.UpdateFacilityStatusFailure);
		return new ResponseEntity<Void>(headers, HttpStatus.OK);
	}

	@ApiOperation(value = "To read payment report by filtering based on city locality type and month", notes = "To read payments")
	@ApiResponses(value = { @ApiResponse(code = 2611, message = "Payment Read Success"),
			@ApiResponse(code = 2612, message = "Payment Read Failure"),
			@ApiResponse(code = 9996, message = "User is not allowed to perform this action"),
			@ApiResponse(code = 9999, message = "Invalid session id") })
	@RequestMapping(value = "/paymentReport/", method = RequestMethod.GET)
	public ResponseEntity<List<AdminPaymentDB>> paymentReport(@RequestParam String sessionId, @RequestParam int month,
			@RequestParam int year) {
		List<AdminPaymentDB> report = facilityService.paymentReport(sessionId, month, year);
		HttpHeaders headers = new HttpHeaders();
		if (report.isEmpty())
			headers.set("ResponseCode", ResponseCode.PaymentReadSuccess);
		else if (report.get(0).getStatus() == -2)
			headers.set("ResponseCode", ResponseCode.invalidSessionId);
		else if (report.get(0).getStatus() == -1)
			headers.set("ResponseCode", ResponseCode.accessRestricted);
		else
			headers.set("ResponseCode", ResponseCode.PaymentReadSuccess);
		return new ResponseEntity<List<AdminPaymentDB>>(report, headers, HttpStatus.OK);
	}

	@ApiOperation(value = "To read all the facility histories", notes = "To read facility status history by advisor")
	@ApiResponses(value = { @ApiResponse(code = 2651, message = "Facility History Read Successful"),
			@ApiResponse(code = 2652, message = "Facility History Read Failure"),
			@ApiResponse(code = 9996, message = "User is not allowed to perform this action"),
			@ApiResponse(code = 9999, message = "Invalid session id") })
	@RequestMapping(value = "/facilityhistory/{sessionId}/{id}", method = RequestMethod.GET)
	public ResponseEntity<List<FacilityHistory>> getFacilityHistory(@PathVariable("sessionId") String sessionId,
			@PathVariable("id") int id) {
		HttpHeaders headers = new HttpHeaders();
		List<FacilityHistory> history = facilityService.getFacilityHistory(sessionId, id);
		if (history.isEmpty())
			headers.set("ResponseCode", ResponseCode.FacilityHistoryReadSuccessful);
		else if (history.get(0).getId() == -1)
			headers.set("ResponseCode", ResponseCode.invalidSessionId);
		else if (history.get(0).getId() == -2)
			headers.set("ResponseCode", ResponseCode.accessRestricted);
		else
			headers.set("ResponseCode", ResponseCode.FacilityHistoryReadSuccessful);
		return new ResponseEntity<List<FacilityHistory>>(history, headers, HttpStatus.OK);
	}

	@ApiOperation(value = "To request for huddil verification by SP", notes = "To make a request to upgrade a facility to huddil verified")
	@ApiResponses(value = { @ApiResponse(code = 2641, message = "Huddil Verify Request Successful"),
			@ApiResponse(code = 2642, message = "HUddil Verify Request Failure"),
			@ApiResponse(code = 9996, message = "User is not allowed to perform this action"),
			@ApiResponse(code = 9999, message = "Invalid session id") })
	@RequestMapping(value = "/spHUddilRequest/", method = RequestMethod.PUT)
	public ResponseEntity<Void> updateFacilityStatus(@RequestParam String sessionId, @RequestParam int facilityId,
			@RequestParam int status) {
		HttpHeaders headers = new HttpHeaders();
		int result = facilityService.verifyHuddilRequest(sessionId, facilityId, status);
		if (result == -1)
			headers.set("ResponseCode", ResponseCode.invalidSessionId);
		else if (result == -2)
			headers.set("ResponseCode", ResponseCode.accessRestricted);
		else if (result == 1)
			headers.set("ResponseCode", ResponseCode.HuddilVerifyRequestSuccessful);
		else
			headers.set("ResponseCode", ResponseCode.HuddilVerifyRequestFailure);
		return new ResponseEntity<Void>(headers, HttpStatus.OK);
	}

	@ApiOperation(value = "To find out all the facilities nearby", notes = "To findout nearby facilities  by consumer")
	@ApiResponses(value = { @ApiResponse(code = 2641, message = "Huddil Verify Request Successful"),
			@ApiResponse(code = 2642, message = "HUddil Verify Request Failure") })
	@RequestMapping(value = "/nearBy/", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<FacilityFilterResult>> nearBy(@RequestParam(required = false) int facilityId,
			@RequestParam double lat, @RequestParam double longt, @RequestParam String type) {
		return new ResponseEntity<List<FacilityFilterResult>>(facilityService.getNearBy(facilityId, lat, longt, type),
				HttpStatus.OK);
	}

	@ApiOperation(value = "To filter facilities by consumer", notes = "To read facilities by applying filters")
	@ApiResponses(value = { @ApiResponse(code = 2121, message = "Facility Read Successful"),
			@ApiResponse(code = 2122, message = "Facility Read Failure"),
			@ApiResponse(code = 9996, message = "User is not allowed to perform this action"),
			@ApiResponse(code = 9999, message = "Invalid session id") })
	@RequestMapping(value = "/filterFacilityByConsumer/", method = RequestMethod.GET)
	public ResponseEntity<List<FacilityFilterResult>> getFacilityByConsumer(
			@RequestParam(required = false) String sessionId, @RequestParam Timestamp fromTime,
			@RequestParam Timestamp toTime, @RequestParam double minCost, @RequestParam double maxCost,
			@RequestParam int maxCapacity, @RequestParam int facilityType, @RequestParam int cityId,
			@RequestParam int localityId, @RequestParam int offers, @RequestParam String amenity,
			@RequestParam int pageNo, @RequestParam int count) {
		System.out.println(new Date());
		HttpHeaders headers = new HttpHeaders();
		FacilityFilterResultPagination facility = facilityService.getFacilityByConsumer(sessionId, fromTime, toTime,
				minCost, maxCost, maxCapacity, facilityType, cityId, localityId, offers, amenity, pageNo, count);
		if (facility != null) {
			headers.set("totalRecords", facility.getCount());
			headers.set("ResponseCode", ResponseCode.FacilityReadSuccessful);
		} else
			headers.set("ResponseCode", ResponseCode.invalidSessionId);
		System.out.println(new Date());
		return new ResponseEntity<List<FacilityFilterResult>>(facility.getFacilityFilterView(), headers, HttpStatus.OK);
	}

	@ApiOperation(value = "To search facility by consumer", notes = "To search for facilities by entering search text by consumer")
	@ApiResponses(value = { @ApiResponse(code = 2121, message = "Facility Read Successful"),
			@ApiResponse(code = 2122, message = "Facility Read Failure") })
	@RequestMapping(value = "/searchFacilityByConsumer/{sessionId}", method = RequestMethod.GET)
	public ResponseEntity<List<FacilityFilterResult>> searchFacilityByConsumer(@RequestParam String search) {
		HttpHeaders headers = new HttpHeaders();
		List<FacilityFilterResult> facility = facilityService.searchFacilityByConsumer(search);
		if (facility != null)
			headers.set("ResponseCode", ResponseCode.FacilityReadSuccessful);
		else
			headers.set("ResponseCode", ResponseCode.FacilityReadFailure);
		return new ResponseEntity<List<FacilityFilterResult>>(facility, headers, HttpStatus.OK);
	}

	@ApiOperation(value = "Add Favorities", notes = "To add a facility to favorities list")
	@ApiResponses(value = { @ApiResponse(code = 2181, message = "Favorities Add Successful"),
			@ApiResponse(code = 2182, message = "Favorities Add Failure"),
			@ApiResponse(code = 2183, message = "Favorities Already Added"),
			@ApiResponse(code = 9996, message = "User is not allowed to perform this action"),
			@ApiResponse(code = 9999, message = "Invalid session id") })
	@RequestMapping(value = "/favorities/{sessionId}", method = RequestMethod.POST)
	public ResponseEntity<Void> favorities(@PathVariable("sessionId") String sessionId, @RequestParam int id) {
		HttpHeaders headers = new HttpHeaders();
		int fav = facilityService.addfavorities(sessionId, id);
		if (fav == -1)
			headers.set("ResponseCode", ResponseCode.invalidSessionId);
		else if (fav == -2)
			headers.set("ResponseCode", ResponseCode.accessRestricted);
		else if (fav == 0)
			headers.set("ResponseCode", ResponseCode.FavoritiesAddSuccessful);
		else if (fav == 2)
			headers.set("ResponseCode", ResponseCode.FavoritiesAlreadyAdded);
		else
			headers.set("ResponseCode", ResponseCode.FavoritiesAddFailure);
		return new ResponseEntity<Void>(headers, HttpStatus.OK);
	}

	@ApiOperation(value = "Read Favorities", notes = "To Read a users favorities list")
	@ApiResponses(value = { @ApiResponse(code = 2191, message = "Favorities Read Successful"),
			@ApiResponse(code = 2192, message = "Favorities Read Failure") })
	@RequestMapping(value = "/favorities/", method = RequestMethod.GET)
	public ResponseEntity<List<FacilityFilterResult>> getfavorities(@RequestParam String sessionId,
			@RequestParam int pageNo, @RequestParam int count) {
		HttpHeaders headers = new HttpHeaders();
		FacilityFilterResultPagination fav = facilityService.getfavorities(sessionId, pageNo, count);
		if (fav != null) {
			headers.set("totalRecords", fav.getCount());
			headers.set("ResponseCode", ResponseCode.FavoritiesReadSuccess);
			return new ResponseEntity<List<FacilityFilterResult>>(fav.getFacilityFilterView(), headers, HttpStatus.OK);
		} else {
			headers.set("ResponseCode", ResponseCode.invalidSessionId);
			return new ResponseEntity<List<FacilityFilterResult>>(headers, HttpStatus.OK);
		}
	}

	@ApiOperation(value = "Delete Favorities", notes = "To remove a facility from favorities list of user")
	@ApiResponses(value = { @ApiResponse(code = 2201, message = "Favorities Delete Successful"),
			@ApiResponse(code = 2202, message = "Favorities Delete Failure"),
			@ApiResponse(code = 9996, message = "User is not allowed to perform this action"),
			@ApiResponse(code = 9999, message = "Invalid session id") })
	@RequestMapping(value = "/favorities/{sessionId}/{id}", method = RequestMethod.DELETE)
	public ResponseEntity<Void> delfavorities(@PathVariable("sessionId") String sessionId, @PathVariable("id") int id) {
		HttpHeaders headers = new HttpHeaders();
		int result = facilityService.favorities(sessionId, id);
		if (result == -1)
			headers.set("ResponseCode", ResponseCode.invalidSessionId);
		else if (result == -2)
			headers.set("ResponseCode", ResponseCode.accessRestricted);
		else if (result == 1)
			headers.set("ResponseCode", ResponseCode.FavoritiesDeleteSuccess);
		else
			headers.set("ResponseCode", ResponseCode.FavoritiesDeleteFailure);
		return new ResponseEntity<Void>(headers, HttpStatus.OK);
	}

	@ApiOperation(value = "Search Facilities By Admin", notes = "To search facility by locality,city & service provider")
	@ApiResponses(value = { @ApiResponse(code = 2121, message = "Facility Read Successful"),
			@ApiResponse(code = 2122, message = "Facility Read Failure"),
			@ApiResponse(code = 9996, message = "User is not allowed to perform this action"),
			@ApiResponse(code = 9999, message = "Invalid session id") })
	@RequestMapping(value = "/searchFacilityByAdmin/", method = RequestMethod.GET)
	public ResponseEntity<List<FacilityFilterResult>> getFacilityByAdmin(@RequestParam String sessionId,
			@RequestParam String search, @RequestParam String searchType, @RequestParam int facilityType,
			@RequestParam int pageNo, @RequestParam int count) {
		HttpHeaders headers = new HttpHeaders();
		FacilityFilterResultPagination facility = facilityService.getFacilityByAdmin(sessionId, search, searchType,
				facilityType, pageNo, count);
		if (facility == null)
			headers.set("ResponseCode", ResponseCode.FacilityReadFailure);
		else if (facility.getId() == -1)
			headers.set("ResponseCode", ResponseCode.invalidSessionId);
		else if (facility.getId() == -2)
			headers.set("ResponseCode", ResponseCode.accessRestricted);
		else {
			headers.set("totalRecords", facility.getCount());
			headers.set("ResponseCode", ResponseCode.FacilityReadSuccessful);
		}
		return new ResponseEntity<List<FacilityFilterResult>>(facility.getFacilityFilterView(), headers, HttpStatus.OK);
	}

	@ApiOperation(value = "Update Facility Price by SP", notes = "To update facility price by Service Provider")
	@ApiResponses(value = { @ApiResponse(code = 2483, message = "Facility Price Update Successful"),
			@ApiResponse(code = 2484, message = "Facility Price Update Failure"),
			@ApiResponse(code = 9996, message = "User is not allowed to perform this action"),
			@ApiResponse(code = 9999, message = "Invalid session id") })
	@RequestMapping(value = "/updateFacilityPrice", method = RequestMethod.PUT)
	public ResponseEntity<Void> updateFacilityPrice(@RequestParam String sessionId, @RequestParam int facilityId,
			@RequestParam double costPerDay, @RequestParam double costPerHour, @RequestParam double costPerMonth) {
		HttpHeaders headers = new HttpHeaders();
		int response = facilityService.updateFacilityPrice(sessionId, facilityId, costPerHour, costPerDay,
				costPerMonth);
		if (response == -1)
			headers.set("ResponseCode", ResponseCode.invalidSessionId);
		else if (response == -2)
			headers.set("ResponseCode", ResponseCode.accessRestricted);
		else if (response == 1)
			headers.set("ResponseCode", ResponseCode.FacilityPriceUpdationSuccessful);
		else
			headers.set("ResponseCode", ResponseCode.FacilityPriceUpdationFailure);
		return new ResponseEntity<Void>(headers, HttpStatus.OK);
	}

	@ApiOperation(value = "Get the saved facilities")
	@ApiResponses(value = { @ApiResponse(code = 2121, message = "Facility read successful"),
			@ApiResponse(code = 9996, message = "User is not allowed to perform this operation"),
			@ApiResponse(code = 9999, message = "Invalid/ Session does not exist") })
	@RequestMapping(value = "/savedFacility/", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<SavedFacility>> savedFacility(@RequestParam String sessionId) {
		HttpHeaders headers = new HttpHeaders();
		List<SavedFacility> facilities = facilityService.getSavedFacility(sessionId);
		if(facilities == null || facilities.isEmpty())
			headers.set("ResponseCode", ResponseCode.FacilityReadSuccessful);
		else if (facilities.get(0).getId() == -1)
			headers.set("ResponseCode", ResponseCode.invalidSessionId);
		else if (facilities.get(0).getId() == -2)
			headers.set("ResponseCode", ResponseCode.accessRestricted);
		else
			headers.set("ResponseCode", ResponseCode.FacilityReadSuccessful);
		return new ResponseEntity<List<SavedFacility>>(facilities, headers, HttpStatus.OK);
	}

}
