package myapps.solutions.huddil.service;

import java.io.IOException;
import java.util.List;

import javax.mail.MessagingException;

import myapps.solutions.huddil.model.Meeting;
import myapps.solutions.huddil.model.MeetingResult;
import myapps.solutions.huddil.model.Participants;
import myapps.solutions.huddil.model.ParticipantsTeam;
import myapps.solutions.huddil.model.UserSearchResult;

public interface IUserService {
	
	List<UserSearchResult> searchUser(String sessionId, String emailId, int userType);
	int deActivteUser(String sessionId, int id, boolean status, String comments);
	String getComments(int userId);
	
	int addTeam(String sessionId, String teamName);
	List<ParticipantsTeam>getTeam(String sessionId);
	int updateTeam(String sessionId, int id, String name);
	int deletTeam(String sessionId, int id);
	
	//int addParticipants(String sessionId, Participants participants, int id);
	List<Participants> getParticipants(String sessionId, int id);
	int updateParticipants(String sessionId, Participants participants, int id);
	int deleteParticipants(String sessionId, int id);
	int addParticipants(String sessionId, List<Participants> participantses, int teamId);
	
	int addMeeting(String sessionId, Meeting meeting) throws MessagingException, IOException;
	List<MeetingResult> getMeeting(String sessionId, int id);
	int updateMeeting(String sessionId, Meeting meeting, int id);
	int deleteMeeting(String sessionId, int id);
}
