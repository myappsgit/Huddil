package myapps.solutions.huddil.dao;

import java.io.IOException;

import javax.mail.MessagingException;
import javax.mail.internet.AddressException;

import myapps.solutions.huddil.model.CancellationCost;

public interface ICancellationDAO {

	CancellationCost calculateOrCancel(int type, int operation, int bookingId, String sessionId, String reason)
			throws IOException, AddressException, MessagingException;

}
