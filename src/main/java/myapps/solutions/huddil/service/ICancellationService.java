package myapps.solutions.huddil.service;

import java.io.IOException;

import javax.mail.MessagingException;
import javax.mail.internet.AddressException;

import myapps.solutions.huddil.model.CancellationCost;

public interface ICancellationService {
	
	CancellationCost calculateOrCancel(int type, int operation, int boookingId, String sessionId, String reason)
			throws IOException, AddressException, MessagingException;
}
