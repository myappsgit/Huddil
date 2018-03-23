package myapps.solutions.huddil.controller;

import java.io.IOException;
import java.util.List;

import javax.mail.MessagingException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import myapps.solutions.huddil.model.Meeting;
import myapps.solutions.huddil.model.MeetingResult;
import myapps.solutions.huddil.model.Participants;
import myapps.solutions.huddil.model.ParticipantsTeam;
import myapps.solutions.huddil.model.UserSearchResult;
import myapps.solutions.huddil.service.IUserService;
import myapps.solutions.huddil.utils.ResponseCode;
import myapps.solutions.huddil.utils.UserType;

@RestController
public class UserController {

	@Autowired
	private IUserService userService;

	@ApiOperation(value = "Search for a user", notes = "To search for a particular user based on user type")
	@ApiResponses(value = { @ApiResponse(code = 2601, message = "Search user Successful"),
			@ApiResponse(code = 9996, message = "User is not allowed to perform this action"),
			@ApiResponse(code = 9998, message = "Invalid user type"),
			@ApiResponse(code = 9999, message = "Session invalid/ does not exist"), })
	@RequestMapping(value = "/searchUser/", method = RequestMethod.GET)
	public ResponseEntity<List<UserSearchResult>> searchUser(@RequestParam String sessionId, @RequestParam String user,
			@RequestParam String userType) {
		HttpHeaders headers = new HttpHeaders();
		List<UserSearchResult> users = null;
		int userT = UserType.isValidUserType(userType);
		if (userT == 0)
			headers.set("ResponseCode", ResponseCode.invalidUserType);
		else {
			users = userService.searchUser(sessionId, user, userT);
			if (users.isEmpty())
				headers.set("ResponseCode", ResponseCode.ReadUserSuccessful);
			else if (users.get(0).getId() == -2)
				headers.set("ResponseCode", ResponseCode.accessRestricted);
			else if (users.get(0).getId() == -1)
				headers.set("ResponseCode", ResponseCode.invalidSessionId);
			else
				headers.set("ResponseCode", ResponseCode.ReadUserSuccessful);
		}
		return new ResponseEntity<List<UserSearchResult>>(users, headers, HttpStatus.OK);
	}

	@ApiOperation(value = "Deactive a user", notes = "To deactivate a particular user based on user type")
	@ApiResponses(value = { @ApiResponse(code = 2603, message = "User deactivation successful"),
			@ApiResponse(code = 2604, message = "User deactivation failure"),
			@ApiResponse(code = 2605, message = "User not in activated state"),
			@ApiResponse(code = 2606, message = "User not in deactivated state"),
			@ApiResponse(code = 9996, message = "User is not allowed to perform this action"),
			@ApiResponse(code = 9999, message = "Session invalid/ does not exist") })
	@RequestMapping(value = "/deActivateUser/", method = RequestMethod.PUT)
	public ResponseEntity<Void> deActivateUser(@RequestParam String sessionId, @RequestParam int id,
			@RequestParam boolean status, @RequestParam String comments) {
		HttpHeaders headers = new HttpHeaders();
		int result = userService.deActivteUser(sessionId, id, status, comments);
		if (result == -1)
			headers.set("ResponseCode", ResponseCode.invalidSessionId);
		else if (result == -2)
			headers.set("ResponseCode", ResponseCode.accessRestricted);
		else if (result == 1 || result == 3)
			headers.set("ResponseCode", ResponseCode.UpdateUserSuccessful);
		else if(result == 2)
			headers.set("ResponseCode", ResponseCode.UpdateUserNotInActive);
		else if(result == 4)
			headers.set("ResponseCode", ResponseCode.UpdateUserNotInDeactive);
		else
			headers.set("ResponseCode", ResponseCode.UpdateUserFailure);
		return new ResponseEntity<Void>(headers, HttpStatus.OK);
	}

	@ApiOperation(value = "Get comments", notes = "To get the reason/comments for the user based on userId")
	@RequestMapping(value = "/comments/", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> getComments(@RequestParam int userId) {
		return new ResponseEntity<String>(userService.getComments(userId), HttpStatus.OK);
	}

	// participantsTeam
	@ApiOperation(value = "Add Team ", notes = "To create a team")
	@ApiResponses(value = { @ApiResponse(code = 2711, message = "Participants Team Add Successful"),
			@ApiResponse(code = 2712, message = "Participants Team Add Failure"),
			@ApiResponse(code = 9996, message = "User is not allowed to perform this action"),
			@ApiResponse(code = 9999, message = "Invalid Session") })
	@RequestMapping(value = "/team/", method = RequestMethod.POST)
	public ResponseEntity<ParticipantsTeam> team(@RequestParam String sessionId, @RequestParam String teamName) {
		HttpHeaders headers = new HttpHeaders();
		int team = userService.addTeam(sessionId, teamName);
		if (team == -1)
			headers.set("ResponseCode", ResponseCode.invalidSessionId);
		else if (team == -2)
			headers.set("ResponseCode", ResponseCode.accessRestricted);
		else if (team != 0)
			headers.set("ResponseCode", ResponseCode.ParticipantsTeamAddSuccessful);
		else
			headers.set("ResponseCode", ResponseCode.ParticipantsTeamAddFailure);
		return new ResponseEntity<ParticipantsTeam>(headers, HttpStatus.OK);
	}

	@ApiOperation(value = "Add Team & Participants", notes = "To create and add participants to the team")
	@ApiResponses(value = { @ApiResponse(code = 2712, message = "Participants Team Add Failure"),
			@ApiResponse(code = 2811, message = "Add Participant Successful"),
			@ApiResponse(code = 9996, message = "User is not allowed to perform this action"),
			@ApiResponse(code = 9999, message = "Invalid Session") })
	@RequestMapping(value = "/teamAndParticipant/", method = RequestMethod.POST)
	public ResponseEntity<ParticipantsTeam> team(@RequestParam String sessionId, @RequestParam String teamName,
			@RequestBody List<Participants> participantses) {
		HttpHeaders headers = new HttpHeaders();
		int team = userService.addTeam(sessionId, teamName);
		if (team == -1)
			headers.set("ResponseCode", ResponseCode.invalidSessionId);
		else if (team == -2)
			headers.set("ResponseCode", ResponseCode.accessRestricted);
		else if (team == 0)
			headers.set("ResponseCode", ResponseCode.ParticipantsTeamAddFailure);
		else {
			team = userService.addParticipants(sessionId, participantses, team);
			headers.set("ResponseCode", ResponseCode.AddParticipantSuccessful);
		}
		return new ResponseEntity<ParticipantsTeam>(headers, HttpStatus.OK);
	}

	@ApiOperation(value = "Add Team & Participants", notes = "To create and add participants to the team")
	@ApiResponses(value = { @ApiResponse(code = 2712, message = "Participants Team Add Failure"),
			@ApiResponse(code = 2742, message = "Participants Team Delete Failure"),
			@ApiResponse(code = 2811, message = "Add Participant Successful"),
			@ApiResponse(code = 9996, message = "User is not allowed to perform this action"),
			@ApiResponse(code = 9999, message = "Invalid Session") })
	@RequestMapping(value = "/teamAndParticipantDelAndCre/", method = RequestMethod.POST)
	public ResponseEntity<ParticipantsTeam> team(@RequestParam String sessionId, @RequestParam String teamName,
			@RequestParam int teamId, @RequestBody List<Participants> participantses) {
		HttpHeaders headers = new HttpHeaders();
		int result = userService.deletTeam(sessionId, teamId);
		if (result == -1)
			headers.set("ResponseCode", ResponseCode.invalidSessionId);
		else if (result == -2)
			headers.set("ResponseCode", ResponseCode.accessRestricted);
		else if (result == 0)
			headers.set("ResponseCode", ResponseCode.ParticipantsTeamDeleteFailure);
		else {
			result = userService.addTeam(sessionId, teamName);
			if (result == 0)
				headers.set("ResponseCode", ResponseCode.ParticipantsTeamAddFailure);
			else {
				result = userService.addParticipants(sessionId, participantses, result);
				headers.set("ResponseCode", ResponseCode.AddParticipantSuccessful);
			}
		}
		return new ResponseEntity<ParticipantsTeam>(headers, HttpStatus.OK);
	}

	@ApiOperation(value = "To get all the teams", notes = "To read all the teams of a consumer")
	@ApiResponses(value = { @ApiResponse(code = 2721, message = "Team Read Successful"),
			@ApiResponse(code = 2722, message = "Team Read Failure"),
			@ApiResponse(code = 9996, message = "User is not allowed to perform this action"),
			@ApiResponse(code = 9999, message = "Invalid Session") })
	@RequestMapping(value = "/team/", method = RequestMethod.GET)
	public ResponseEntity<List<ParticipantsTeam>> team(@RequestParam String sessionId) {
		HttpHeaders headers = new HttpHeaders();
		List<ParticipantsTeam> team = userService.getTeam(sessionId);
		if (team.isEmpty())
			headers.set("ResponseCode", ResponseCode.ParticipantsTeamReadSuccessful);
		else if (team.get(0).getId() == -1)
			headers.set("ResponseCode", ResponseCode.invalidSessionId);
		else if (team.get(0).getId() == -2)
			headers.set("ResponseCode", ResponseCode.accessRestricted);
		else
			headers.set("ResponseCode", ResponseCode.ParticipantsTeamReadSuccessful);
		return new ResponseEntity<List<ParticipantsTeam>>(team, headers, HttpStatus.OK);
	}

	@ApiOperation(value = "To update team name", notes = "To update the name of a team by the consumer who created the team")
	@ApiResponses(value = { @ApiResponse(code = 2731, message = "Team Update Successful"),
			@ApiResponse(code = 2732, message = "Team Update Failure"),
			@ApiResponse(code = 9996, message = "User is not allowed to perform this action"),
			@ApiResponse(code = 9999, message = "Invalid Session") })
	@RequestMapping(value = "/team/", method = RequestMethod.PUT)
	public ResponseEntity<Void> team(@RequestParam String sessionId, @RequestParam int id, @RequestParam String name) {
		HttpHeaders headers = new HttpHeaders();
		int result = userService.updateTeam(sessionId, id, name);
		if (result == -1)
			headers.set("ResponseCode", ResponseCode.invalidSessionId);
		else if (result == -2)
			headers.set("ResponseCode", ResponseCode.accessRestricted);
		else if (result == 0)
			headers.set("ResponseCode", ResponseCode.ParticipantsTeamUpdateFailure);
		else
			headers.set("ResponseCode", ResponseCode.ParticipantsTeamUpdateSuccessful);
		return new ResponseEntity<Void>(headers, HttpStatus.OK);
	}

	@ApiOperation(value = "To delete a team", notes = "To delete a team by the consumer who created it")
	@ApiResponses(value = { @ApiResponse(code = 2741, message = "Delete Team Successful"),
			@ApiResponse(code = 2742, message = "Delete Team Failure"),
			@ApiResponse(code = 9996, message = "User is not allowed to perform this action"),
			@ApiResponse(code = 9999, message = "Invalid Session") })
	@RequestMapping(value = "/team/", method = RequestMethod.DELETE)
	public ResponseEntity<Void> team(@RequestParam String sessionId, @RequestParam int id) {
		HttpHeaders headers = new HttpHeaders();
		int result = userService.deletTeam(sessionId, id);
		if (result == -1)
			headers.set("ResponseCode", ResponseCode.invalidSessionId);
		else if (result == -2)
			headers.set("ResponseCode", ResponseCode.accessRestricted);
		else if (result == 0)
			headers.set("ResponseCode", ResponseCode.ParticipantsTeamDeleteFailure);
		else
			headers.set("ResponseCode", ResponseCode.ParticipantsTeamDeleteSuccessful);
		return new ResponseEntity<Void>(headers, HttpStatus.OK);
	}

	@ApiOperation(value = "To add participants to a team", notes = "To add aprticipants to a team by consumer")
	@ApiResponses(value = { @ApiResponse(code = 2812, message = "Add PArticipant Failure"),
			@ApiResponse(code = 2813, message = "Add Participant Partially Successful"),
			@ApiResponse(code = 2814, message = "Add Participant Denied"),
			@ApiResponse(code = 9996, message = "User is not allowed to perform this action"),
			@ApiResponse(code = 9999, message = "Invalid Session") })
	@RequestMapping(value = "/participants/", method = RequestMethod.POST)
	public ResponseEntity<Void> participants(@RequestParam String sessionId, @RequestParam int teamId,
			@RequestBody List<Participants> participantses) {
		HttpHeaders headers = new HttpHeaders();
		int part = userService.addParticipants(sessionId, participantses, teamId);
		if (part == -1)
			headers.set("ResponseCode", ResponseCode.invalidSessionId);
		else if (part == -2)
			headers.set("ResponseCode", ResponseCode.accessRestricted);
		else if (part == 0)
			headers.set("ResponseCode", ResponseCode.AddParticipantsDenied);
		else if (part == 1)
			headers.set("ResponseCode", ResponseCode.AddParticipantsPartiallySuccessful);
		else
			headers.set("ResponseCode", ResponseCode.AddParticipantsFailure);
		return new ResponseEntity<Void>(headers, HttpStatus.OK);
	}

	@ApiOperation(value = "To get participants in a team", notes = "To get prticipants of a team by consumer")
	@ApiResponses(value = { @ApiResponse(code = 2821, message = "Read Participants Successful"),
			@ApiResponse(code = 2822, message = "Read Participants Failure"),
			@ApiResponse(code = 9996, message = "User is not allowed to perform this action"),
			@ApiResponse(code = 9999, message = "Invalid Session") })
	@RequestMapping(value = "/participants/", method = RequestMethod.GET)
	public ResponseEntity<List<Participants>> participants(@RequestParam String sessionId, @RequestParam int id) {
		HttpHeaders headers = new HttpHeaders();
		List<Participants> participants = userService.getParticipants(sessionId, id);
		if (participants.isEmpty())
			headers.set("ResponseCode", ResponseCode.ReadParticipantsSuccessful);
		else if (participants.get(0).getId() == -1)
			headers.set("ResponseCode", ResponseCode.invalidSessionId);
		else if (participants.get(0).getId() == -2)
			headers.set("ResponseCode", ResponseCode.accessRestricted);
		else
			headers.set("ResponseCode", ResponseCode.ReadParticipantsSuccessful);
		return new ResponseEntity<List<Participants>>(participants, headers, HttpStatus.OK);
	}

	@ApiOperation(value = "To get participants in a team", notes = "To get prticipants of a team by consumer")
	@ApiResponses(value = { @ApiResponse(code = 2831, message = "Update Participants Successful"),
			@ApiResponse(code = 2832, message = "Update Participants Failure"),
			@ApiResponse(code = 9996, message = "User is not allowed to perform this action"),
			@ApiResponse(code = 9999, message = "Invalid Session") })
	@RequestMapping(value = "/updateParticipant/", method = RequestMethod.PUT)
	public ResponseEntity<Void> participants(@RequestBody Participants participants, @RequestParam String sessionId,
			@RequestParam int id) {
		HttpHeaders headers = new HttpHeaders();
		int result = userService.updateParticipants(sessionId, participants, id);
		if (result == -1)
			headers.set("ResponseCode", ResponseCode.invalidSessionId);
		else if (result == -2)
			headers.set("ResponseCode", ResponseCode.accessRestricted);
		else if (result == 0)
			headers.set("ResponseCode", ResponseCode.UpdateParticipantsFailure);
		else
			headers.set("ResponseCode", ResponseCode.UpdateParticipantsSuccessful);
		return new ResponseEntity<Void>(headers, HttpStatus.OK);
	}

	@ApiOperation(value = "To delete a participant in a team", notes = "To delete participant of a team by consumer")
	@ApiResponses(value = { @ApiResponse(code = 2841, message = "Delete Participants Successful"),
			@ApiResponse(code = 2842, message = "Delete Participants Failure"),
			@ApiResponse(code = 9996, message = "User is not allowed to perform this action"),
			@ApiResponse(code = 9999, message = "Invalid Session") })
	@RequestMapping(value = "/participant/", method = RequestMethod.DELETE)
	public ResponseEntity<Void> participant(@RequestParam String sessionId, @RequestParam int id) {
		HttpHeaders headers = new HttpHeaders();
		int result = userService.deleteParticipants(sessionId, id);
		if (result == -1)
			headers.set("ResponseCode", ResponseCode.invalidSessionId);
		else if (result == -2)
			headers.set("ResponseCode", ResponseCode.accessRestricted);
		else if (result == 0)
			headers.set("ResponseCode", ResponseCode.DeleteParticipantsFailure);
		else
			headers.set("ResponseCode", ResponseCode.DeleteParticipantsSuccessful);
		return new ResponseEntity<Void>(headers, HttpStatus.OK);
	}

	// meeting
	@ApiOperation(value = "To create meeting", notes = "To create meeting by consumer who has made a Successful booking")
	@ApiResponses(value = { @ApiResponse(code = 2911, message = "Add Meeting Successful"),
			@ApiResponse(code = 2912, message = "Add Meeting Failure"),
			@ApiResponse(code = 2913, message = "Meeting already added for the booking"),
			@ApiResponse(code = 9996, message = "User is not allowed to perform this action"),
			@ApiResponse(code = 9999, message = "Invalid Session") })
	@RequestMapping(value = "/meeting/", method = RequestMethod.POST)
	public ResponseEntity<Meeting> meeting(@RequestBody Meeting meeting, @RequestParam String sessionId)
			throws MessagingException, IOException {
		HttpHeaders headers = new HttpHeaders();
		int meet = userService.addMeeting(sessionId, meeting);
		if (meet == -1)
			headers.set("ResponseCode", ResponseCode.invalidSessionId);
		else if (meet == -2)
			headers.set("ResponseCode", ResponseCode.accessRestricted);
		else if (meet == 1)
			headers.set("ResponseCode", ResponseCode.AddMeetingSuccessful);
		else if (meet == 0)
			headers.set("ResponseCode", ResponseCode.AddMeetingFailure);
		else
			headers.set("ResponseCode", ResponseCode.MeetingAlreadAdded);
		return new ResponseEntity<Meeting>(headers, HttpStatus.OK);
	}

	@ApiOperation(value = "To get meeting details by consumer", notes = "To get meeting details by consumer who created the meeting")
	@ApiResponses(value = { @ApiResponse(code = 2921, message = "Read Meeting Successful"),
			@ApiResponse(code = 2922, message = "Read Meeting Failure"),
			@ApiResponse(code = 9996, message = "User is not allowed to perform this action"),
			@ApiResponse(code = 9999, message = "Invalid Session") })
	@RequestMapping(value = "/meeting/", method = RequestMethod.GET)
	public ResponseEntity<List<MeetingResult>> meetings(@RequestParam String sessionId, @RequestParam int id) {
		HttpHeaders headers = new HttpHeaders();
		List<MeetingResult> meet = userService.getMeeting(sessionId, id);
		if (meet.isEmpty())
			headers.set("ResponseCode", ResponseCode.MeetingReadSuccessful);
		else if (meet.get(0).getId() == -1)
			headers.set("ResponseCode", ResponseCode.invalidSessionId);
		else if (meet.get(0).getId() == -2)
			headers.set("ResponseCode", ResponseCode.accessRestricted);
		else
			headers.set("ResponseCode", ResponseCode.MeetingReadSuccessful);
		return new ResponseEntity<List<MeetingResult>>(meet, headers, HttpStatus.OK);
	}

	@ApiOperation(value = "To Update Meeting Details", notes = "To update meeting details by consumer who created the meeting")
	@ApiResponses(value = { @ApiResponse(code = 2931, message = "Update Meeting Successful"),
			@ApiResponse(code = 2932, message = "Update Meeting Failure"),
			@ApiResponse(code = 9996, message = "User is not allowed to perform this action"),
			@ApiResponse(code = 9999, message = "Invalid Session") })
	@RequestMapping(value = "/meeting/", method = RequestMethod.PUT)
	public ResponseEntity<Void> meeting(@RequestParam String sessionId, @RequestBody Meeting meeting,
			@RequestParam int id) {
		HttpHeaders headers = new HttpHeaders();
		int result = userService.updateMeeting(sessionId, meeting, id);
		if (result == -1)
			headers.set("ResponseCode", ResponseCode.invalidSessionId);
		else if (result == -2)
			headers.set("ResponseCode", ResponseCode.accessRestricted);
		else if (result == 0)
			headers.set("ResponseCode", ResponseCode.UpdateMeetingFailure);
		else
			headers.set("ResponseCode", ResponseCode.UpdateMeetingSuccessful);
		return new ResponseEntity<Void>(headers, HttpStatus.OK);
	}

	@ApiOperation(value = "To Delete Meeting", notes = "To delete meeting details by consumer who created the meeting")
	@ApiResponses(value = { @ApiResponse(code = 2941, message = "Delete Meeting Successful"),
			@ApiResponse(code = 2942, message = "Delete Meeting Failure"),
			@ApiResponse(code = 9996, message = "User is not allowed to perform this action"),
			@ApiResponse(code = 9999, message = "Invalid Session") })
	@RequestMapping(value = "/meeting/", method = RequestMethod.DELETE)
	public ResponseEntity<Void> meeting(@RequestParam String sessionId, @RequestParam int id) {
		HttpHeaders headers = new HttpHeaders();
		int result = userService.deleteMeeting(sessionId, id);
		if (result == -1)
			headers.set("ResponseCode", ResponseCode.invalidSessionId);
		else if (result == -2)
			headers.set("ResponseCode", ResponseCode.accessRestricted);
		else if (result == 0)
			headers.set("ResponseCode", ResponseCode.DeleteMeetingFailure);
		else
			headers.set("ResponseCode", ResponseCode.DeleteMeetingSuccessful);
		return new ResponseEntity<Void>(headers, HttpStatus.OK);
	}

}
