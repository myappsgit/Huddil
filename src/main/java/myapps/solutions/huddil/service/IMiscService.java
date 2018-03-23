package myapps.solutions.huddil.service;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import javax.mail.MessagingException;

import org.springframework.web.multipart.MultipartFile;

import myapps.solutions.huddil.model.AdminFacilityDB;
import myapps.solutions.huddil.model.AdminPaymentDB;
import myapps.solutions.huddil.model.AdminUserDB;
import myapps.solutions.huddil.model.Amenity;
import myapps.solutions.huddil.model.CallMeBack;
import myapps.solutions.huddil.model.City;
import myapps.solutions.huddil.model.CommissionDetails;
import myapps.solutions.huddil.model.Events;
import myapps.solutions.huddil.model.FacilityType;
import myapps.solutions.huddil.model.Locality;
import myapps.solutions.huddil.model.Status;

public interface IMiscService {
	
	int facilityType(String sessionId, String facilityType);
	List<FacilityType> facilityType();
	
	//City
	int city(City city, String sessionId);
	List<City> city();
	boolean updateCity(City city, String sessionId);
	boolean city(String sessionId, int id);
	
	// Amenity
	int amenity(Amenity amenity, String sessionId);
	List<Amenity> amenity();
	int amenity(String sessionId, int id);
		
	//UserType
	boolean userType(String sessionId, int userId, int userType);
	
	
	int addLocality(String sessionId, Locality locality);
    List<Locality>getLocality(int id);    
    boolean updateLocality(Locality locality, int id, String sessionId);
 
    List<String> uploadFile(MultipartFile[] inputFile) throws IllegalStateException, IOException;
    InputStream downloadFile(String path) throws FileNotFoundException;

    // Events
    List<Events> getEvents(String sessionId, int pageNo);
    boolean markAsRead(String sessionId, int id);
    
    List<Status>status(String sessionId);
    
    //Admin DB
	List<AdminUserDB> getStatsUser(String sessionId);
	List<AdminFacilityDB> getStatsFacility(String sessionId);
	AdminPaymentDB getStatsPayment(String sessionId, int month, int year);
	List<AdminPaymentDB> getPaymentsForAdmin(String sessionId, int month, int year, String city, String spName,	int spId);
	int updateSPCommission(String sessionId, int spUserId, int month, int year, double commission);
	List<CommissionDetails> getCommissionByAdmin(String ids, int month, int year);
	
	//Send Enquiry
	int sendEnquiry(CallMeBack callMeBack, int facilityId) throws MessagingException, IOException;
}
