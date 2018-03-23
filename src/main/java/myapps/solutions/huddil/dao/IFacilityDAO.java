package myapps.solutions.huddil.dao;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import javax.mail.MessagingException;
import javax.mail.internet.AddressException;

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

public interface IFacilityDAO {
	
	int addFacility(Facility facility, String sessionId) throws IOException;
	int updateFacility(Facility facility, String sessionId);
	List<Facility> getFacility(String sessionId, String type);
	FacilityFilterResultPagination getFacility(String sessionId, int cityId, int localityId, int locationId, int typeId, String search, int status, int pageNo, int count);
	FacilityFilterResultPagination getFacility(String sessionId, int pageNo, int count);
	Facility getFacility(String sessionId, int id);
	void deleteFacility(Facility facility, String sessionId);
	List<Facility>getFacilities(String sessionId, int locationId);
	int additionalCost(String sessionId,FacilityAdditionalCost additionalCost);
	List<FacilityAdditionalCost> additionalCost(String sessionId,int facilityId);
	int updateFacilityStatus(String sessionId, int id, int status, String comments) throws MessagingException, IOException;
	int verifyHuddilRequest(String sessionId, int facilityId, int status);
	List<AdminFacilityDB> statusCount(String sessionId);
	List<StatusCount> statusCountBySP(String sessionId);
	List<FacilityFilterResult> getNearBy(int facilityId, double lat, double longt, String type);
	FacilityFilterResultPagination getFacilityByConsumer(String sessionId, Timestamp fromTime, Timestamp toTime, double minCost, double maxCost, int maxCapacity, int facilityType, int cityId, int localityId, int offers, String amenity, int pageNo, int count);
	List<FacilityFilterResult> searchFacilityByConsumer(String search);
	List<SavedFacility> getSavedFacility(String sessionId);
	int deleteFacility(String sessionId, int facilityId);


	List<Booking> getBookings(String sessionId, int facilityId);
    List<Booking> getBookings(String sessionId);
    List<Booking> viewBookings(String sessionId, Optional<Integer> locationId, Optional<Integer> localityId, Optional<Integer> cityId, Optional<Date> fromDate,
			Optional<Date> toDate, Optional<Integer> id);
    List<Report> viewReport(String sessionId, int month, int year, int facilityType, int selection);
	
	int addOffer(FacilityOffers facilityOffers, String sessionId);
	List<FacilityOffers>getOffers(String sessionId, int facilityId);
	int deleteOffer();
	
	int addMultitenant(MultiTenant multitenant, String sessionId);
	List<MultiTenant> getMultitenant(String sessionId);
	int updateMultitenant(MultiTenant multitenant, String sessionId);
	boolean deleteMultitenant(int id, String sessionId);
	
	int facilityundermaintenance(FacilityUnderMaintenance maintenance, String sessionId);
	List<FacilityUnderMaintenance> facilityundermaintenance(String sessionId);
	int updateFacilityUnderMaintenance(FacilityUnderMaintenance maintenance, String sessionId);
	int facilityundermaintenance(String sessionId, int id);
	
	int review(CustomerReview reviews, String sessionId);
	List<Review> review(int facilityId);
	List<Review>reviewbyBookingId(int bookingId);
	boolean delReview(String sessionId, int id);
	
	int addLocation(String sessionId, Location location);
    List<Location> getLocation(int id);
    List<LocationDetails> getLocation(String sessionId);
    boolean updateLocation(Location location, String sessionId, int id);
    boolean deleteLocation(int id, String sessionId);
   // List<Facility> getFacilities(String sessionId, int locationId);
    
    FacilityFilterResultPagination facilityByAdvisor(String sessionId, int pageNo, int count);
    int addHistory(String comments, String sessionId, int id, int status, int confirm) throws IOException, AddressException, MessagingException;
    List<FacilityHistory> getFacilityHistory(String sessionId, int id);
    
	List<AdminPaymentDB> paymentReport(String sessionId, int month, int year);
	
	int disableAllFacilityOfSP(String sessionId, int userId, boolean status, String comments);

	int addfavorities(String sessionId, int id);
	FacilityFilterResultPagination getfavorities(String sessionId, int pageNo, int count);
	int favorities(String sessionId, int id);
	FacilityFilterResultPagination getFacilityByAdmin(String sessionId, String search, String searchType, int facilityType, int pageNo, int count);
	int updateFacilityPrice(String sessionId, int facilityId, double costPerHour, double costPerDay, double costPerMonth);

	
}
