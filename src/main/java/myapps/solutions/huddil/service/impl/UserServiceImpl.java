package myapps.solutions.huddil.service.impl;

import java.io.IOException;
import java.util.List;

import javax.mail.MessagingException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import myapps.solutions.huddil.dao.IFacilityDAO;
import myapps.solutions.huddil.dao.IUserDAO;
import myapps.solutions.huddil.model.Meeting;
import myapps.solutions.huddil.model.MeetingResult;
import myapps.solutions.huddil.model.Participants;
import myapps.solutions.huddil.model.ParticipantsTeam;
import myapps.solutions.huddil.model.UserSearchResult;
import myapps.solutions.huddil.service.IUserService;

@Service
public class UserServiceImpl implements IUserService {

	@Autowired
	private IUserDAO userDao;

	@Autowired
	private IFacilityDAO facilityDao;

	@Override
	public List<UserSearchResult> searchUser(String sessionId, String emailId, int userType) {
		return userDao.searchUser(sessionId, emailId, userType);
	}

	@Override
	public int deActivteUser(String sessionId, int id, boolean status, String comments) {
		int result = userDao.deActivteUser(sessionId, id, status);
		if (result == 1 || result == 3) {
			userDao.updateComments(id, comments);
			return facilityDao.disableAllFacilityOfSP(sessionId, id, status, comments);
		}
		return result;
	}
	
	@Override
	public String getComments(int userId) {
		return userDao.getComments(userId);
	}

	@Override
	public int addTeam(String sessionId, String teamName) {
		return userDao.addTeam(sessionId, teamName);
	}
	
	@Override
	public List<ParticipantsTeam> getTeam(String sessionId) {
		return userDao.getTeam(sessionId);
	}
	
	@Override
	public int updateTeam(String sessionId, int id, String name) {
		return userDao.updateTeam(sessionId, id, name);
	}

	@Override
	public int deletTeam(String sessionId, int id) {
		return userDao.deleteTeam(sessionId, id);
	}
/*
	@Override
	public int addParticipants(String sessionId, Participants participants, int id) {
		return userDao.addParticipants(sessionId, participants, id);
	}
*/
	@Override
	public List<Participants> getParticipants(String sessionId, int id) {
		return userDao.getParticipants(sessionId, id);
	}

	@Override
	public int updateParticipants(String sessionId,  Participants participants, int id) {
		return userDao.updateParticipants(sessionId, participants, id);
	}
	
	@Override
	public int deleteParticipants(String sessionId, int id) {
		return userDao.deleteParticipants(sessionId, id);
	}

	@Override
	public int addMeeting(String sessionId, Meeting meeting) throws MessagingException, IOException {
		return userDao.addMeeting(sessionId, meeting);
	}

	@Override
	public List<MeetingResult> getMeeting(String sessionId, int id) {
		return userDao.getMeeting(sessionId, id);
	}

	@Override
	public int addParticipants(String sessionId, List<Participants> participantses, int teamId) {
		return userDao.addParticipants(sessionId, participantses, teamId);
	}

	@Override
	public int updateMeeting(String sessionId, Meeting meeting, int id) {
		return userDao.updateMeeting(sessionId, meeting, id);
	}

	@Override
	public int deleteMeeting(String sessionId, int id) {
		return userDao.deleteMeeting(sessionId, id);
	}
}
