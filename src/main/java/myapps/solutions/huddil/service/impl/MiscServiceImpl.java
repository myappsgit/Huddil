package myapps.solutions.huddil.service.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.mail.MessagingException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import myapps.solutions.huddil.dao.IMiscDAO;
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
import myapps.solutions.huddil.service.IMiscService;

@Service
public class MiscServiceImpl implements IMiscService {

	@Autowired
	private IMiscDAO miscDao;

	@Override
	public int facilityType(String sessionId, String facilityType) {
		return miscDao.facilityType(sessionId, facilityType);

	}

	@Override
	public List<FacilityType> facilityType() {
		return miscDao.facilityType();
	}

	@Override
	public int city(City city, String sessionId) {
		return miscDao.city(city, sessionId);
	}

	@Override
	public List<City> city() {
		return miscDao.city();
	}

	@Override
	public boolean updateCity(City city, String sessionId) {
		return miscDao.updateCity(city, sessionId);
	}

	@Override
	public boolean city(String sessionId, int id) {
		return miscDao.city(sessionId, id);
	}

	@Override
	public int amenity(Amenity amenity, String sessionId) {
		return miscDao.amenity(amenity, sessionId);
	}

	@Override
	public List<Amenity> amenity() {
		return miscDao.amenity();
	}

	@Override
	public int amenity(String sessionId, int id) {
		return miscDao.amenity(sessionId, id);
	}

	@Override
	public boolean userType(String sessionId, int userId, int userType) {
		return miscDao.userType(sessionId, userId, userType);
	}

	@Override
	public int addLocality(String sessionId, Locality locality) {
		return miscDao.addLocality(sessionId, locality);
	}

	@Override
	public List<Locality> getLocality(int id) {
		return miscDao.getLocality(id);
	}

	@Override
	public boolean updateLocality(Locality locality, int id, String sessionId) {
		return miscDao.updateLocality(locality, id, sessionId);
	}

	@Override
	public List<String> uploadFile(MultipartFile[] inputFile) throws IllegalStateException, IOException {
		List<String> fileNames = new ArrayList<String>();
		for (int i = 0; i < inputFile.length; i++) {
			Calendar cal = Calendar.getInstance();
			String destinationDir = System.getProperty("user.home") + File.separator + "uploads" + File.separator;
			File uploadFolder = new File(destinationDir + cal.get(Calendar.YEAR) + "-" + cal.get(Calendar.MONTH));
			if (!uploadFolder.exists())
				uploadFolder.mkdirs();
			File destinationFile = new File(uploadFolder + File.separator + System.currentTimeMillis()
					+ inputFile[i].getOriginalFilename().replaceAll("[^\\w.]", ""));
			try {
				inputFile[i].transferTo(destinationFile);
			} catch (IllegalStateException | IOException e) {
				e.printStackTrace();
				return null;
			}
			fileNames.add(destinationFile.getAbsolutePath().substring(destinationDir.length()));
		}
		return fileNames;
	}

	@Override
	public InputStream downloadFile(String path) throws FileNotFoundException {
		String destinationDir = System.getProperty("user.home") + File.separator + "uploads" + File.separator;
		File image = new File(destinationDir + path);
		if (image.exists())
			return new FileInputStream(image);
		return null;
	}

	@Override
	public List<Events> getEvents(String sessionId, int pageNo) {
		return miscDao.getEvents(sessionId, pageNo);
	}

	@Override
	public boolean markAsRead(String sessionId, int id) {
		return miscDao.markAsRead(sessionId, id);
	}

	@Override
	public List<Status> status(String sessionId) {
		return miscDao.status(sessionId);
	}

	@Override
	public List<AdminUserDB> getStatsUser(String sessionId) {
		return miscDao.getStatsUser(sessionId);
	}

	@Override
	public List<AdminFacilityDB> getStatsFacility(String sessionId) {
		return miscDao.getStatsFacility(sessionId);
	}

	@Override
	public AdminPaymentDB getStatsPayment(String sessionId, int month, int year) {
		return miscDao.getStatsPayment(sessionId, month, year);
	}

	@Override
	public List<AdminPaymentDB> getPaymentsForAdmin(String sessionId, int month, int year, String city, String spName, int spId) {
		return miscDao.getPaymentsForAdmin(sessionId, month, year, city, spName, spId);
	}

	@Override
	public int updateSPCommission(String sessionId, int spUserId, int month, int year, double commission) {
		return miscDao.updateSPCommission(sessionId, spUserId, month, year, commission);
	}

	@Override
	public List<CommissionDetails> getCommissionByAdmin(String ids, int month, int year) {
		return miscDao.getCommissionByAdmin(ids, month, year);
	}

	@Override
	public int sendEnquiry(CallMeBack callMeBack, int facilityId) throws MessagingException, IOException {
		return miscDao.sendEnquiry(callMeBack, facilityId);
	}
}
