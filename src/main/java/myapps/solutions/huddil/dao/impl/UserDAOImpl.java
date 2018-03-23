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

import myapps.solutions.huddil.dao.IUserDAO;
import myapps.solutions.huddil.model.Booking;
import myapps.solutions.huddil.model.BookingNotificationTemplate;
import myapps.solutions.huddil.model.ContactInfo;
import myapps.solutions.huddil.model.Facility;
import myapps.solutions.huddil.model.Meeting;
import myapps.solutions.huddil.model.MeetingResult;
import myapps.solutions.huddil.model.Participants;
import myapps.solutions.huddil.model.ParticipantsTeam;
import myapps.solutions.huddil.model.UserPref;
import myapps.solutions.huddil.model.UserSearchResult;
import myapps.solutions.huddil.utils.Notifications;
import myapps.solutions.huddil.utils.UserType;

@Repository
@Transactional(value = "huddilTranscationManager")
public class UserDAOImpl implements IUserDAO {

	@PersistenceContext(unitName = "huddil")
	private EntityManager huddilEM;

	@PersistenceContext(unitName = "wrapper")
	private EntityManager wrapperEM;

	@SuppressWarnings("unchecked")
	@Override
	@Transactional(value = "wrapperTranscationManager")
	public List<UserSearchResult> searchUser(String sessionId, String emailId, int userType) {
		UserSearchResult user = getUserPreference(sessionId);
		List<UserSearchResult> result = new ArrayList<UserSearchResult>();
		if (user == null)
			result.add(new UserSearchResult(-1));
		else if (user.getUserType() != UserType.advisor && user.getUserType() != UserType.administrator)
			result.add(new UserSearchResult(-2));
		else
			result = wrapperEM.createNativeQuery(
					"SELECT u.id, u.addressingName AS name, u.mobileNo, u.emailId, u.isActive, s.productUserType AS userType FROM user_details u "
							+ "JOIN user_subscription s ON u.id = s.userId "
							+ "WHERE s.productUserType = :userType AND (u.emailId like :emailId OR u.addressingName like :emailId OR u.mobileNo like :emailId)  AND (u.isActive = 0 OR u.isActive = 1 OR u.isActive = 2 OR u.isActive = 3)",
					"user_search_details").setParameter("emailId", "%" + emailId + "%")
					.setParameter("userType", userType).getResultList();
		return result;
	}

	@Override
	@Transactional(value = "wrapperTranscationManager")
	public int deActivteUser(String sessionId, int id, boolean status) {
		UserSearchResult user = getUserPreference(sessionId);
		if (user == null)
			return -1;
		else if (user.getUserType() != UserType.advisor && user.getUserType() != UserType.administrator)
			return -2;
		StoredProcedureQuery query = wrapperEM.createStoredProcedureQuery("changeUserStatus")
				.registerStoredProcedureParameter("p_userId", Integer.class, ParameterMode.IN)
				.registerStoredProcedureParameter("p_deActivateUser", Boolean.class, ParameterMode.IN)
				.registerStoredProcedureParameter("p_result", Integer.class, ParameterMode.OUT)
				.setParameter("p_userId", id).setParameter("p_deActivateUser", status);
		query.execute();
		return Integer.parseInt(query.getOutputParameterValue("p_result").toString());
	}

	@Override
	@Transactional(value = "huddilTranscationManager")
	public void updateComments(int userId, String comments) {
		huddilEM.createQuery("UPDATE UserPref u SET u.comments = :comments WHERE u.userId = :userId")
				.setParameter("comments", comments).setParameter("userId", userId).executeUpdate();
	}

	@Override
	public String getComments(int userId) {
		return (String) huddilEM.createQuery("SELECT u.comments FROM UserPref u WHERE u.userId = :userId")
				.setParameter("userId", userId).getSingleResult();
	}

	@Override
	public int addTeam(String sessionId, String teamName) {
		UserSearchResult user = getUserPreference(sessionId);
		if (user == null)
			return -1;
		else if (user.getUserType() != UserType.consumer)
			return -2;
		Object obj = huddilEM.createQuery(
				"SELECT p FROM ParticipantsTeam p JOIN FETCH p.userPref u WHERE u.userId = :userId AND p.name = :name")
				.setParameter("userId", user.getId()).setParameter("name", teamName).getSingleResult();
		if(obj != null)
			return 0;
		ParticipantsTeam p = new ParticipantsTeam(huddilEM.find(UserPref.class, user.getId()), teamName);
		huddilEM.persist(p);
		if (p.getId() != null)
			return p.getId();
		return 0;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<ParticipantsTeam> getTeam(String sessionId) {
		UserSearchResult user = getUserPreference(sessionId);
		List<ParticipantsTeam> participantsTeams = new ArrayList<ParticipantsTeam>();
		if (user == null)
			participantsTeams.add(new ParticipantsTeam(-1));
		else if (user.getUserType() != UserType.consumer)
			participantsTeams.add(new ParticipantsTeam(-2));
		else
			participantsTeams = huddilEM.createNativeQuery(
					"SELECT p.* from huddil.participants_team p JOIN huddil.user_pref u ON p.userId = u.userId WHERE u.userId= :userId",
					"team").setParameter("userId", user.getId()).getResultList();
		return participantsTeams;
	}

	@Override
	public int updateTeam(String sessionId, int id, String name) {
		UserSearchResult pref = getUserPreference(sessionId);
		if (pref == null)
			return -1;
		else if (pref.getUserType() != UserType.consumer)
			return -2;
		else
			return huddilEM.createNativeQuery(
					"UPDATE huddil.participants_team t SET t.name = :name where t.id = :id AND t.userId= :userId")
					.setParameter("id", id).setParameter("userId", pref.getId()).setParameter("name", name)
					.executeUpdate();
	}

	@Override
	public int deleteTeam(String sessionId, int id) {
		UserSearchResult user = getUserPreference(sessionId);
		if (user == null)
			return -1;
		else if (user.getUserType() != UserType.consumer)
			return -2;
		else
			return huddilEM
					.createNativeQuery("DELETE FROM `huddil`.`participants_team` WHERE id = :id AND userId = :userId")
					.setParameter("id", id).setParameter("userId", user.getId()).executeUpdate();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Participants> getParticipants(String sessionId, int id) {
		UserSearchResult user = getUserPreference(sessionId);
		List<Participants> participants = new ArrayList<Participants>();
		if (user == null)
			participants.add(new Participants(-1));
		else if (user.getUserType() != UserType.consumer)
			participants.add(new Participants(-2));
		else
			participants = huddilEM.createNativeQuery(
					"SELECT p.* FROM huddil.participants p JOIN huddil.participants_team t ON p.participantTeamId = t.id WHERE t.id= :id",
					"participants").setParameter("id", id).getResultList();
		return participants;
	}

	@Override
	public int updateParticipants(String sessionId, Participants participants, int id) {
		UserSearchResult user = getUserPreference(sessionId);
		if (user == null)
			return -1;
		else if (user.getUserType() != UserType.consumer)
			return -2;
		else
			return huddilEM.createNativeQuery(
					"UPDATE huddil.participants p SET p.name = :name, p.emailId = :emailId, p.phoneNo = :phoneNo where p.id = :id")
					.setParameter("name", participants.getName()).setParameter("emailId", participants.getEmailId())
					.setParameter("phoneNo", participants.getPhoneNo()).setParameter("id", id).executeUpdate();
	}

	@Override
	public int deleteParticipants(String sessionId, int id) {
		UserSearchResult user = getUserPreference(sessionId);
		if (user == null)
			return -1;
		else if (user.getUserType() != UserType.consumer)
			return -2;
		else
			return huddilEM.createNativeQuery("DELETE FROM `huddil`.`participants` WHERE id = :id")
					.setParameter("id", id).executeUpdate();
	}

	@SuppressWarnings("unchecked")
	@Override
	public int addMeeting(String sessionId, Meeting meeting) throws MessagingException, IOException {
		int result = 0;
		UserSearchResult user = getUserPreference(sessionId);
		if (user == null)
			return -1;
		else if (user.getUserType() != UserType.consumer)
			return -2;
		Object obj = huddilEM.createNativeQuery("SELECT m.id FROM huddil.meeting m WHERE m.bookingId =:bookingId")
				.setParameter("bookingId", meeting.getBooking().getId()).getSingleResult();
		if (obj == null) {
			result = huddilEM.createNativeQuery(
					"INSERT IGNORE INTO huddil.meeting (title, description, participandsId, userId, bookingId)VALUES(:title, :description, :participandsId, :userId, :bookingId)")
					.setParameter("title", meeting.getTitle()).setParameter("description", meeting.getDescription())
					.setParameter("participandsId", meeting.getParticipandsId()).setParameter("userId", user.getId())
					.setParameter("bookingId", meeting.getBooking().getId()).executeUpdate();
			if (result == 1) {
				List<ContactInfo> contactInfos = huddilEM.createNativeQuery(
						"SELECT p.emailId, p.phoneNo FROM huddil.participants p JOIN huddil.participants_team t ON p.participantTeamId = t.id WHERE t.id =:teamId",
						"contactInfo").setParameter("teamId", meeting.getParticipandsId()).getResultList();
				Booking b = huddilEM.find(Booking.class, meeting.getBooking().getId());
				Facility f = huddilEM.find(Facility.class, b.getFacility().getId());
				BookingNotificationTemplate notificationTemplate = new BookingNotificationTemplate(b.getFromTime(),
						b.getToTime(), f, huddilEM.find(UserPref.class, f.getUserPrefBySpUserId()));
				notificationTemplate.setConsumer(user);
				Notifications.sendMeetingInfo(contactInfos, notificationTemplate, meeting.getTitle());
				return 1;
			} else
				return 0;
		}
		return 3;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<MeetingResult> getMeeting(String sessionId, int id) {
		UserSearchResult user = getUserPreference(sessionId);
		List<MeetingResult> meetings = new ArrayList<MeetingResult>();
		if (user == null)
			meetings.add(new MeetingResult(-1));
		else if (user.getUserType() != UserType.consumer)
			meetings.add(new MeetingResult(-2));
		else
			meetings = huddilEM.createNativeQuery(
					"SELECT m.id, m.title, m.description, m.participandsId, m.bookingId, b.fromTime, b.toTime, c.name as cityName, l.name as localityName, lo.name as locationName, lo.address, lo.landmark, u.userId, u.displayName FROM huddil.meeting m "
							+ "JOIN huddil.booking b ON m.bookingId = b.id JOIN huddil.facility f ON f.id = b.facilityId JOIN huddil.city c ON c.name = f.cityName "
							+ "JOIN huddil.locality l ON l.name = f.localityName JOIN huddil.location lo ON lo.id = f.locationId "
							+ "JOIN huddil.user_pref u ON u.userId = f.spUserId WHERE b.id = :id",
					"Meeting_View").setParameter("id", id).getResultList();
		return meetings;
	}

	@Override
	public int addParticipants(String sessionId, List<Participants> participantses, int teamId) {
		UserSearchResult user = getUserPreference(sessionId);
		if (user == null)
			return -1;
		else if (user.getUserType() != UserType.consumer)
			return -2;
		else {
			Object obj = huddilEM.createQuery(
					"SELECT p FROM ParticipantsTeam p JOIN FETCH p.userPref u WHERE u.userId = :userId AND p.id = :id")
					.setParameter("userId", user.getId()).setParameter("id", teamId).getSingleResult();
			if (obj == null)
				return 0;
			ParticipantsTeam pt = (ParticipantsTeam) obj;
			for (Participants p : participantses) {
				p.setParticipantsTeam(pt);
				huddilEM.persist(p);
			}
			return 1;
		}
	}

	@Override
	public int updateMeeting(String sessionId, Meeting meeting, int id) {
		UserSearchResult user = getUserPreference(sessionId);
		if (user == null)
			return -1;
		else if (user.getUserType() != UserType.consumer)
			return -2;
		else
			return huddilEM
					.createNativeQuery(
							"UPDATE huddil.meeting m SET m.title= :title, m.description= :description where m.id= :id")
					.setParameter("title", meeting.getTitle()).setParameter("description", meeting.getDescription())
					.setParameter("id", id).executeUpdate();
	}

	@Override
	public int deleteMeeting(String sessionId, int id) {
		UserSearchResult user = getUserPreference(sessionId);
		if (user == null)
			return -1;
		else if (user.getUserType() != UserType.consumer)
			return -2;
		else
			return huddilEM.createNativeQuery("DELETE m FROM Meeting m WHERE m.id= :id").setParameter("id", id)
					.executeUpdate();
	}

	private UserSearchResult getUserPreference(String sessionId) {
		Object obj = huddilEM.createNativeQuery(
				"SELECT userId AS id, displayName AS name, emailId, mobileNo, mobileNoVerified, CAST(userType AS SIGNED) AS userType FROM user_pref WHERE sessionId = :sessionId",
				"user_pref").setParameter("sessionId", sessionId).getSingleResult();
		if (obj == null)
			return null;
		return (UserSearchResult) obj;
	}
}
