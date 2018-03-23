package myapps.solutions.huddil.dao.impl;

import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import javax.mail.MessagingException;
import javax.mail.internet.AddressException;
import javax.persistence.EntityManager;
import javax.persistence.ParameterMode;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.StoredProcedureQuery;

import org.apache.http.util.TextUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import myapps.payment.service.InstaMojoService;
import myapps.payment.service.model.InstaMojoRefund;
import myapps.solutions.huddil.dao.IFacilityDAO;
import myapps.solutions.huddil.dao.IMiscDAO;
import myapps.solutions.huddil.model.AdminFacilityDB;
import myapps.solutions.huddil.model.AdminPaymentDB;
import myapps.solutions.huddil.model.Booking;
import myapps.solutions.huddil.model.CancellationNotificationTemplate;
import myapps.solutions.huddil.model.CustomerReview;
import myapps.solutions.huddil.model.Facility;
import myapps.solutions.huddil.model.FacilityAdditionalCost;
import myapps.solutions.huddil.model.FacilityAmenity;
import myapps.solutions.huddil.model.FacilityCancellationCharges;
import myapps.solutions.huddil.model.FacilityFilterResult;
import myapps.solutions.huddil.model.FacilityFilterResultPagination;
import myapps.solutions.huddil.model.FacilityHistory;
import myapps.solutions.huddil.model.FacilityOffers;
import myapps.solutions.huddil.model.FacilityPhoto;
import myapps.solutions.huddil.model.FacilityTermsConditions;
import myapps.solutions.huddil.model.FacilityTiming;
import myapps.solutions.huddil.model.FacilityUnderMaintenance;
import myapps.solutions.huddil.model.Location;
import myapps.solutions.huddil.model.LocationDetails;
import myapps.solutions.huddil.model.MultiTenant;
import myapps.solutions.huddil.model.Report;
import myapps.solutions.huddil.model.Review;
import myapps.solutions.huddil.model.ReviewRating;
import myapps.solutions.huddil.model.SavedFacility;
import myapps.solutions.huddil.model.StatusCount;
import myapps.solutions.huddil.model.UserPref;
import myapps.solutions.huddil.model.UserSearchResult;
import myapps.solutions.huddil.utils.ImageScaler;
import myapps.solutions.huddil.utils.Notifications;
import myapps.solutions.huddil.utils.UserType;

@Transactional(value = "huddilTranscationManager")
@Repository
public class FacilityDAOImpl implements IFacilityDAO {

	@PersistenceContext(unitName = "huddil")
	private EntityManager huddilEM;

	@PersistenceContext(unitName = "wrapper")
	private EntityManager wrapperEntityManager;

	@Autowired
	IMiscDAO miscDao;

	@Override
	public int addFacility(Facility facility, String sessionId) throws IOException {
		UserSearchResult user = getUserPreference(sessionId);
		if (user == null)
			return -1;
		else if (user.getUserType() != UserType.serviceprovider)
			return -2;
		Location location = huddilEM.find(Location.class, facility.getLocation().getId());
		if (location == null)
			return -3;
		StoredProcedureQuery spQuery = huddilEM.createStoredProcedureQuery("cancellationPolicyId")
				.registerStoredProcedureParameter("v_duration1", Integer.class, ParameterMode.IN)
				.registerStoredProcedureParameter("v_percentage1", Double.class, ParameterMode.IN)
				.registerStoredProcedureParameter("v_duration2", Integer.class, ParameterMode.IN)
				.registerStoredProcedureParameter("v_percentage2", Double.class, ParameterMode.IN)
				.registerStoredProcedureParameter("v_duration3", Integer.class, ParameterMode.IN)
				.registerStoredProcedureParameter("v_percentage3", Double.class, ParameterMode.IN)
				.registerStoredProcedureParameter("v_cancellationPolicyId", Integer.class, ParameterMode.OUT)
				.setParameter("v_duration1", facility.getFacilityCancellationCharges().getDuration1())
				.setParameter("v_percentage1", facility.getFacilityCancellationCharges().getPercentage1())
				.setParameter("v_duration2", facility.getFacilityCancellationCharges().getDuration2())
				.setParameter("v_percentage2", facility.getFacilityCancellationCharges().getPercentage2())
				.setParameter("v_duration3", facility.getFacilityCancellationCharges().getDuration3())
				.setParameter("v_percentage3", facility.getFacilityCancellationCharges().getPercentage3());
		spQuery.execute();
		int policyId = Integer.parseInt(spQuery.getOutputParameterValue("v_cancellationPolicyId").toString());
		int huddleVerified = facility.getHuddleVerified();
		if (facility.isSave())
			facility.setStatus((huddleVerified + 1) * -1);
		else
			facility.setStatus(huddleVerified + 1);
		facility.setFacilityCancellationCharges(huddilEM.find(FacilityCancellationCharges.class, policyId));
		facility.setCity(location.getCity().getName());
		facility.setLocality(location.getLocality().getName());
		facility.setUserPrefBySpUserId(user.getId());

		if (facility.getFacilityAmenities() != null) {
			for (FacilityAmenity amenity : facility.getFacilityAmenities())
				amenity.setFacility(facility);
		}

		if (facility.getFacilityOfferses() != null) {
			for (FacilityOffers offers : facility.getFacilityOfferses())
				offers.setFacility(facility);
		}

		if (facility.getFacilityPhotos() != null && !facility.getFacilityPhotos().isEmpty()) {
			String path = null;
			for (FacilityPhoto photo : facility.getFacilityPhotos()) {
				if (path == null)
					path = photo.getImgPath();
				photo.setFacility(facility);
			}
			String destinationDir = System.getProperty("user.home") + File.separator + "uploads" + File.separator;
			File image = new File(destinationDir + path);
			facility.setThumbnail(ImageScaler.encodeFileToBase64Binary(image, 256, 144));
		} else {
			facility.setThumbnail("");
		}

		if (facility.getFacilityTimings() != null) {
			for (FacilityTiming time : facility.getFacilityTimings())
				time.setFacility(facility);
		}

		huddilEM.persist(facility);
		miscDao.addEvents("New facility " + facility.getTitle() + " is added", user.getId());
		return facility.getId() == null ? 0 : facility.getId();
	}

	@Override
	public int updateFacility(Facility latest, String sessionId) {
		UserSearchResult user = getUserPreference(sessionId);
		if (user == null)
			return -1;
		else if (user.getUserType() != UserType.serviceprovider)
			return -2;
		Object obj = huddilEM
				.createQuery("SELECT f FROM Facility f WHERE f.userPrefBySpUserId = :userId AND f.id =:facilityId")
				.setParameter("userId", user.getId()).setParameter("facilityId", latest.getId()).getSingleResult();
		if (obj == null)
			return -3;
		Facility old = (Facility) obj;
		if (latest.isSave() && old.getStatus() > 0)
			return -5;
		String value;
		int val;
		value = latest.getTitle();
		if (!TextUtils.isEmpty(value))
			old.setTitle(value);
		value = latest.getDescription();
		if (!TextUtils.isEmpty(value))
			old.setDescription(value);
		val = latest.getCapacity();
		if (value != null)
			old.setCapacity(val);
		value = Double.toString(latest.getLatitude());
		if (value != null)
			old.setLatitude(Double.parseDouble(value));
		value = Double.toString(latest.getLongtitude());
		if (value != null)
			old.setLongtitude(Double.parseDouble(value));
		value = Double.toString(latest.getCostPerHour());
		if (value != null)
			old.setCostPerHour(Double.parseDouble(value));
		value = Double.toString(latest.getCostPerDay());
		if (value != null)
			old.setCostPerDay(Double.parseDouble(value));
		value = Double.toString(latest.getCostPerMonth());
		if (value != null)
			old.setCostPerMonth(Double.parseDouble(value));
		value = latest.getContactNo();
		if (!TextUtils.isEmpty(value))
			old.setContactNo(value);
		value = latest.getAlternateContactNo();
		if (!TextUtils.isEmpty(value))
			old.setAlternateContactNo(value);
		value = latest.getEmailId();
		if (!TextUtils.isEmpty(value))
			old.setEmailId(value);
		value = latest.getAlternateEmailId();
		if (!TextUtils.isEmpty(value))
			old.setAlternateEmailId(value);
		val = old.getStatus();
		if (!latest.isSave()) {
			if (val == -1 || val == -2)
				old.setStatus(val * -1);
			else if (val == 3 || val == 4)
				old.setStatus(latest.getHuddleVerified() + 1);
			else if (val == 6)
				old.setStatus(5);
		} else
			old.setStatus((latest.getHuddleVerified() + 1) * -1);
		val = old.getLocation().getId();
		Location location = null;
		if (latest.getLocation() != null && latest.getLocation().getId() != null
				&& latest.getLocation().getId() != val) {
			location = huddilEM.find(Location.class, latest.getLocation().getId());
			if (location != null) {
				old.setCity(location.getCity().getName());
				old.setLocality(location.getLocality().getName());
			} else
				return -4;
		}
		if (latest.getFacilityCancellationCharges() != null) {
			StoredProcedureQuery spQuery = huddilEM.createStoredProcedureQuery("cancellationPolicyId")
					.registerStoredProcedureParameter("v_duration1", Integer.class, ParameterMode.IN)
					.registerStoredProcedureParameter("v_percentage1", Double.class, ParameterMode.IN)
					.registerStoredProcedureParameter("v_duration2", Integer.class, ParameterMode.IN)
					.registerStoredProcedureParameter("v_percentage2", Double.class, ParameterMode.IN)
					.registerStoredProcedureParameter("v_duration3", Integer.class, ParameterMode.IN)
					.registerStoredProcedureParameter("v_percentage3", Double.class, ParameterMode.IN)
					.registerStoredProcedureParameter("v_cancellationPolicyId", Integer.class, ParameterMode.OUT)
					.setParameter("v_duration1", latest.getFacilityCancellationCharges().getDuration1())
					.setParameter("v_percentage1", latest.getFacilityCancellationCharges().getPercentage1())
					.setParameter("v_duration2", latest.getFacilityCancellationCharges().getDuration2())
					.setParameter("v_percentage2", latest.getFacilityCancellationCharges().getPercentage2())
					.setParameter("v_duration3", latest.getFacilityCancellationCharges().getDuration3())
					.setParameter("v_percentage3", latest.getFacilityCancellationCharges().getPercentage3());
			spQuery.execute();
			int policyId = Integer.parseInt(spQuery.getOutputParameterValue("v_cancellationPolicyId").toString());
			old.setFacilityCancellationCharges(huddilEM.find(FacilityCancellationCharges.class, policyId));
		}
		if (latest.getFacilityTimings() != null) {
			for (FacilityTiming timeNew : latest.getFacilityTimings()) {
				for (FacilityTiming timeOld : old.getFacilityTimings()) {
					if (timeNew.getWeekDay() == timeOld.getWeekDay()) {
						timeOld.setOpeningTime(timeNew.getOpeningTime());
						timeOld.setClosingTime(timeNew.getClosingTime());
					}
				}
			}
		}

		Set<FacilityAmenity> facilityAmenities = latest.getFacilityAmenities();
		Set<FacilityAmenity> oldAmenities = old.getFacilityAmenities();
		if (facilityAmenities != null && !facilityAmenities.isEmpty()) {
			for (FacilityAmenity facilityAmenity : facilityAmenities) {
				if (facilityAmenity.isDelete())
					huddilEM.createQuery("DELETE FROM FacilityAmenity f WHERE f.id = :id")
							.setParameter("id", facilityAmenity.getId()).executeUpdate();
				else {
					oldAmenities.add(facilityAmenity);
					facilityAmenity.setFacility(old);
				}
			}
		}

		Set<FacilityPhoto> facilityPhotos = latest.getFacilityPhotos();
		Set<FacilityPhoto> oldPhotos = old.getFacilityPhotos();
		if (facilityPhotos != null && !facilityPhotos.isEmpty()) {
			for (FacilityPhoto facilityPhoto : facilityPhotos) {
				if (facilityPhoto.isDelete())
					huddilEM.createQuery("DELETE FROM FacilityPhoto p WHERE p.id = :id")
							.setParameter("id", facilityPhoto.getId()).executeUpdate();
				else {
					oldPhotos.add(facilityPhoto);
					facilityPhoto.setFacility(old);
				}
			}
		}

		Set<FacilityOffers> facilityOffers = latest.getFacilityOfferses();
		Set<FacilityOffers> oldOffers = old.getFacilityOfferses();
		if (facilityOffers != null && !facilityOffers.isEmpty()) {
			for (FacilityOffers faOffers : facilityOffers) {
				if (faOffers.isDelete())
					huddilEM.createQuery("DELETE FROM FacilityOffers f WHERE f.id = :id")
							.setParameter("id", faOffers.getId()).executeUpdate();
				else {
					faOffers.setFacility(old);
					oldOffers.add(faOffers);
				}
			}
		}
		huddilEM.merge(old);
		miscDao.addEvents("Resubmitted facility:-  " + old.getTitle() + " by", user.getId());
		huddilEM.createNativeQuery(
				"INSERT INTO huddil.facility_history(oldStatus, comments, facilityId, userId)values(:status, :comments, :facilityId, :userId)")
				.setParameter("status", val).setParameter("facilityId", latest.getId())
				.setParameter("comments", "resubmitting").setParameter("userId", user.getId()).executeUpdate();
		return 1;
	}

	@Override
	public int deleteFacility(String sessionId, int facilityId) {
		UserSearchResult user = getUserPreference(sessionId);
		if (user == null)
			return -1;
		if (user.getUserType() != UserType.serviceprovider)
			return -2;
		Facility facility = huddilEM.find(Facility.class, facilityId);
		if (facility == null)
			return -3;
		if (facility.getStatus() > -1)
			return -4;
		if (facility.getUserPrefBySpUserId() != user.getId())
			return -5;
		return huddilEM.createNativeQuery("DELETE FROM facility WHERE id = :id").setParameter("id", facilityId)
				.executeUpdate();
	}

	@Override
	public List<Facility> getFacility(String sessionId, String type) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Facility getFacility(String sessionId, int id) {
		Object obj = null;
		if (sessionId != null) {
			UserSearchResult user = getUserPreference(sessionId);
			if (user != null)
				obj = huddilEM.createNativeQuery(
						"SELECT f.id FROM huddil.favorites f WHERE f.facilityId =:facilityId AND f.userId =:userId")
						.setParameter("facilityId", id).setParameter("userId", user.getId()).getSingleResult();
		}

		Object obj1 = huddilEM.find(Facility.class, id);

		if (obj1 == null)
			return null;
		Facility facility = (Facility) obj1;
		if (obj != null)
			facility.setFavorites(true);
		else
			facility.setFavorites(false);
		return facility;
	}

	@Override
	public void deleteFacility(Facility facility, String sessionId) {
		// TODO Auto-generated method stub

	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Facility> getFacilities(String sessionId, int locationId) {
		UserSearchResult user = getUserPreference(sessionId);
		if (user != null)
			return huddilEM.createQuery("SELECT f FROM Facility f JOIN FETCH f.location l where l.id =:locationId")
					.setParameter("locationId", locationId).getResultList();
		return null;
	}

	@Override
	public int addMultitenant(MultiTenant multitenant, String sessionId) {
		Object obj = huddilEM.createQuery("SELECT u FROM UserPref u WHERE u.sessionId = :sessionId")
				.setParameter("sessionId", sessionId).getSingleResult();
		if (obj == null)
			return -1;
		UserPref user = (UserPref) obj;
		if (user.getUserType() != UserType.serviceprovider)
			return -2;
		multitenant.setUserPref(user);
		huddilEM.persist(multitenant);
		return 1;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<MultiTenant> getMultitenant(String sessionId) {
		UserSearchResult user = getUserPreference(sessionId);
		List<MultiTenant> tenants = new ArrayList<MultiTenant>();
		if (user == null)
			tenants.add(new MultiTenant(-1));
		else if (user.getUserType() != UserType.serviceprovider)
			tenants.add(new MultiTenant(-2));
		else
			tenants = huddilEM
					.createQuery("SELECT m FROM MultiTenant m JOIN FETCH m.userPref p WHERE p.sessionId =:sessionId")
					.setParameter("sessionId", sessionId).getResultList();
		return tenants;
	}

	@Override
	public int updateMultitenant(MultiTenant multitenant, String sessionId) {
		UserSearchResult user = getUserPreference(sessionId);
		if (user == null)
			return -1;
		if (user.getUserType() != UserType.serviceprovider)
			return -2;
		return huddilEM.createNativeQuery(
				"UPDATE huddil.multi_tenant m SET m.title =:title, m.description =:description, m.emailId =:emailId WHERE m.id =:id")
				.setParameter("title", multitenant.getTitle()).setParameter("description", multitenant.getDescription())
				.setParameter("emailId", multitenant.getEmailId()).setParameter("id", multitenant.getId())
				.executeUpdate();
	}

	@Override
	public boolean deleteMultitenant(int id, String sessionId) {
		UserSearchResult user = getUserPreference(sessionId);
		if (user == null || user.getUserType() != UserType.administrator)
			return false;
		return huddilEM.createNativeQuery("DELETE m.* FROM huddil.multi_tenant m WHERE m.id =:id")
				.setParameter("id", id).executeUpdate() == 0 ? false : true;
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
	public int addOffer(FacilityOffers facilityOffers, String sessionId) {
		StoredProcedureQuery spQuery = huddilEM.createStoredProcedureQuery("facility_offers")
				.registerStoredProcedureParameter("v_sessionId", String.class, ParameterMode.IN)
				.registerStoredProcedureParameter("v_startDate", Date.class, ParameterMode.IN)
				.registerStoredProcedureParameter("v_endDate", Date.class, ParameterMode.IN)
				.registerStoredProcedureParameter("v_price", Double.class, ParameterMode.IN)
				.registerStoredProcedureParameter("v_facilityId", Integer.class, ParameterMode.IN)
				.registerStoredProcedureParameter("v_result", Integer.class, ParameterMode.OUT)
				.setParameter("v_sessionId", sessionId).setParameter("v_startDate", facilityOffers.getStartDate())
				.setParameter("v_endDate", facilityOffers.getEndDate())
				.setParameter("v_price", facilityOffers.getPrice())
				.setParameter("v_facilityId", facilityOffers.getFacility().getId());
		spQuery.execute();
		int flag = (int) spQuery.getOutputParameterValue("v_result");
		return flag;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<FacilityOffers> getOffers(String sessionId, int facilityId) {
		UserSearchResult user = getUserPreference(sessionId);
		List<FacilityOffers> offers = new ArrayList<FacilityOffers>();
		if (user == null)
			offers.add(new FacilityOffers(-1));
		else if (user.getUserType() != UserType.serviceprovider)
			offers.add(new FacilityOffers(-2));
		else
			offers = huddilEM
					.createQuery("SELECT o FROM FacilityOffers o JOIN FETCH o.facility f WHERE f.id =:facilityId")
					.setParameter("facilityId", facilityId).getResultList();
		return offers;
	}

	@Override
	public int deleteOffer() {
		StoredProcedureQuery spQuery = huddilEM.createStoredProcedureQuery("deleteOffer")
				.registerStoredProcedureParameter("v_result", Integer.class, ParameterMode.OUT);
		spQuery.execute();
		return (int) spQuery.getOutputParameterValue("v_result");

	}

	@Override
	public int facilityundermaintenance(FacilityUnderMaintenance maintenance, String sessionId) {
		UserSearchResult user = getUserPreference(sessionId);
		if (user == null)
			return -1;
		else if (user.getUserType() != UserType.serviceprovider)
			return -2;
		else {
			Object obj = huddilEM.createNativeQuery(
					"SELECT f.spUserId FROM huddil.facility f JOIN huddil.user_pref p ON p.userId = f.spUserId WHERE p.sessionId =:sessionId AND f.id =:facilityId")
					.setParameter("sessionId", sessionId).setParameter("facilityId", maintenance.getFacility().getId())
					.getSingleResult();
			if (obj == null)
				return 0;
		}
		maintenance.setFacility(huddilEM.find(Facility.class, maintenance.getFacility().getId()));
		huddilEM.persist(maintenance);
		return 1;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<FacilityUnderMaintenance> facilityundermaintenance(String sessionId) {
		UserSearchResult user = getUserPreference(sessionId);
		List<FacilityUnderMaintenance> maintenances = new ArrayList<FacilityUnderMaintenance>();
		if (user == null)
			maintenances.add(new FacilityUnderMaintenance(-1));
		else if (user.getUserType() != UserType.serviceprovider)
			maintenances.add(new FacilityUnderMaintenance(-2));
		else
			maintenances = huddilEM.createQuery(
					"SELECT m FROM FacilityUnderMaintenance m JOIN FETCH m.facility f WHERE f.userPrefBySpUserId = :userId")
					.setParameter("userId", user.getId()).getResultList();
		return maintenances;
	}

	@Override
	public int updateFacilityUnderMaintenance(FacilityUnderMaintenance maintenance, String sessionId) {
		UserSearchResult user = getUserPreference(sessionId);
		if (user == null)
			return -1;
		else if (user.getUserType() != UserType.serviceprovider)
			return -2;
		Facility facility = huddilEM.find(Facility.class, maintenance.getFacility().getId());
		if (user.getId() == facility.getUserPrefBySpUserId()) {
			huddilEM.merge(maintenance);
			return 1;
		}
		return 0;
	}

	@Override
	public int facilityundermaintenance(String sessionId, int id) {
		UserSearchResult user = getUserPreference(sessionId);
		if (user == null)
			return -1;
		else if (user.getUserType() != UserType.serviceprovider)
			return -2;
		return huddilEM.createNativeQuery(
				"DELETE m.* FROM huddil.facility_under_maintenance m JOIN huddil.facility f ON f.id = m.facilityId JOIN huddil.user_pref p ON p.userId = f.spUserId WHERE p.sessionId =:sessionId AND m.id =:id")
				.setParameter("sessionId", sessionId).setParameter("id", id).executeUpdate();
	}

	@Override
	public int additionalCost(String sessionId, FacilityAdditionalCost additionalCost) {
		UserSearchResult user = getUserPreference(sessionId);
		if (user == null)
			return -1;
		else if (user.getUserType() != UserType.serviceprovider)
			return -2;
		Facility facility = huddilEM.find(Facility.class, additionalCost.getFacility().getId());
		if (user.getId() == facility.getUserPrefBySpUserId()) {
			return huddilEM.createNativeQuery(
					"INSERT INTO huddil.facility_additional_cost(facilityId,name,description,price) VALUES(:facilityId,:name,:description,:price)")
					.setParameter("facilityId", additionalCost.getFacility().getId())
					.setParameter("name", additionalCost.getName())
					.setParameter("description", additionalCost.getDescription())
					.setParameter("price", additionalCost.getPrice()).executeUpdate();

		}
		return 0;
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<FacilityAdditionalCost> additionalCost(String sessionId, int facilityId) {
		UserSearchResult user = getUserPreference(sessionId);
		List<FacilityAdditionalCost> costs = new ArrayList<FacilityAdditionalCost>();
		if (user == null)
			costs.add(new FacilityAdditionalCost(-1));
		else if (user.getUserType() != UserType.serviceprovider)
			costs.add(new FacilityAdditionalCost(-2));
		else
			costs = huddilEM
					.createQuery(
							"SELECT a FROM FacilityAdditionalCost a JOIN FETCH a.facility f WHERE f.id =:facilityId")
					.setParameter("facilityId", facilityId).getResultList();
		return costs;
	}

	@SuppressWarnings("unchecked")
	@Override
	public int review(CustomerReview reviews, String sessionId) {

		Object obj = null;
		UserSearchResult user = getUserPreference(sessionId);
		if (user == null)
			return -1;
		else if (user.getUserType() != UserType.consumer && user.getUserType() != UserType.serviceprovider)
			return -2;
		if (reviews.getParentId() == 0) {
			obj = huddilEM.createNativeQuery(
					"SELECT b.facilityId FROM huddil.booking_history b WHERE b.bookingId =:bookingId AND b.userId =:userId")
					.setParameter("bookingId", reviews.getId()).setParameter("userId", user.getId()).getSingleResult();
			if (obj == null)
				return 1;
		}

		if (reviews.getParentId() != 0) {
			obj = huddilEM
					.createNativeQuery(
							"SELECT b.facilityId FROM huddil.booking_history b WHERE b.bookingId =:bookingId")
					.setParameter("bookingId", reviews.getId()).getSingleResult();

			if (obj == null)
				return 1;
		}

		Query query = huddilEM.createNativeQuery(
				"INSERT INTO huddil.review(comments, rating, parentId, facilityId, userId, bookingId) values(:comments, :rating, :parentId, :facilityId, :userId, :bookingId)")
				.setParameter("comments", reviews.getComments()).setParameter("rating", reviews.getRating())
				.setParameter("parentId", null).setParameter("facilityId", (int) obj)
				.setParameter("userId", user.getId()).setParameter("bookingId", reviews.getId());

		if (reviews.getParentId() != 0) {
			int spUserId = (int) huddilEM.createNativeQuery(
					"SELECT f.spUserId FROM huddil.facility f JOIN huddil.booking_history b ON b.facilityId = f.id WHERE b.bookingId =:bookingId")
					.setParameter("bookingId", reviews.getId()).getSingleResult();
			if (user.getId() == spUserId) {
				query.setParameter("rating", 0.0);
				query.setParameter("bookingId", reviews.getId());
				query.setParameter("parentId", reviews.getParentId());
				query.executeUpdate();
				return 0;
			} else
				return 1;
		}
		List<Integer> userId = huddilEM
				.createNativeQuery(
						"SELECT b.userId FROM booking_history b where b.facilityId =:facilityId AND b.userId =:userId")
				.setParameter("facilityId", (int) obj).setParameter("userId", user.getId()).getResultList();
		if (userId == null || userId.isEmpty())
			return 1;
		query.executeUpdate();
		Object obj1 = huddilEM.createNativeQuery(
				"SELECT COUNT(id) as count, SUM(rating) as sum FROM review WHERE facilityId =:facilityId AND parentId IS NULL",
				"reviewRating").setParameter("facilityId", (int) obj).getSingleResult();
		ReviewRating review = (ReviewRating) obj1;
		double rating = 0;
		rating = review.getSumRating() / review.getCount().intValue();
		huddilEM.createNativeQuery("UPDATE facility f SET f.averageRating =:rating WHERE f.id =:facilityId")
				.setParameter("rating", rating).setParameter("facilityId", (int) obj).executeUpdate();

		return 0;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Review> review(int facilityId) {
		return huddilEM.createQuery("SELECT r FROM Review r WHERE r.facility = :facility AND r.review IS NULL")
				.setParameter("facility", facilityId).getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Review> reviewbyBookingId(int bookingId) {
		Object obj1 = huddilEM
				.createNativeQuery("SELECT b.facilityId FROM huddil.booking_history b WHERE b.bookingId =:bookingId")
				.setParameter("bookingId", bookingId).getSingleResult();
		if (obj1 == null)
			return null;
		return huddilEM.createQuery("SELECT r FROM Review r WHERE r.bookingHistory =:bookingId")
				.setParameter("bookingId", bookingId).getResultList();
	}

	@Override
	public boolean delReview(String sessionId, int id) {
		Object obj = huddilEM
				.createNativeQuery("SELECT p.userId FROM huddil.user_pref p  WHERE p.sessionId =:sessionId")
				.setParameter("sessionId", sessionId).getSingleResult();
		if (obj == null)
			return false;

		return huddilEM.createNativeQuery("DELETE r.* FROM huddil.review r WHERE r.id =:id AND r.userId =:userId")
				.setParameter("id", id).setParameter("userId", Integer.parseInt(obj.toString())).executeUpdate() == 0
						? false
						: true;
	}

	@Override
	public int addLocation(String sessionId, Location location) {
		UserSearchResult user = getUserPreference(sessionId);
		if (user == null)
			return -1;
		if (user.getUserType() != UserType.serviceprovider)
			return -2;
		Object obj = huddilEM.createNativeQuery("SELECT l.name FROM locality l WHERE l.cityId =:cityId AND l.id =:id")
				.setParameter("cityId", location.getCity().getId()).setParameter("id", location.getLocality().getId())
				.getSingleResult();
		if (obj == null)
			return 1;
		location.setUserPref(user.getId());
		if (location.getFacilityTermsConditionses() != null) {
			for (FacilityTermsConditions conditions : location.getFacilityTermsConditionses()) {
				conditions.setLocation(location);
				conditions.setCreatedDate(new Date());
			}
		}
		huddilEM.persist(location);
		return 0;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Location> getLocation(int id) {
		return huddilEM.createQuery("SELECT l FROM Location l JOIN FETCH l.locality k WHERE k.id= :id")
				.setParameter("id", id).getResultList();

	}

	@SuppressWarnings("unchecked")
	@Override
	public List<LocationDetails> getLocation(String sessionId) {
		UserSearchResult user = getUserPreference(sessionId);
		List<LocationDetails> locations = new ArrayList<LocationDetails>();
		if (user == null)
			locations.add(new LocationDetails(-1));
		else if (user.getUserType() != UserType.serviceprovider)
			locations.add(new LocationDetails(-2));
		else
			locations = huddilEM.createNativeQuery(
					"SELECT l.id, c.name as city,lo.name as locality, l.name as locationName, l.address, l.landmark, l.nearBy, t.description FROM huddil.location l JOIN huddil.city c ON c.id = l.cityId JOIN huddil.locality lo ON lo.id = l.localityId JOIN huddil.user_pref p ON p.userId = l.userId JOIN huddil.facility_terms_conditions t on l.id = t.loacationId WHERE p.sessionId =:sessionId",
					"locationDetails").setParameter("sessionId", sessionId).getResultList();
		return locations;
	}

	@Override
	public boolean updateLocation(Location location, String sessionId, int id) {
		UserPref pref = (UserPref) huddilEM.createQuery("SELECT u from UserPref u where u.sessionId= :sessionId")
				.setParameter("sessionId", sessionId).getSingleResult();
		if (pref != null)
			return (huddilEM.createNativeQuery("UPDATE huddil.location SET name= :name where id= :id")
					.setParameter("name", location.getName()).setParameter("id", id).executeUpdate() == 0) ? false
							: true;
		return false;
	}

	@Override
	public boolean deleteLocation(int id, String sessionId) {
		UserPref pref = (UserPref) huddilEM.createQuery("select u from UserPref u where u.sessionId= :sessionId")
				.setParameter("sessionId", sessionId).getSingleResult();
		if (pref != null)
			huddilEM.createNativeQuery("delete l.* from huddil.location l where l.id= :id").setParameter("id", id)
					.executeUpdate();
		return false;
	}

	public int updateFacilityStatus(String sessionId, int id, int status, String comments)
			throws MessagingException, IOException {
		int oldStatus = 0;
		UserSearchResult user = getUserPreference(sessionId);
		if (user == null)
			return -1;
		if (user.getUserType() != UserType.advisor && user.getUserType() != UserType.administrator)
			return -2;
		int spUserId = (int) huddilEM.createNativeQuery("SELECT f.spUserId FROM huddil.facility f WHERE f.id =:id")
				.setParameter("id", id).getSingleResult();
		int isActive = getUserStatus(spUserId);
		if (isActive == 2)
			return 2;
		Facility facility = huddilEM.find(Facility.class, id);
		if (facility == null)
			return 0;
		int newStatus = 0;
		oldStatus = facility.getStatus();

		if (oldStatus == 3 || oldStatus == 4 || oldStatus == 6 || oldStatus == 9 || oldStatus == 10
				|| (oldStatus == 13 && user.getUserType() != UserType.administrator)
				|| (oldStatus == 14 && user.getUserType() != UserType.administrator)
				|| ((oldStatus == 7 || oldStatus == 8) && status == 1)
				|| ((oldStatus == 12 || oldStatus == 11) && status == 0)
				|| ((oldStatus == 13 || oldStatus == 14) && status == 0))
			return 0;

		if (oldStatus == 1 && status == 0)
			newStatus = 3;
		else if (oldStatus == 2 && status == 0)
			newStatus = 4;
		else if (oldStatus == 2 && status == 1)
			newStatus = 5;
		else if (oldStatus == 5 && status == 0)
			newStatus = 7;
		else if (oldStatus == 1 && status == 1)
			newStatus = 7;
		else if (oldStatus == 5 && status == 1)
			newStatus = 8;
		else if (oldStatus == 7 && status == 0 && user.getUserType() == UserType.advisor)
			newStatus = 11;
		else if (oldStatus == 8 && status == 0 && user.getUserType() == UserType.advisor)
			newStatus = 12;
		else if (oldStatus == 7 && status == 0 && user.getUserType() == UserType.administrator)
			newStatus = 13;
		else if (oldStatus == 8 && status == 0 && user.getUserType() == UserType.administrator)
			newStatus = 14;
		else if (oldStatus == 13 && status == 1 && user.getUserType() == UserType.administrator)
			newStatus = 7;
		else if (oldStatus == 14 && status == 1 && user.getUserType() == UserType.administrator)
			newStatus = 8;
		else if (oldStatus == 12 && status == 1)
			newStatus = 8;
		else if (oldStatus == 11 && status == 1)
			newStatus = 7;

		boolean value = huddilEM.createNativeQuery("UPDATE huddil.facility f SET f.status =:status WHERE f.id =:id")
				.setParameter("status", newStatus).setParameter("id", id).executeUpdate() == 0 ? false : true;

		String title = (String) huddilEM.createNativeQuery("SELECT f.title FROM facility f WHERE f.id  =:id")
				.setParameter("id", id).getSingleResult();
		if (value == true) {
			if (oldStatus == 5 && status == 0 && user.getUserType() == UserType.advisor)
				miscDao.addEvents("Facility" + title + " Huddil Verification Request Rejected By Advisor",
						user.getId());
			else if (oldStatus == 5 && status == 1 && user.getUserType() == UserType.advisor)
				miscDao.addEvents("Facility" + title + " Huddil Verification Request Approved By Advisor",
						user.getId());
			else if (oldStatus == 5 && status == 0 && user.getUserType() == UserType.administrator)
				miscDao.addEvents("Facility" + title + " Huddil Verification Request Rejected By Administrator",
						user.getId());
			else if (oldStatus == 5 && status == 1 && user.getUserType() == UserType.administrator)
				miscDao.addEvents("Facility" + title + " Huddil Verification Request Approved By Administrator",
						user.getId());
			else if ((oldStatus == 1 || oldStatus == 2) && status == 0 && user.getUserType() == UserType.advisor)
				miscDao.addEvents("Facility" + title + " Create Request Rejected By Advisor", user.getId());
			else if (oldStatus == 1 && status == 1 && user.getUserType() == UserType.advisor)
				miscDao.addEvents("Facility" + title + " Create Request Approved By Advisor", user.getId());
			else if (oldStatus == 2 && status == 1 && user.getUserType() == UserType.advisor)
				miscDao.addEvents("Facility" + title + " Create Request Approved BY Advisor, Verification Pending",
						user.getId());
			else if ((oldStatus == 1 || oldStatus == 2) && status == 0 && user.getUserType() == UserType.administrator)
				miscDao.addEvents("Facility" + title + " Create Request Rejected By Administrator", user.getId());
			else if (oldStatus == 1 && status == 1 && user.getUserType() == UserType.administrator)
				miscDao.addEvents("Facility" + title + " Create Request Approved By Administrator", user.getId());
			else if (oldStatus == 2 && status == 1 && user.getUserType() == UserType.administrator)
				miscDao.addEvents(
						"Facility" + title + " Create Request Approved BY Administrator, Verification Pending",
						user.getId());
			else if ((oldStatus == 7 || oldStatus == 8) && status == 0 && user.getUserType() == UserType.advisor)
				miscDao.addEvents("Facility" + title + " Blocked By Advisor", user.getId());
			else if ((oldStatus == 11 || oldStatus == 12) && status == 1 && user.getUserType() == UserType.advisor)
				miscDao.addEvents("Facility" + title + " Enabled By Advisor", user.getId());
			else if ((oldStatus == 7 || oldStatus == 8) && status == 0 && user.getUserType() == UserType.administrator)
				miscDao.addEvents("Facility" + title + " Blocked By Administrator", user.getId());
			else if ((oldStatus == 13 || oldStatus == 14) && status == 1
					&& user.getUserType() == UserType.administrator)
				miscDao.addEvents("Facility" + title + " Enabled By Administrator", user.getId());
			huddilEM.createNativeQuery(
					"INSERT INTO huddil.facility_history(oldStatus, comments, facilityId, userId)values(:status, :comments, :facilityId, :userId)")
					.setParameter("status", oldStatus).setParameter("facilityId", id).setParameter("comments", comments)
					.setParameter("userId", user.getId()).executeUpdate();
			UserPref userPref = huddilEM.find(UserPref.class, facility.getUserPrefBySpUserId());
			if (newStatus > 10 && newStatus < 15)
				Notifications.sendBlockedNotification(userPref.getEmailId(), userPref.getMobileNo(),
						userPref.getDisplayName(), facility.getTitle(), comments);
			return 1;
		} else
			return 0;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<AdminFacilityDB> statusCount(String sessionId) {
		UserSearchResult user = getUserPreference(sessionId);
		List<AdminFacilityDB> counts = new ArrayList<AdminFacilityDB>();
		if (user == null)
			counts.add(new AdminFacilityDB(-1));
		else if (user.getUserType() != UserType.advisor)
			counts.add(new AdminFacilityDB(-2));
		else
			counts = huddilEM.createNativeQuery("SELECT COUNT(f.id) AS count, 1 AS status FROM status s "
					+ "LEFT JOIN facility f ON f.status = s.id WHERE s.id = 1 OR s.id = 2 UNION "
					+ " SELECT COUNT(f.id) AS count, 2 AS status FROM status s "
					+ "LEFT JOIN facility f ON f.status = s.id WHERE s.id = 3 OR s.id = 4 UNION "
					+ "SELECT COUNT(f.id) AS count, 3 AS status FROM status s "
					+ "LEFT JOIN facility f ON f.status = s.id WHERE s.id = 5 OR s.id = 6 OR s.id = 7 OR s.id =8 UNION "
					+ "SELECT COUNT(f.id) AS count, 4 AS status FROM status s "
					+ "LEFT JOIN facility f ON f.status = s.id WHERE s.id = 9 OR s.id = 10 OR s.id = 11 OR s.id = 12 OR s.id = 13 OR s.id = 14 UNION "
					+ "SELECT COUNT(f.id) AS count, 5 AS status FROM status s "
					+ "LEFT JOIN facility f ON f.status = s.id WHERE s.id = 5 UNION "
					+ "SELECT COUNT(f.id) AS count, 6 AS status FROM status s "
					+ "LEFT JOIN facility f ON f.status = s.id WHERE s.id = 8", "adminFacilityDB").getResultList();
		return counts;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<StatusCount> statusCountBySP(String sessionId) {
		UserSearchResult user = getUserPreference(sessionId);
		List<StatusCount> counts = new ArrayList<StatusCount>();
		if (user == null)
			counts.add(new StatusCount(-1));
		else if (user.getUserType() != UserType.serviceprovider)
			counts.add(new StatusCount(-2));
		else
			counts = huddilEM.createNativeQuery(
					"SELECT  count(f.id) as count, s.name as name FROM huddil.status s LEFT JOIN huddil.facility f ON f.status = s.id "
							+ "AND f.spUserId =:spUserId WHERE s.id > -1 group by s.name",
					"facilitystatus").setParameter("spUserId", user.getId()).getResultList();
		return counts;
	}

	@SuppressWarnings("unchecked")
	@Override
	public FacilityFilterResultPagination getFacility(String sessionId, int pageNo, int count) {
		UserSearchResult user = getUserPreference(sessionId);
		if (user == null)
			return new FacilityFilterResultPagination(-1);
		else if (user.getUserType() != UserType.serviceprovider)
			return new FacilityFilterResultPagination(-2);
		else {
			Date p_fromDateTime = new Date();
			Date p_toDateTime = new Date();

			StoredProcedureQuery spQuery = huddilEM
					.createStoredProcedureQuery("showAvailableFacilities", "facilityFilterView")
					.registerStoredProcedureParameter("v_sessionId", String.class, ParameterMode.IN)
					.registerStoredProcedureParameter("v_operation", Integer.class, ParameterMode.IN)
					.registerStoredProcedureParameter("v_fromDateTime", Timestamp.class, ParameterMode.IN)
					.registerStoredProcedureParameter("v_toDateTime", Timestamp.class, ParameterMode.IN)
					.registerStoredProcedureParameter("v_minCost", Double.class, ParameterMode.IN)
					.registerStoredProcedureParameter("v_maxCost", Double.class, ParameterMode.IN)
					.registerStoredProcedureParameter("v_maxCapacity", Integer.class, ParameterMode.IN)
					.registerStoredProcedureParameter("v_typeId", Integer.class, ParameterMode.IN)
					.registerStoredProcedureParameter("v_cityId", Integer.class, ParameterMode.IN)
					.registerStoredProcedureParameter("v_localityId", Integer.class, ParameterMode.IN)
					.registerStoredProcedureParameter("v_offers", Integer.class, ParameterMode.IN)
					.registerStoredProcedureParameter("v_amenity", String.class, ParameterMode.IN)
					.registerStoredProcedureParameter("v_pageNo", Integer.class, ParameterMode.IN)
					.registerStoredProcedureParameter("v_counting", Integer.class, ParameterMode.INOUT)
					.registerStoredProcedureParameter("v_result", Integer.class, ParameterMode.OUT)
					.setParameter("v_sessionId", sessionId).setParameter("v_operation", 2)
					.setParameter("v_fromDateTime", p_fromDateTime).setParameter("v_toDateTime", p_toDateTime)
					.setParameter("v_minCost", 0.0).setParameter("v_maxCost", 0.0).setParameter("v_maxCapacity", 0)
					.setParameter("v_typeId", 0).setParameter("v_cityId", 0).setParameter("v_localityId", 0)
					.setParameter("v_offers", 0).setParameter("v_amenity", " ").setParameter("v_pageNo", pageNo)
					.setParameter("v_counting", count);
			spQuery.execute();
			int result = Integer.parseInt(spQuery.getOutputParameterValue("v_result").toString());
			if (result == -1)
				return null;
			return new FacilityFilterResultPagination(spQuery.getOutputParameterValue("v_counting").toString(),
					spQuery.getResultList());
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Booking> getBookings(String sessionId, int facilityId) {
		UserSearchResult user = getUserPreference(sessionId);
		List<Booking> bookings = new ArrayList<Booking>();
		if (user == null)
			bookings.add(new Booking(-1));
		else if (user.getUserType() != UserType.serviceprovider)
			bookings.add(new Booking(-2));
		else
			bookings = huddilEM.createQuery(
					"SELECT b FROM Booking b JOIN FETCH b.facility f WHERE f.userPrefBySpUserId = :userId AND f.id =:facilityId")
					.setParameter("userId", user.getId()).setParameter("facilityId", facilityId).getResultList();
		return bookings;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Booking> getBookings(String sessionId) {
		UserSearchResult user = getUserPreference(sessionId);
		List<Booking> bookings = new ArrayList<Booking>();
		if (user == null)
			bookings.add(new Booking(-1));
		else if (user.getUserType() != UserType.serviceprovider)
			bookings.add(new Booking(-2));
		else
			bookings = huddilEM
					.createQuery("SELECT b FROM Booking b JOIN FETCH b.facility f WHERE f.userPrefBySpUserId = :userId")
					.setParameter("userId", user.getId()).getResultList();
		return bookings;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Booking> viewBookings(String sessionId, Optional<Integer> locationId, Optional<Integer> localityId,
			Optional<Integer> cityId, Optional<Date> fromDate, Optional<Date> toDate, Optional<Integer> id) {
		StoredProcedureQuery spQuery = huddilEM.createStoredProcedureQuery("filterBookingView")
				.registerStoredProcedureParameter("v_sessionId", Integer.class, ParameterMode.IN)
				.registerStoredProcedureParameter("v_locationId", Integer.class, ParameterMode.IN)
				.registerStoredProcedureParameter("v_localityId", Integer.class, ParameterMode.IN)
				.registerStoredProcedureParameter("v_cityId", Integer.class, ParameterMode.IN)
				.registerStoredProcedureParameter("v_fromDate", Date.class, ParameterMode.IN)
				.registerStoredProcedureParameter("v_toDate", Date.class, ParameterMode.IN)
				.registerStoredProcedureParameter("v_id", Integer.class, ParameterMode.IN);
		spQuery.execute();
		return spQuery.getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public FacilityFilterResultPagination getFacility(String sessionId, int cityId, int localityId, int locationId,
			int typeId, String search, int status, int pageNo, int count) {

		StoredProcedureQuery spQuery = huddilEM.createStoredProcedureQuery("filterFacilities", "facilityFilterView")
				.registerStoredProcedureParameter("v_sessionId", String.class, ParameterMode.IN)
				.registerStoredProcedureParameter("v_cityId", Integer.class, ParameterMode.IN)
				.registerStoredProcedureParameter("v_localityId", Integer.class, ParameterMode.IN)
				.registerStoredProcedureParameter("v_locationId", Integer.class, ParameterMode.IN)
				.registerStoredProcedureParameter("v_type", Integer.class, ParameterMode.IN)
				.registerStoredProcedureParameter("v_search", String.class, ParameterMode.IN)
				.registerStoredProcedureParameter("v_status", Integer.class, ParameterMode.IN)
				.registerStoredProcedureParameter("v_pageNo", Integer.class, ParameterMode.IN)
				.registerStoredProcedureParameter("v_count", Integer.class, ParameterMode.INOUT)
				.registerStoredProcedureParameter("v_flag", Integer.class, ParameterMode.OUT)
				.setParameter("v_sessionId", sessionId).setParameter("v_cityId", cityId)
				.setParameter("v_localityId", localityId).setParameter("v_locationId", locationId)
				.setParameter("v_type", typeId).setParameter("v_search", search).setParameter("v_status", status)
				.setParameter("v_pageNo", pageNo).setParameter("v_count", count);
		spQuery.execute();
		int flag = (int) spQuery.getOutputParameterValue("v_flag");
		if (flag == -1)
			return new FacilityFilterResultPagination(-1);
		if (flag == -2)
			return new FacilityFilterResultPagination(-2);
		if (flag == 1)
			return new FacilityFilterResultPagination(spQuery.getOutputParameterValue("v_count").toString(),
					spQuery.getResultList());
		return null;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Report> viewReport(String sessionId, int month, int year, int facilityType, int selection) {
		StoredProcedureQuery spQuery = huddilEM.createStoredProcedureQuery("bookingReport", "report")
				.registerStoredProcedureParameter("v_sessionId", String.class, ParameterMode.IN)
				.registerStoredProcedureParameter("v_month", Integer.class, ParameterMode.IN)
				.registerStoredProcedureParameter("v_year", Integer.class, ParameterMode.IN)
				.registerStoredProcedureParameter("v_type", Integer.class, ParameterMode.IN)
				.registerStoredProcedureParameter("v_selection", Integer.class, ParameterMode.IN)
				.registerStoredProcedureParameter("v_result", Integer.class, ParameterMode.OUT)
				.setParameter("v_sessionId", sessionId).setParameter("v_month", month).setParameter("v_year", year)
				.setParameter("v_type", facilityType).setParameter("v_selection", selection);
		spQuery.execute();
		List<Report> bookings = new ArrayList<Report>();
		int result = Integer.parseInt(spQuery.getOutputParameterValue("v_result").toString());
		if (result == -1)
			bookings.add(new Report(-1));
		else if (result == -2)
			bookings.add(new Report(-2));
		else
			bookings = spQuery.getResultList();
		return bookings;
	}

	@SuppressWarnings("unchecked")
	@Override
	public int addHistory(String comments, String sessionId, int id, int status, int confirm)
			throws IOException, AddressException, MessagingException {

		int p_operation = 0;
		int newStatus = 0;
		int result = 0;
		UserSearchResult user = getUserPreference(sessionId);
		if (user == null)
			return -1;
		else if (user.getUserType() != UserType.serviceprovider)
			return -2;
		Object obj = huddilEM
				.createNativeQuery("SELECT f.status FROM huddil.facility f WHERE f.id =:id AND f.spUserId =:userId")
				.setParameter("id", id).setParameter("userId", user.getId()).getSingleResult();
		if (obj == null)
			return 0;
		int oldStatus = 0;
		oldStatus = (int) obj;

		if (oldStatus != 5 && oldStatus != 7 && oldStatus != 8 && oldStatus != 9 && oldStatus != 10
				&& ((oldStatus == 7 || oldStatus == 8) && status == 1)
				&& (((oldStatus == 9) || (oldStatus == 10)) && status == 0))
			return 0;
		else if (oldStatus != 5 && oldStatus != 7 && oldStatus != 8 && oldStatus != 9 && oldStatus != 10)
			return 0;
		else if ((oldStatus == 9 || oldStatus == 10) && status == 0)
			return 0;
		else if ((oldStatus == 9 || oldStatus == 10) && confirm == 1)
			return 0;
		else if ((oldStatus == 8 || oldStatus == 7) && status == 1)
			return 0;
		if (oldStatus == 9 && status == 1 && user.getUserType() == 7)
			newStatus = 7;
		else if (oldStatus == 10 && status == 1 && user.getUserType() == 7)
			newStatus = 8;

		else if ((oldStatus == 5 && status == 0 && confirm == 1 && user.getUserType() == 7)
				|| (oldStatus == 7 && status == 0 && confirm == 1 && user.getUserType() == 7)
				|| (oldStatus == 8 && status == 0 && confirm == 1 && user.getUserType() == 7)) {
			if (oldStatus == 7 || oldStatus == 5)
				newStatus = 9;
			else
				newStatus = 10;
		}
		Object verificationStatus = huddilEM.createNativeQuery(
				"SELECT h.oldStatus FROM huddil.facility_history h WHERE h.facilityId =:facilityId order by h.dateTime desc LIMIT 1")
				.setParameter("facilityId", id).getSingleResult();

		if (verificationStatus != null && (int) verificationStatus == 5 && status == 1)
			newStatus = (int) verificationStatus;

		if (confirm == 0 && status == 0)
			p_operation = 3;
		else if (status == 0 && confirm == 1)
			p_operation = 4;

		Date p_fromDateTime = new Date();
		Date p_toDateTime = new Date();
		StoredProcedureQuery spQuery = null;
		if (oldStatus != 9 && oldStatus != 10 && (confirm == 0 || confirm == 1)) {
			spQuery = huddilEM.createStoredProcedureQuery("performCancellation", "cancellationMailData")
					.registerStoredProcedureParameter("p_type", Integer.class, ParameterMode.IN)
					.registerStoredProcedureParameter("p_bookingId", Integer.class, ParameterMode.IN)
					.registerStoredProcedureParameter("p_facilityId", Integer.class, ParameterMode.IN)
					.registerStoredProcedureParameter("p_sessionId", String.class, ParameterMode.IN)
					.registerStoredProcedureParameter("p_reason", String.class, ParameterMode.IN)
					.registerStoredProcedureParameter("p_operation", Integer.class, ParameterMode.IN)
					.registerStoredProcedureParameter("p_fromDateTime", Date.class, ParameterMode.IN)
					.registerStoredProcedureParameter("p_toDateTime", Date.class, ParameterMode.IN)
					.registerStoredProcedureParameter("p_cancel", Integer.class, ParameterMode.INOUT)
					.registerStoredProcedureParameter("p_result", Integer.class, ParameterMode.OUT)
					.registerStoredProcedureParameter("p_refund", Double.class, ParameterMode.OUT)
					.registerStoredProcedureParameter("p_cancellationPrice", Double.class, ParameterMode.OUT)
					.registerStoredProcedureParameter("p_totalPrice", Double.class, ParameterMode.OUT)
					.registerStoredProcedureParameter("p_count", Integer.class, ParameterMode.OUT)
					.setParameter("p_type", 3).setParameter("p_bookingId", 0).setParameter("p_facilityId", id)
					.setParameter("p_sessionId", sessionId).setParameter("p_reason", comments)
					.setParameter("p_operation", p_operation).setParameter("p_fromDateTime", p_fromDateTime)
					.setParameter("p_toDateTime", p_toDateTime).setParameter("p_cancel", 0);
			spQuery.execute();
			result = Integer.parseInt(spQuery.getOutputParameterValue("p_result").toString());
		}

		if (result == 17 && confirm == 0)
			return 1;
		else if (result == 16 && confirm == 0)
			return 2;

		boolean failure = false;
		if (result == 18) {

			List<CancellationNotificationTemplate> notificationTemplates = spQuery.getResultList();
			for (CancellationNotificationTemplate template : notificationTemplates) {
				InstaMojoRefund refund = InstaMojoService.getInstance().createRefund(template.getPaymentId(),
						template.getRefundAmt(), "", "huddil");
				if (refund != null) {
					huddilEM.createNativeQuery(
							"UPDATE cancellation SET refundId = :refundId WHERE paymentId = :paymentId")
							.setParameter("refundId", refund.getId()).setParameter("paymentId", refund.getPaymentId())
							.executeUpdate();
					Notifications.sendCancellationDetails(template, false);
				} else {
					spQuery = huddilEM.createStoredProcedureQuery("performCancellation", "cancellationMailData")
							.registerStoredProcedureParameter("p_type", Integer.class, ParameterMode.IN)
							.registerStoredProcedureParameter("p_bookingId", Integer.class, ParameterMode.IN)
							.registerStoredProcedureParameter("p_facilityId", Integer.class, ParameterMode.IN)
							.registerStoredProcedureParameter("p_sessionId", String.class, ParameterMode.IN)
							.registerStoredProcedureParameter("p_reason", String.class, ParameterMode.IN)
							.registerStoredProcedureParameter("p_operation", Integer.class, ParameterMode.IN)
							.registerStoredProcedureParameter("p_fromDateTime", Date.class, ParameterMode.IN)
							.registerStoredProcedureParameter("p_toDateTime", Date.class, ParameterMode.IN)
							.registerStoredProcedureParameter("p_cancel", Integer.class, ParameterMode.INOUT)
							.registerStoredProcedureParameter("p_result", Integer.class, ParameterMode.OUT)
							.registerStoredProcedureParameter("p_refund", Double.class, ParameterMode.OUT)
							.registerStoredProcedureParameter("p_cancellationPrice", Double.class, ParameterMode.OUT)
							.registerStoredProcedureParameter("p_totalPrice", Double.class, ParameterMode.OUT)
							.registerStoredProcedureParameter("p_count", Integer.class, ParameterMode.OUT)
							.setParameter("p_type", 4).setParameter("p_bookingId", template.getBookingId())
							.setParameter("p_facilityId", 0).setParameter("p_sessionId", sessionId)
							.setParameter("p_reason", comments).setParameter("p_operation", p_operation)
							.setParameter("p_fromDateTime", p_fromDateTime).setParameter("p_toDateTime", p_toDateTime)
							.setParameter("p_cancel", 0);
					spQuery.execute();
					failure = true;
				}
			}
		}
		if (failure == true) {
			spQuery = huddilEM.createStoredProcedureQuery("performCancellation", "cancellationMailData")
					.registerStoredProcedureParameter("p_type", Integer.class, ParameterMode.IN)
					.registerStoredProcedureParameter("p_bookingId", Integer.class, ParameterMode.IN)
					.registerStoredProcedureParameter("p_facilityId", Integer.class, ParameterMode.IN)
					.registerStoredProcedureParameter("p_sessionId", String.class, ParameterMode.IN)
					.registerStoredProcedureParameter("p_reason", String.class, ParameterMode.IN)
					.registerStoredProcedureParameter("p_operation", Integer.class, ParameterMode.IN)
					.registerStoredProcedureParameter("p_fromDateTime", Date.class, ParameterMode.IN)
					.registerStoredProcedureParameter("p_toDateTime", Date.class, ParameterMode.IN)
					.registerStoredProcedureParameter("p_cancel", Integer.class, ParameterMode.INOUT)
					.registerStoredProcedureParameter("p_result", Integer.class, ParameterMode.OUT)
					.registerStoredProcedureParameter("p_refund", Double.class, ParameterMode.OUT)
					.registerStoredProcedureParameter("p_cancellationPrice", Double.class, ParameterMode.OUT)
					.registerStoredProcedureParameter("p_totalPrice", Double.class, ParameterMode.OUT)
					.registerStoredProcedureParameter("p_count", Integer.class, ParameterMode.OUT)
					.setParameter("p_type", 4).setParameter("p_bookingId", 0).setParameter("p_facilityId", id)
					.setParameter("p_sessionId", sessionId).setParameter("p_reason", comments)
					.setParameter("p_operation", 5).setParameter("p_fromDateTime", p_fromDateTime)
					.setParameter("p_toDateTime", p_toDateTime).setParameter("p_cancel", 0);
			spQuery.execute();
			return 4;
		}

		if (p_operation == 0)
			huddilEM.createNativeQuery("UPDATE huddil.facility f SET f.status =:status where f.id= :id")
					.setParameter("status", newStatus).setParameter("id", id).executeUpdate();

		String title = (String) huddilEM.createNativeQuery("SELECT f.title FROM facility f WHERE f.id  =:id")
				.setParameter("id", id).getSingleResult();

		if (status == 0)
			miscDao.addEvents("Facility" + title + " disabled by service provider", user.getId());
		if (status == 1)
			miscDao.addEvents("Facility" + title + "enabled by service provider", user.getId());

		huddilEM.createNativeQuery(
				"INSERT INTO huddil.facility_history(oldStatus, comments, facilityId, userId)values(:status, :comments, :facilityId, :userId)")
				.setParameter("status", oldStatus).setParameter("facilityId", id).setParameter("comments", comments)
				.setParameter("userId", user.getId()).executeUpdate();
		return 3;
	}

	@SuppressWarnings("unchecked")
	@Override
	public FacilityFilterResultPagination facilityByAdvisor(String sessionId, int pageNo, int count) {
		UserSearchResult user = getUserPreference(sessionId);
		if (user == null || user.getUserType() != UserType.advisor)
			return null;
		Date p_fromDateTime = new Date();
		Date p_toDateTime = new Date();

		StoredProcedureQuery spQuery = huddilEM
				.createStoredProcedureQuery("showAvailableFacilities", "facilityFilterView")
				.registerStoredProcedureParameter("v_sessionId", String.class, ParameterMode.IN)
				.registerStoredProcedureParameter("v_operation", Integer.class, ParameterMode.IN)
				.registerStoredProcedureParameter("v_fromDateTime", Timestamp.class, ParameterMode.IN)
				.registerStoredProcedureParameter("v_toDateTime", Timestamp.class, ParameterMode.IN)
				.registerStoredProcedureParameter("v_minCost", Double.class, ParameterMode.IN)
				.registerStoredProcedureParameter("v_maxCost", Double.class, ParameterMode.IN)
				.registerStoredProcedureParameter("v_maxCapacity", Integer.class, ParameterMode.IN)
				.registerStoredProcedureParameter("v_typeId", Integer.class, ParameterMode.IN)
				.registerStoredProcedureParameter("v_cityId", Integer.class, ParameterMode.IN)
				.registerStoredProcedureParameter("v_localityId", Integer.class, ParameterMode.IN)
				.registerStoredProcedureParameter("v_offers", Integer.class, ParameterMode.IN)
				.registerStoredProcedureParameter("v_amenity", String.class, ParameterMode.IN)
				.registerStoredProcedureParameter("v_pageNo", Integer.class, ParameterMode.IN)
				.registerStoredProcedureParameter("v_counting", Integer.class, ParameterMode.INOUT)
				.registerStoredProcedureParameter("v_result", Integer.class, ParameterMode.OUT)
				.setParameter("v_sessionId", sessionId).setParameter("v_operation", 4)
				.setParameter("v_fromDateTime", p_fromDateTime).setParameter("v_toDateTime", p_toDateTime)
				.setParameter("v_minCost", 0.0).setParameter("v_maxCost", 0.0).setParameter("v_maxCapacity", 0)
				.setParameter("v_typeId", 0).setParameter("v_cityId", 0).setParameter("v_localityId", 0)
				.setParameter("v_offers", 0).setParameter("v_amenity", " ").setParameter("v_pageNo", pageNo)
				.setParameter("v_counting", count);
		spQuery.execute();
		int result = Integer.parseInt(spQuery.getOutputParameterValue("v_result").toString());
		if (result == -1)
			return null;
		return new FacilityFilterResultPagination(spQuery.getOutputParameterValue("v_counting").toString(),
				spQuery.getResultList());
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<AdminPaymentDB> paymentReport(String sessionId, int month, int year) {
		UserSearchResult user = getUserPreference(sessionId);
		List<AdminPaymentDB> adminPaymentDBs = new ArrayList<AdminPaymentDB>();
		if (user == null)
			adminPaymentDBs.add(new AdminPaymentDB(-2));
		else if (user.getUserType() != UserType.serviceprovider)
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
					.setParameter("p_month", month).setParameter("p_year", year).setParameter("p_city", "")
					.setParameter("p_spName", "").setParameter("p_spId", user.getId());
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
	public int disableAllFacilityOfSP(String sessionId, int userId, boolean status, String comments) {
		StoredProcedureQuery query = huddilEM.createStoredProcedureQuery("deactivateFacities")
				.registerStoredProcedureParameter("p_sessionId", String.class, ParameterMode.IN)
				.registerStoredProcedureParameter("p_userId", Integer.class, ParameterMode.IN)
				.registerStoredProcedureParameter("p_status", Boolean.class, ParameterMode.IN)
				.registerStoredProcedureParameter("p_result", Integer.class, ParameterMode.OUT)
				.setParameter("p_sessionId", sessionId).setParameter("p_userId", userId)
				.setParameter("p_status", status);
		query.execute();
		return (int) query.getOutputParameterValue("p_result");
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<FacilityHistory> getFacilityHistory(String sessionId, int id) {
		UserSearchResult user = getUserPreference(sessionId);
		List<FacilityHistory> histories = new ArrayList<FacilityHistory>();
		if (user == null)
			histories.add(new FacilityHistory(-1));
		else if (user.getUserType() != UserType.advisor)
			histories.add(new FacilityHistory(-2));
		else
			histories = huddilEM.createNativeQuery("SELECT h.* FROM huddil.facility_history h WHERE h.facilityId= :id",
					"facilityHistory").setParameter("id", id).getResultList();
		return histories;
	}

	@Override
	public int verifyHuddilRequest(String sessionId, int facilityId, int status) {
		UserSearchResult user = getUserPreference(sessionId);
		if (user == null)
			return -1;
		else if (user.getUserType() != UserType.serviceprovider)
			return -2;
		Object obj = huddilEM
				.createNativeQuery("SELECT f.status FROM huddil.facility f WHERE f.id =:id AND f.spUserId =:spUserId")
				.setParameter("id", facilityId).setParameter("spUserId", user.getId()).getSingleResult();
		if (obj == null)
			return 0;
		int newStatus = 0;
		int oldStatus = (int) obj;
		if (oldStatus == 7 && status == 1) {
			newStatus = 5;
			huddilEM.createNativeQuery("UPDATE huddil.facility f SET f.status =:status where f.id= :id")
					.setParameter("status", newStatus).setParameter("id", facilityId).executeUpdate();

			miscDao.addEvents("Facility" + facilityId + "Enabled By Service Provider", user.getId());

			huddilEM.createNativeQuery(
					"INSERT INTO huddil.facility_history(oldStatus, comments, facilityId, userId)values(:status, :comments, :facilityId, :userId)")
					.setParameter("status", oldStatus).setParameter("facilityId", facilityId)
					.setParameter("comments", "Request For Huddil Verification").setParameter("userId", user.getId())
					.executeUpdate();
			return 1;
		} else
			return 0;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<FacilityFilterResult> getNearBy(int facilityId, double lat, double longt, String type) {
		return huddilEM.createNativeQuery(
				"SELECT DISTINCT f.id, f.title, f.description,f.capacity, f.latitude, f.longtitude, f.costPerHour, f.costPerDay, f.costPerMonth, "
						+ "f.averageRating, f.size, f.status, f.contactNo, f.alternateContactNo, f.emailid, f.alternateEmailId, f.thumbnail, f.typeName, f.cityName as city, f.localityName as locality, "
						+ "lo.name as locationName, lo.landmark, lo.address, lo.nearBy, GROUP_CONCAT(DISTINCT am.id) as Amenities, GROUP_CONCAT(DISTINCT ph.imgPath) as imgPath, "
						+ "(3959 * acos(cos(radians(:lat)) * cos(radians(latitude)) * cos(radians(longtitude) - radians(:longt)) + sin(radians(:lat)) * sin(radians(latitude)))) AS distance "
						+ "FROM huddil.facility f JOIN huddil.facility_photo ph ON ph.facilityId = f.id JOIN huddil.user_pref p ON p.userId = f.spUserId "
						+ "JOIN huddil.facility_amenity a ON a.facilityId = f.id JOIN huddil.amenity am ON am.id = a.amenityId "
						+ "JOIN huddil.city c ON c.name = f.cityName JOIN huddil.locality l ON l.name = f.localityName "
						+ "JOIN huddil.location lo ON lo.id = f.locationId WHERE f.typeName = :type AND f.id <> :facilityId AND (f.status = 5 OR f.status = 6 OR f.status = 7 OR f.status = 8) GROUP BY f.id ORDER BY distance LIMIT 0,4",
				"facilityFilterView").setParameter("lat", lat).setParameter("longt", longt).setParameter("type", type)
				.setParameter("facilityId", facilityId).getResultList();
	}

	@Override
	public int addfavorities(String sessionId, int id) {
		UserSearchResult user = getUserPreference(sessionId);
		if (user == null)
			return -1;
		if (user.getUserType() != UserType.consumer)
			return -2;
		Facility facility = huddilEM.find(Facility.class, id);
		if (facility == null)
			return 1;
		boolean value = huddilEM
				.createNativeQuery(
						"INSERT IGNORE INTO huddil.favorites(facilityId, userId) VALUES(:facilityId, :userId)")
				.setParameter("userId", user.getId()).setParameter("facilityId", id).executeUpdate() == 0 ? false
						: true;
		if (value == true)
			return 0;
		return 2;
	}

	@SuppressWarnings("unchecked")
	@Override
	public FacilityFilterResultPagination getfavorities(String sessionId, int pageNo, int count) {
		Date p_fromDateTime = new Date();
		Date p_toDateTime = new Date();

		StoredProcedureQuery spQuery = huddilEM
				.createStoredProcedureQuery("showAvailableFacilities", "facilityFilterView")
				.registerStoredProcedureParameter("v_sessionId", String.class, ParameterMode.IN)
				.registerStoredProcedureParameter("v_operation", Integer.class, ParameterMode.IN)
				.registerStoredProcedureParameter("v_fromDateTime", Timestamp.class, ParameterMode.IN)
				.registerStoredProcedureParameter("v_toDateTime", Timestamp.class, ParameterMode.IN)
				.registerStoredProcedureParameter("v_minCost", Double.class, ParameterMode.IN)
				.registerStoredProcedureParameter("v_maxCost", Double.class, ParameterMode.IN)
				.registerStoredProcedureParameter("v_maxCapacity", Integer.class, ParameterMode.IN)
				.registerStoredProcedureParameter("v_typeId", Integer.class, ParameterMode.IN)
				.registerStoredProcedureParameter("v_cityId", Integer.class, ParameterMode.IN)
				.registerStoredProcedureParameter("v_localityId", Integer.class, ParameterMode.IN)
				.registerStoredProcedureParameter("v_offers", Integer.class, ParameterMode.IN)
				.registerStoredProcedureParameter("v_amenity", String.class, ParameterMode.IN)
				.registerStoredProcedureParameter("v_pageNo", Integer.class, ParameterMode.IN)
				.registerStoredProcedureParameter("v_counting", Integer.class, ParameterMode.INOUT)
				.registerStoredProcedureParameter("v_result", Integer.class, ParameterMode.OUT)
				.setParameter("v_sessionId", sessionId).setParameter("v_operation", 3)
				.setParameter("v_fromDateTime", p_fromDateTime).setParameter("v_toDateTime", p_toDateTime)
				.setParameter("v_minCost", 0.0).setParameter("v_maxCost", 0.0).setParameter("v_maxCapacity", 0)
				.setParameter("v_typeId", 0).setParameter("v_cityId", 0).setParameter("v_localityId", 0)
				.setParameter("v_offers", 0).setParameter("v_amenity", " ").setParameter("v_pageNo", pageNo)
				.setParameter("v_counting", count);
		spQuery.execute();
		int result = Integer.parseInt(spQuery.getOutputParameterValue("v_result").toString());
		if (result == -1)
			return null;
		return new FacilityFilterResultPagination(spQuery.getOutputParameterValue("v_counting").toString(),
				spQuery.getResultList());
	}

	@Override
	public int favorities(String sessionId, int id) {
		UserSearchResult user = getUserPreference(sessionId);
		if (user == null)
			return -1;
		if (user.getUserType() != UserType.consumer)
			return -2;
		return huddilEM.createNativeQuery(
				"DELETE f.* FROM huddil.favorites f JOIN huddil.user_pref p ON p.userId = f.userId WHERE p.sessionId =:sessionId AND f.facilityId =:id")
				.setParameter("id", id).setParameter("sessionId", sessionId).executeUpdate();
	}

	@SuppressWarnings("unchecked")
	@Override
	public FacilityFilterResultPagination getFacilityByConsumer(String sessionId, Timestamp fromTime, Timestamp toTime,
			double minCost, double maxCost, int maxCapacity, int facilityType, int cityId, int localityId, int offers,
			String amenity, int pageNo, int count) {
		StoredProcedureQuery spQuery = huddilEM
				.createStoredProcedureQuery("showAvailableFacilities", "facilityFilterView")
				.registerStoredProcedureParameter("v_sessionId", String.class, ParameterMode.IN)
				.registerStoredProcedureParameter("v_operation", Integer.class, ParameterMode.IN)
				.registerStoredProcedureParameter("v_fromDateTime", Timestamp.class, ParameterMode.IN)
				.registerStoredProcedureParameter("v_toDateTime", Timestamp.class, ParameterMode.IN)
				.registerStoredProcedureParameter("v_minCost", Double.class, ParameterMode.IN)
				.registerStoredProcedureParameter("v_maxCost", Double.class, ParameterMode.IN)
				.registerStoredProcedureParameter("v_maxCapacity", Integer.class, ParameterMode.IN)
				.registerStoredProcedureParameter("v_typeId", Integer.class, ParameterMode.IN)
				.registerStoredProcedureParameter("v_cityId", Integer.class, ParameterMode.IN)
				.registerStoredProcedureParameter("v_localityId", Integer.class, ParameterMode.IN)
				.registerStoredProcedureParameter("v_offers", Integer.class, ParameterMode.IN)
				.registerStoredProcedureParameter("v_amenity", String.class, ParameterMode.IN)
				.registerStoredProcedureParameter("v_pageNo", Integer.class, ParameterMode.IN)
				.registerStoredProcedureParameter("v_counting", Integer.class, ParameterMode.INOUT)
				.registerStoredProcedureParameter("v_result", Integer.class, ParameterMode.OUT)
				.setParameter("v_sessionId", sessionId == null ? "0" : sessionId).setParameter("v_operation", 1)
				.setParameter("v_fromDateTime", fromTime).setParameter("v_toDateTime", toTime)
				.setParameter("v_minCost", minCost).setParameter("v_maxCost", maxCost)
				.setParameter("v_maxCapacity", maxCapacity).setParameter("v_typeId", facilityType)
				.setParameter("v_cityId", cityId).setParameter("v_localityId", localityId)
				.setParameter("v_offers", offers).setParameter("v_amenity", amenity).setParameter("v_pageNo", pageNo)
				.setParameter("v_counting", count);
		spQuery.execute();
		int result = Integer.parseInt(spQuery.getOutputParameterValue("v_result").toString());
		if (result == -1)
			return null;
		return new FacilityFilterResultPagination(spQuery.getOutputParameterValue("v_counting").toString(),
				spQuery.getResultList());
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<FacilityFilterResult> searchFacilityByConsumer(String search) {
		List<FacilityFilterResult> facility = huddilEM.createNativeQuery(
				"SELECT DISTINCT f.id, f.title, f.description,f.capacity, f.latitude, f.longtitude, f.costPerHour, f.costPerDay, f.costPerMonth, f.averageRating, f.size, f.status, f.contactNo, f.alternateContactNo, f.emailid, f.alternateEmailId, f.thumbnail, f.typeName, f.cityName as city, f.localityName as locality, lo.name as locationName, lo.landmark, lo.address, lo.nearBy, GROUP_CONCAT(DISTINCT am.id) as Amenities, GROUP_CONCAT(DISTINCT ph.imgPath) as imgPath FROM huddil.facility f JOIN huddil.facility_photo ph ON ph.facilityId = f.id JOIN huddil.facility_amenity a ON a.facilityId = f.id JOIN huddil.amenity am ON am.id = a.amenityId JOIN huddil.city c ON c.name = f.cityName JOIN huddil.locality l ON l.name = f.localityName JOIN huddil.location lo ON lo.id = f.locationId WHERE f.status > -1 AND f.title LIKE :search group by f.id",
				"facilityFilterView").setParameter("search", "%" + search + "%").getResultList();
		return facility;
	}

	@SuppressWarnings("unchecked")
	@Override
	public FacilityFilterResultPagination getFacilityByAdmin(String sessionId, String search, String searchType,
			int facilityType, int pageNo, int count) {
		StoredProcedureQuery spQuery = huddilEM.createStoredProcedureQuery("adminFacility", "facilityFilterView")
				.registerStoredProcedureParameter("p_sessionId", String.class, ParameterMode.IN)
				.registerStoredProcedureParameter("p_search", String.class, ParameterMode.IN)
				.registerStoredProcedureParameter("p_searchType", String.class, ParameterMode.IN)
				.registerStoredProcedureParameter("p_facilityType", Integer.class, ParameterMode.IN)
				.registerStoredProcedureParameter("p_pageNo", Integer.class, ParameterMode.IN)
				.registerStoredProcedureParameter("p_count", Integer.class, ParameterMode.INOUT)
				.registerStoredProcedureParameter("p_result", Integer.class, ParameterMode.OUT)
				.setParameter("p_sessionId", sessionId).setParameter("p_search", search)
				.setParameter("p_searchType", searchType).setParameter("p_facilityType", facilityType)
				.setParameter("p_pageNo", pageNo).setParameter("p_count", count);
		spQuery.execute();
		int flag = (int) spQuery.getOutputParameterValue("p_result");
		if (flag == 0)
			return new FacilityFilterResultPagination(spQuery.getOutputParameterValue("p_count").toString(),
					spQuery.getResultList());
		else if (flag == -1)
			return new FacilityFilterResultPagination(-1);
		else if (flag == -2)
			return new FacilityFilterResultPagination(-2);
		else
			return null;
	}

	@Transactional(value = "wrapperTranscationManager")
	private int getUserStatus(int spUserId) {
		return (int) wrapperEntityManager.createNativeQuery("SELECT u.isActive FROM user_details u WHERE u.id =:userId")
				.setParameter("userId", spUserId).getSingleResult();

	}

	@Override
	public int updateFacilityPrice(String sessionId, int facilityId, double costPerHour, double costPerDay,
			double costPerMonth) {
		StoredProcedureQuery spQuery = huddilEM.createStoredProcedureQuery("updateFacilityPrice")
				.registerStoredProcedureParameter("p_sessionId", String.class, ParameterMode.IN)
				.registerStoredProcedureParameter("p_facilityId", Integer.class, ParameterMode.IN)
				.registerStoredProcedureParameter("p_costPerHour", Double.class, ParameterMode.IN)
				.registerStoredProcedureParameter("p_costPerDay", Double.class, ParameterMode.IN)
				.registerStoredProcedureParameter("p_costPerMonth", Double.class, ParameterMode.IN)
				.registerStoredProcedureParameter("p_result", Integer.class, ParameterMode.OUT)
				.setParameter("p_sessionId", sessionId).setParameter("p_facilityId", facilityId)
				.setParameter("p_costPerHour", costPerHour).setParameter("p_costPerDay", costPerDay)
				.setParameter("p_costPerMonth", costPerMonth);
		spQuery.execute();
		return (int) spQuery.getOutputParameterValue("p_result");
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<SavedFacility> getSavedFacility(String sessionId) {
		UserSearchResult user = getUserPreference(sessionId);
		List<SavedFacility> facilities = new ArrayList<SavedFacility>();
		if (user == null)
			facilities.add(new SavedFacility(-1));
		else if (user.getUserType() != UserType.serviceprovider)
			facilities.add(new SavedFacility(-2));
		else
			facilities = huddilEM.createNativeQuery(
					"SELECT f.id, f.title, f.cityName, f.typeName, l.name FROM facility f JOIN location l ON f.locationId = l.id "
							+ "JOIN user_pref u ON u.userId = f.spUserId WHERE u.sessionId = :sessionId AND (f.status = -1 OR f.status = -2)",
					"savedFacility").setParameter("sessionId", sessionId).getResultList();
		return facilities;
	}
}
