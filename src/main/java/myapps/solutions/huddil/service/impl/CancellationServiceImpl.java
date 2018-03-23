package myapps.solutions.huddil.service.impl;

import java.io.IOException;

import javax.mail.MessagingException;
import javax.mail.internet.AddressException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import myapps.solutions.huddil.dao.ICancellationDAO;
import myapps.solutions.huddil.model.CancellationCost;
import myapps.solutions.huddil.service.ICancellationService;
import myapps.solutions.huddil.utils.ResponseCode;

@Service
public class CancellationServiceImpl implements ICancellationService {
	
	@Autowired
	private ICancellationDAO cancellationDao;

	@Override
	public CancellationCost calculateOrCancel(int type, int operation, int bookingId, String sessionId, String reason)
			throws IOException, AddressException, MessagingException {
		CancellationCost cancellationCost = cancellationDao.calculateOrCancel(type, operation, bookingId, sessionId, reason);
		if(cancellationCost.getResponseCode() == ResponseCode.CancelBookingFailed)
			cancellationDao.calculateOrCancel(4, operation, bookingId, sessionId, reason);
		return cancellationCost;
	}
}
