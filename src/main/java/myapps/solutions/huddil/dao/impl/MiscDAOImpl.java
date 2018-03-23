package myapps.solutions.huddil.dao.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.mail.MessagingException;
import javax.persistence.EntityManager;
import javax.persistence.ParameterMode;
import javax.persistence.PersistenceContext;
import javax.persistence.StoredProcedureQuery;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import myapps.solutions.huddil.dao.IMiscDAO;
import myapps.solutions.huddil.model.AdminFacilityDB;
import myapps.solutions.huddil.model.AdminPaymentDB;
import myapps.solutions.huddil.model.AdminUserDB;
import myapps.solutions.huddil.model.Amenity;
import myapps.solutions.huddil.model.CallMeBack;
import myapps.solutions.huddil.model.City;
import myapps.solutions.huddil.model.CommissionDetails;
import myapps.solutions.huddil.model.Events;
import myapps.solutions.huddil.model.Facility;
import myapps.solutions.huddil.model.FacilityType;
import myapps.solutions.huddil.model.Locality;
import myapps.solutions.huddil.model.Status;
import myapps.solutions.huddil.model.UserPref;
import myapps.solutions.huddil.model.UserSearchResult;
import myapps.solutions.huddil.utils.Notifications;
import myapps.solutions.huddil.utils.UserType;

@Transactional(value = "huddilTranscationManager")
@Repository
public class MiscDAOImpl implements IMiscDAO {

	@PersistenceContext(unitName = "huddil")
	private EntityManager huddilEM;

	@PersistenceContext(unitName = "wrapper")
	private EntityManager wrapperEM;

	@Override
	public int facilityType(String sessionId, String facilityType) {
		UserSearchResult user = getUserPreference(sessionId);
		if (user == null)
			return -1;
		else if (user.getUserType() != UserType.administrator)
			return -2;
		boolean value = huddilEM.createNativeQuery("INSERT IGNORE INTO facility_type(name) values(:facilityType)")
				.setParameter("facilityType", facilityType).executeUpdate() == 0 ? false : true;
		if (value == true)
			return 0;
		return 1;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<FacilityType> facilityType() {
		List<FacilityType> facilityType = huddilEM.createQuery("SELECT t FROM FacilityType t").getResultList();
		return facilityType;
	}

	@Override
	public int city(City city, String sessionId) {
		UserSearchResult user = getUserPreference(sessionId);
		if (user == null)
			return -1;
		else if (user.getUserType() != UserType.administrator)
			return -2;
		boolean value = huddilEM.createNativeQuery("INSERT IGNORE INTO huddil.city (name) VALUES (:name)")
				.setParameter("name", city.getName()).executeUpdate() == 0 ? false : true;
		if (value == true)
			return 0;
		return 1;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<City> city() {
		return huddilEM.createQuery("SELECT c FROM City c ORDER BY name").getResultList();

	}

	@Override
	public boolean updateCity(City city, String sessionId) {
		UserSearchResult user = getUserPreference(sessionId);
		if (user == null || user.getUserType() != UserType.administrator)
			return false;
		huddilEM.merge(city);
		return true;
	}

	@Override
	public boolean city(String sessionId, int id) {
		UserSearchResult user = getUserPreference(sessionId);
		if (user == null || user.getUserType() != UserType.administrator)
			return false;
		return huddilEM.createNativeQuery("DELETE FROM huddil.city WHERE id =:id").setParameter("id", id)
				.executeUpdate() == 0 ? false : true;
	}

	@Override
	public int amenity(Amenity amenity, String sessionId) {
		UserSearchResult pref = getUserPreference(sessionId);
		if (pref == null)
			return -1;
		if (pref.getUserType() != UserType.administrator)
			return -2;
		Object obj = huddilEM.createQuery("SELECT a FROM Amenity a WHERE a.name = :name")
				.setParameter("name", amenity.getName()).getSingleResult();
		if (obj != null)
			return 0;
		huddilEM.persist(amenity);
		if (amenity.getId() == 0)
			return 1;
		return 2;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Amenity> amenity() {
		return huddilEM.createQuery("SELECT a FROM Amenity a").getResultList();
	}

	@Override
	public int amenity(String sessionId, int id) {
		UserSearchResult user = getUserPreference(sessionId);
		boolean value = false;
		if (user == null)
			return -1;
		else if (user.getUserType() != UserType.administrator)
			return -2;
		else
			value = huddilEM.createNativeQuery("DELETE a.* FROM huddil.amenity a WHERE a.id =:id")
					.setParameter("id", id).executeUpdate() == 0 ? false : true;
		if (value == true)
			return 0;
		return 1;
	}

	private UserSearchResult getUserPreference(String sessionId) {
		Object obj = huddilEM.createNativeQuery(
				"SELECT userId AS id, displayName AS name, emailId, mobileNo, mobileNoVerified, CAST(userType AS SIGNED) AS userType FROM user_pref WHERE sessionId = :sessionId",
				"user_pref").setParameter("sessionId", sessionId).getSingleResult();
		if (obj == null)
			return null;
		return (UserSearchResult) obj;
	}

	@Override
	public boolean userType(String sessionId, int userId, int userType) {
		UserSearchResult user = getUserPreference(sessionId);
		if (user == null || user.getUserType() != UserType.administrator)
			return false;
		return huddilEM.createNativeQuery("UPDATE huddil.user_pref p SET p.userType =:userType WHERE p.userId =:userId")
				.setParameter("userType", userType).setParameter("userId", userId).executeUpdate() == 0 ? false : true;
	}

	@Override
	public int addLocality(String sessionId, Locality locality) {
		UserSearchResult user = getUserPreference(sessionId);
		if (user == null)
			return -1;
		else if (user.getUserType() != UserType.administrator)
			return -2;
		boolean value = huddilEM
				.createNativeQuery("INSERT IGNORE INTO huddil.locality(name,cityId)values(:name, :cityId)")
				.setParameter("name", locality.getName()).setParameter("cityId", locality.getCity().getId())
				.executeUpdate() == 0 ? false : true;
		if (value == true)
			return 0;
		return 1;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Locality> getLocality(int id) {
		return huddilEM.createQuery("SELECT l FROM Locality l JOIN FETCH l.city c WHERE c.id =:cityId ORDER BY l.name")
				.setParameter("cityId", id).getResultList();
	}

	@Override
	public boolean updateLocality(Locality locality, int id, String sessionId) {
		UserPref pref = (UserPref) huddilEM.createQuery("SELECT u FROM UserPref u where u.sessionId= :sessionId")
				.setParameter("sessionId", sessionId).getSingleResult();
		if (pref != null)
			return (huddilEM.createNativeQuery("UPDATE huddil.locality SET name= :name WHERE id= :id")
					.setParameter("name", locality.getName()).setParameter("id", id).executeUpdate() == 0) ? false
							: true;
		return false;
	}

	@Override
	public boolean addEvents(String comments, int userId) {
		return huddilEM
				.createNativeQuery(
						"INSERT INTO `huddil`.`events` (`comments`, `forUserId`) VALUES (:comments, :userId)")
				.setParameter("comments", comments).setParameter("userId", userId).executeUpdate() == 1;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Events> getEvents(String sessionId, int pageNo) {
		List<Events> events = new ArrayList<Events>();
		StoredProcedureQuery query = huddilEM.createStoredProcedureQuery("getEvents", "events")
				.registerStoredProcedureParameter("p_sessionId", String.class, ParameterMode.IN)
				.registerStoredProcedureParameter("p_no", Integer.class, ParameterMode.IN)
				.registerStoredProcedureParameter("p_result", Integer.class, ParameterMode.OUT)
				.registerStoredProcedureParameter("p_count", Integer.class, ParameterMode.OUT)
				.setParameter("p_sessionId", sessionId).setParameter("p_no", pageNo);
		query.execute();
		int result = Integer.parseInt(query.getOutputParameterValue("p_result").toString());
		if (result == 1)
			events.add(new Events(-1));
		else if (result == 2)
			events.add(new Events(-2));
		else if (result == 3) {
			events = query.getResultList();
			events.get(0).setCount(Integer.parseInt(query.getOutputParameterValue("p_count").toString()));
		}
		return events;
	}

	@Override
	public boolean markAsRead(String sessionId, int id) {
		return huddilEM.createQuery(
				"UPDATE Events e SET e.read = 1 JOIN FETCH e.userPref u WHERE u.sessionId = :sessionId AND e.id = :id")
				.setParameter("sessionId", sessionId).setParameter("id", id).executeUpdate() == 1;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Status> status(String sessionId) {
		UserSearchResult user = getUserPreference(sessionId);
		List<Status> status = new ArrayList<Status>();
		if (user == null)
			status.add(new Status(-1));
		else if (user.getUserType() != UserType.advisor)
			return (List<Status>) new Status(-2);

		return huddilEM.createNativeQuery("SELECT s.* FROM huddil.status s").getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<AdminUserDB> getStatsUser(String sessionId) {
		UserSearchResult user = getUserPreference(sessionId);
		List<AdminUserDB> adminUserDBs = new ArrayList<AdminUserDB>();
		if (user == null)
			adminUserDBs.add(new AdminUserDB(-2));
		else if (user.getUserType() != UserType.administrator)
			adminUserDBs.add(new AdminUserDB(-1));
		else
			adminUserDBs = wrapperEM.createNativeQuery(
					"SELECT t.type, COUNT(u.id) AS count, 1 AS status FROM user_type t "
							+ "JOIN product p ON p.id = t.productId AND p.name = 'huddil' "
							+ "LEFT JOIN user_subscription s ON s.productUserType = t.id "
							+ "LEFT JOIN user_details u ON u.id = s.userId AND (u.isActive = 0 OR u.isActive = 1) GROUP BY t.type UNION "
							+ "SELECT t.type, COUNT(u.id) AS count, 2 AS status FROM user_type t "
							+ "JOIN product p ON p.id = t.productId AND p.name = 'huddil' "
							+ "LEFT JOIN user_subscription s ON s.productUserType = t.id "
							+ "LEFT JOIN user_details u ON u.id = s.userId AND (u.isActive = 2 OR u.isActive = 3) GROUP BY t.type UNION "
							+ "SELECT t.type, COUNT(u.id) AS count, 3 AS statuss FROM user_type t "
							+ "JOIN product p ON p.id = t.productId AND p.name = 'huddil' "
							+ "LEFT JOIN user_subscription s ON s.productUserType = t.id "
							+ "LEFT JOIN user_details u ON u.id = s.userId AND u.isActive = -1 GROUP BY t.type",
					"adminUserDB").getResultList();
		return adminUserDBs;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<AdminFacilityDB> getStatsFacility(String sessionId) {
		UserSearchResult user = getUserPreference(sessionId);
		List<AdminFacilityDB> adminFacilityDBs = new ArrayList<AdminFacilityDB>();
		if (user == null)
			adminFacilityDBs.add(new AdminFacilityDB(-2));
		else if (user.getUserType() != UserType.administrator)
			adminFacilityDBs.add(new AdminFacilityDB(-1));
		else
			adminFacilityDBs = huddilEM.createNativeQuery("SELECT COUNT(f.id) AS count, 1 AS status FROM status s "
					+ "LEFT JOIN facility f ON f.status = s.id WHERE s.id = 1 OR s.id = 2 UNION "
					+ "SELECT COUNT(f.id) AS count, 2 AS status FROM status s "
					+ "LEFT JOIN facility f ON f.status = s.id WHERE s.id = 3 OR s.id = 4 UNION "
					+ "SELECT COUNT(f.id) AS count, 3 AS status FROM status s "
					+ "LEFT JOIN facility f ON f.status = s.id WHERE s.id = 5 UNION "
					+ "SELECT COUNT(f.id) AS count, 4 AS status FROM status s "
					+ "LEFT JOIN facility f ON f.status = s.id WHERE s.id = 6 UNION "
					+ "SELECT COUNT(f.id) AS count, 5 AS status FROM status s "
					+ "LEFT JOIN facility f ON f.status = s.id WHERE s.id = 7 OR s.id = 8 OR s.id = 5 OR s.id = 6 "
					+ "UNION SELECT COUNT(f.id) AS count, 6 AS status FROM status s "
					+ "LEFT JOIN facility f ON f.status = s.id WHERE s.id = 8 "
					+ "UNION SELECT COUNT(f.id) AS count, 7 AS status FROM status s "
					+ "LEFT JOIN facility f ON f.status = s.id WHERE s.id = 9 OR s.id = 10 UNION "
					+ "SELECT COUNT(f.id) AS count, 8 AS status FROM status s "
					+ "LEFT JOIN facility f ON f.status = s.id WHERE s.id > 10 AND s.id < 15", "adminFacilityDB")
					.getResultList();
		return adminFacilityDBs;
	}

	@Override
	public AdminPaymentDB getStatsPayment(String sessionId, int month, int year) {
		UserSearchResult user = getUserPreference(sessionId);
		AdminPaymentDB adminPaymentDB = null;
		if (user == null)
			adminPaymentDB = new AdminPaymentDB(-2);
		else if (user.getUserType() != UserType.administrator)
			adminPaymentDB = new AdminPaymentDB(-1);
		else {
			StoredProcedureQuery query = huddilEM.createStoredProcedureQuery("paymentAdminDashboard")
					.registerStoredProcedureParameter("p_month", Integer.class, ParameterMode.IN)
					.registerStoredProcedureParameter("p_year", Integer.class, ParameterMode.IN)
					.registerStoredProcedureParameter("p_city", String.class, ParameterMode.IN)
					.registerStoredProcedureParameter("p_spName", String.class, ParameterMode.IN)
					.registerStoredProcedureParameter("p_spId", Integer.class, ParameterMode.IN)
					.registerStoredProcedureParameter("p_online", Double.class, ParameterMode.OUT)
					.registerStoredProcedureParameter("p_onlineCancel", Double.class, ParameterMode.OUT)
					.registerStoredProcedureParameter("p_onlineCancelCharges", Double.class, ParameterMode.OUT)
					.registerStoredProcedureParameter("p_offline", Double.class, ParameterMode.OUT)
					.registerStoredProcedureParameter("p_offlineCancel", Double.class, ParameterMode.OUT)
					.registerStoredProcedureParameter("p_tranCharge", Double.class, ParameterMode.OUT)
					.registerStoredProcedureParameter("p_commission", Double.class, ParameterMode.OUT)
					.registerStoredProcedureParameter("p_settlement", Double.class, ParameterMode.OUT)
					.setParameter("p_month", month).setParameter("p_year", year).setParameter("p_city", "")
					.setParameter("p_spName", "").setParameter("p_spId", -1);
			query.execute();
			adminPaymentDB = new AdminPaymentDB(
					Double.parseDouble(query.getOutputParameterValue("p_online").toString()),
					Double.parseDouble(query.getOutputParameterValue("p_onlineCancel").toString()),
					Double.parseDouble(query.getOutputParameterValue("p_onlineCancelCharges").toString()),
					Double.parseDouble(query.getOutputParameterValue("p_offline").toString()),
					Double.parseDouble(query.getOutputParameterValue("p_offlineCancel").toString()),
					Double.parseDouble(query.getOutputParameterValue("p_tranCharge").toString()),
					Double.parseDouble(query.getOutputParameterValue("p_commission").toString()),
					Double.parseDouble(query.getOutputParameterValue("p_settlement").toString()));
		}
		return adminPaymentDB;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<AdminPaymentDB> getPaymentsForAdmin(String sessionId, int month, int year, String city, String spName,
			int spId) {
		UserSearchResult user = getUserPreference(sessionId);
		List<AdminPaymentDB> adminPaymentDBs = new ArrayList<AdminPaymentDB>();
		if (user == null)
			adminPaymentDBs.add(new AdminPaymentDB(-2));
		else if (user.getUserType() != UserType.administrator)
			adminPaymentDBs.add(new AdminPaymentDB(-1));
		else {
			StoredProcedureQuery query = huddilEM.createStoredProcedureQuery("paymentAdminDashboard", "adminPaymentDB")
					.registerStoredProcedureParameter("p_month", Integer.class, ParameterMode.IN)
					.registerStoredProcedureParameter("p_year", Integer.class, ParameterMode.IN)
					.registerStoredProcedureParameter("p_city", String.class, ParameterMode.IN)
					.registerStoredProcedureParameter("p_spName", String.class, ParameterMode.IN)
					.registerStoredProcedureParameter("p_spId", Integer.class, ParameterMode.IN)
					.registerStoredProcedureParameter("p_online", Double.class, ParameterMode.OUT)
					.registerStoredProcedureParameter("p_onlineCancel", Double.class, ParameterMode.OUT)
					.registerStoredProcedureParameter("p_onlineCancelCharges", Double.class, ParameterMode.OUT)
					.registerStoredProcedureParameter("p_offline", Double.class, ParameterMode.OUT)
					.registerStoredProcedureParameter("p_offlineCancel", Double.class, ParameterMode.OUT)
					.registerStoredProcedureParameter("p_tranCharge", Double.class, ParameterMode.OUT)
					.registerStoredProcedureParameter("p_commission", Double.class, ParameterMode.OUT)
					.registerStoredProcedureParameter("p_settlement", Double.class, ParameterMode.OUT)
					.setParameter("p_month", month).setParameter("p_year", year).setParameter("p_city", city)
					.setParameter("p_spName", spName).setParameter("p_spId", spId);
			query.execute();
			adminPaymentDBs = query.getResultList();
			adminPaymentDBs.add(0,
					new AdminPaymentDB(Double.parseDouble(query.getOutputParameterValue("p_online").toString()),
							Double.parseDouble(query.getOutputParameterValue("p_onlineCancel").toString()),
							Double.parseDouble(query.getOutputParameterValue("p_onlineCancelCharges").toString()),
							Double.parseDouble(query.getOutputParameterValue("p_offline").toString()),
							Double.parseDouble(query.getOutputParameterValue("p_offlineCancel").toString()),
							Double.parseDouble(query.getOutputParameterValue("p_tranCharge").toString()),
							Double.parseDouble(query.getOutputParameterValue("p_commission").toString()),
							Double.parseDouble(query.getOutputParameterValue("p_settlement").toString())));
		}
		return adminPaymentDBs;
	}

	@Override
	public int updateSPCommission(String sessionId, int spUserId, int month, int year, double commission) {
		StoredProcedureQuery spQuery = huddilEM.createStoredProcedureQuery("updateSPCommission")
				.registerStoredProcedureParameter("p_sessionId", String.class, ParameterMode.IN)
				.registerStoredProcedureParameter("p_spUserId", Integer.class, ParameterMode.IN)
				.registerStoredProcedureParameter("p_month", Integer.class, ParameterMode.IN)
				.registerStoredProcedureParameter("p_year", Integer.class, ParameterMode.IN)
				.registerStoredProcedureParameter("p_commission", Double.class, ParameterMode.IN)
				.registerStoredProcedureParameter("p_result", Integer.class, ParameterMode.OUT)
				.setParameter("p_sessionId", sessionId).setParameter("p_spUserId", spUserId)
				.setParameter("p_month", month).setParameter("p_year", year).setParameter("p_commission", commission);
		spQuery.execute();
		return (int) spQuery.getOutputParameterValue("p_result");
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<CommissionDetails> getCommissionByAdmin(String ids, int month, int year) {
		StoredProcedureQuery spQuery = huddilEM.createStoredProcedureQuery("getCommissionByAdmin", "commissionDetails")
				.registerStoredProcedureParameter("p_Ids", String.class, ParameterMode.IN)
				.registerStoredProcedureParameter("p_month", Integer.class, ParameterMode.IN)
				.registerStoredProcedureParameter("p_year", Integer.class, ParameterMode.IN).setParameter("p_Ids", ids)
				.setParameter("p_month", month).setParameter("p_year", year);
		spQuery.execute();
		return spQuery.getResultList();
	}

	@Override
	public int sendEnquiry(CallMeBack callMeBack, int facilityId) throws MessagingException, IOException {
		Facility facility = null;
		Object obj = huddilEM.createNativeQuery(
				"SELECT f.title, f.localityName, f.cityName, l.name, f.emailId, f.alternateEmailId, u.emailId AS spEmail, u.displayName FROM facility f "
				+ "JOIN location l ON f.locationId = l.id JOIN user_pref u ON u.userId = f.spUserId WHERE f.id = :id",
				"sendEnquiry").setParameter("id", facilityId).getSingleResult();
		if(obj == null)
			return 0;
		facility = (Facility) obj;
		Notifications.sendEnquiry(callMeBack, facility);
		return 1;
	}

}
