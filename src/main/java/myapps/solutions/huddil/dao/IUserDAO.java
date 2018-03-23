package myapps.solutions.huddil.dao;

import java.io.IOException;
import java.util.List;

import javax.mail.MessagingException;

import myapps.solutions.huddil.model.Meeting;
import myapps.solutions.huddil.model.MeetingResult;
import myapps.solutions.huddil.model.Participants;
import myapps.solutions.huddil.model.ParticipantsTeam;
import myapps.solutions.huddil.model.UserSearchResult;

public interface IUserDAO {

	List<UserSearchResult> searchUser(String sessionId, String emailId, int userType);
	int deActivteUser(String sessionId, int id, boolean status);
	
	int addTeam(String sessionId, String teamName);
	List<ParticipantsTeam>getTeam(String sessionId);
	int updateTeam(String sessionId, int id, String name);
	int deleteTeam(String sessionId, int id);
	
	//int addParticipants(String sessionId, Participants participants, int id);
	List<Participants> getParticipants(String sessionId, int id);
	int updateParticipants(String sessionId, Participants participants, int id);
	int deleteParticipants(String sessionId, int id);
	int addParticipants(String sessionId, List<Participants> participantses, int teamId);
	
	int addMeeting(String sessionId, Meeting meeting) throws MessagingException, IOException;
	List<MeetingResult> getMeeting(String sessionId, int id);
	int updateMeeting(String sessionId, Meeting meeting, int id);
	int deleteMeeting(String sessionId, int id);
	
	void updateComments(int userId, String comments);
	String getComments(int userId);
}
