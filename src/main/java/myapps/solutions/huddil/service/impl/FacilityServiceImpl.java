package myapps.solutions.huddil.service.impl;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import javax.mail.MessagingException;
import javax.mail.internet.AddressException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import myapps.solutions.huddil.dao.IFacilityDAO;
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

@Service
public class FacilityServiceImpl implements IFacilityService {

	@Autowired
	private IFacilityDAO facilityDao;

	@Override
	public int addFacility(Facility facility, String sessionId) throws IOException {
		return facilityDao.addFacility(facility, sessionId);
	}

	@Override
	public int updateFacility(Facility facility, String sessionId) {
		return facilityDao.updateFacility(facility, sessionId);
	}
	
	@Override
	public int deleteFacility(String sessionId, int facilityId) {
		return facilityDao.deleteFacility(sessionId, facilityId);
	}

	@Override
	public List<Facility> getFacility(String sessionId, String type) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Facility getFacility(String sessionId, int id) {
		return facilityDao.getFacility(sessionId, id);
	}

	@Override
	public FacilityFilterResultPagination getFacility(String sessionId, int pageNo, int count) {
		return facilityDao.getFacility(sessionId, pageNo, count);
	}

	@Override
	public void deleteFacility(Facility facility, String sessionId) {
		// TODO Auto-generated method stub

	}

	@Override
	public int updateFacilityStatus(String sessionId, int id, int status, String comments)
			throws MessagingException, IOException {
		return facilityDao.updateFacilityStatus(sessionId, id, status, comments);
	}

	@Override
	public int addMultitenant(MultiTenant multitenant, String sessionId) {
		return facilityDao.addMultitenant(multitenant, sessionId);
	}

	@Override
	public List<MultiTenant> getMultitenant(String sessionId) {
		return facilityDao.getMultitenant(sessionId);
	}

	@Override
	public int updateMultitenant(MultiTenant multitenant, String sessionId) {
		return facilityDao.updateMultitenant(multitenant, sessionId);
	}

	@Override
	public boolean deleteMultitenant(int id, String sessionId) {
		return facilityDao.deleteMultitenant(id, sessionId);
	}

	@Override
	public int addOffer(FacilityOffers facilityOffers, String sessionId) {
		return facilityDao.addOffer(facilityOffers, sessionId);
	}

	@Override
	public List<FacilityOffers> getOffers(String sessionId, int facilityId) {
		return facilityDao.getOffers(sessionId, facilityId);
	}

	/*@Override
	public int deleteOffer(int facilityId, String sessionId, int offerId) {
		return facilityDao.deleteOffer(facilityId, sessionId, offerId);
	}*/

	@Override
	public List<Facility> getFacilities(String sessionId, int locationId) {
		return facilityDao.getFacilities(sessionId, locationId);
	}

	@Override
	public int facilityundermaintenance(FacilityUnderMaintenance maintenance, String sessionId) {
		return facilityDao.facilityundermaintenance(maintenance, sessionId);
	}

	@Override
	public List<FacilityUnderMaintenance> facilityundermaintenance(String sessionId) {
		return facilityDao.facilityundermaintenance(sessionId);
	}

	@Override
	public int updateFacilityUnderMaintenance(FacilityUnderMaintenance maintenance, String sessionId) {
		return facilityDao.updateFacilityUnderMaintenance(maintenance, sessionId);
	}

	@Override
	public int facilityundermaintenance(String sessionId, int id) {
		return facilityDao.facilityundermaintenance(sessionId, id);
	}

	@Override
	public int additionalCost(String sessionId, FacilityAdditionalCost additionalCost) {
		return facilityDao.additionalCost(sessionId, additionalCost);
	}

	@Override
	public List<FacilityAdditionalCost> additionalCost(String sessionId, int facilityId) {
		return facilityDao.additionalCost(sessionId, facilityId);
	}

	@Override
	public int review(CustomerReview reviews, String sessionId) {
		return facilityDao.review(reviews, sessionId);
	}

	@Override
	public List<Review> review(int facilityId) {
		return facilityDao.review(facilityId);
	}

	@Override
	public List<Review> reviewbyBookingId(int bookingId) {
		return facilityDao.reviewbyBookingId(bookingId);
	}

	@Override
	public boolean delReview(String sessionId, int id) {
		return facilityDao.delReview(sessionId, id);
	}

	@Override
	public int addLocation(String sessionId, Location location) {
		return facilityDao.addLocation(sessionId, location);
	}

	@Override
	public List<Location> getLocation(int id) {
		return facilityDao.getLocation(id);
	}

	@Override
	public boolean updateLocation(Location location, String sessionId, int id) {
		return facilityDao.updateLocation(location, sessionId, id);
	}

	@Override
	public boolean deleteLocation(int id, String sessionId) {
		return facilityDao.deleteLocation(id, sessionId);
	}

	/*
	 * @Override public List<Facility> getFacilities(String sessionId, int
	 * locationId) { return facilityDao.getFacilities(sessionId, locationId); }
	 */

	@Override
	public List<AdminFacilityDB> statusCount(String sessionId) {
		return facilityDao.statusCount(sessionId);
	}

	@Override
	public List<StatusCount> statusCountBySP(String sessionId) {
		return facilityDao.statusCountBySP(sessionId);
	}

	@Override
	public List<LocationDetails> getLocation(String sessionId) {
		return facilityDao.getLocation(sessionId);
	}

	@Override
	public List<Booking> getBookings(String sessionId, int facilityId) {
		return facilityDao.getBookings(sessionId, facilityId);
	}

	@Override
	public List<Booking> getBookings(String sessionId) {
		return facilityDao.getBookings(sessionId);
	}

	@Override
	public List<Booking> viewBookings(String sessionId, Optional<Integer> locationId, Optional<Integer> localityId,
			Optional<Integer> cityId, Optional<Date> fromDate, Optional<Date> toDate, Optional<Integer> id) {
		return facilityDao.viewBookings(sessionId, locationId, localityId, cityId, fromDate, toDate, id);
	}

	@Override
	public FacilityFilterResultPagination getFacility(String sessionId, int cityId, int localityId, int locationId,
			int typeId, String search, int status, int pageNo, int count) {
		return facilityDao.getFacility(sessionId, cityId, localityId, locationId, typeId, search, status, pageNo,
				count);
	}

	@Override
	public List<Report> viewReport(String sessionId, int month, int year, int facilityType, int selection) {
		return facilityDao.viewReport(sessionId, month, year, facilityType, selection);
	}

	@Override
	public FacilityFilterResultPagination facilityByAdvisor(String sessionId, int pageNo, int count) {
		return facilityDao.facilityByAdvisor(sessionId, pageNo, count);
	}

	@Override
	public int addHistory(String comments, String sessionId, int id, int status, int confirm)
			throws IOException, AddressException, MessagingException {
		return facilityDao.addHistory(comments, sessionId, id, status, confirm);
	}

	@Override
	public List<AdminPaymentDB> paymentReport(String sessionId, int month, int year) {
		return facilityDao.paymentReport(sessionId, month, year);
	}

	@Override
	public List<FacilityHistory> getFacilityHistory(String sessionId, int id) {
		return facilityDao.getFacilityHistory(sessionId, id);
	}

	@Override
	public int verifyHuddilRequest(String sessionId, int facilityId, int status) {
		return facilityDao.verifyHuddilRequest(sessionId, facilityId, status);
	}

	@Override
	public List<FacilityFilterResult> getNearBy(int facilityId, double lat, double longt, String type) {
		return facilityDao.getNearBy(facilityId, lat, longt, type);
	}

	@Override
	public int addfavorities(String sessionId, int id) {
		return facilityDao.addfavorities(sessionId, id);
	}

	@Override
	public FacilityFilterResultPagination getfavorities(String sessionId, int pageNo, int count) {
		return facilityDao.getfavorities(sessionId, pageNo, count);
	}

	@Override
	public int favorities(String sessionId, int id) {
		return facilityDao.favorities(sessionId, id);
	}

	@Override
	public FacilityFilterResultPagination getFacilityByConsumer(String sessionId, Timestamp fromTime, Timestamp toTime,
			double minCost, double maxCost, int maxCapacity, int facilityType, int cityId, int localityId, int offers,
			String amenity, int pageNo, int count) {
		return facilityDao.getFacilityByConsumer(sessionId, fromTime, toTime, minCost, maxCost, maxCapacity,
				facilityType, cityId, localityId, offers, amenity, pageNo, count);
	}

	@Override
	public List<FacilityFilterResult> searchFacilityByConsumer(String search) {
		return facilityDao.searchFacilityByConsumer(search);
	}

	@Override
	public FacilityFilterResultPagination getFacilityByAdmin(String sessionId, String search, String searchType,
			int facilityType, int pageNo, int count) {
		return facilityDao.getFacilityByAdmin(sessionId, search, searchType, facilityType, pageNo, count);
	}

	@Override
	public int updateFacilityPrice(String sessionId, int facilityId, double costPerHour, double costPerDay,
			double costPerMonth) {
		return facilityDao.updateFacilityPrice(sessionId, facilityId, costPerHour, costPerDay, costPerMonth);
	}

	@Override
	public List<SavedFacility> getSavedFacility(String sessionId) {
		return facilityDao.getSavedFacility(sessionId);
	}

}
