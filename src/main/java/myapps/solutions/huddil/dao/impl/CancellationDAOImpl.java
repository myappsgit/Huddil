package myapps.solutions.huddil.dao.impl;

import java.io.IOException;
import java.util.Date;

import javax.mail.MessagingException;
import javax.mail.internet.AddressException;
import javax.persistence.EntityManager;
import javax.persistence.ParameterMode;
import javax.persistence.PersistenceContext;
import javax.persistence.StoredProcedureQuery;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import myapps.payment.service.InstaMojoService;
import myapps.payment.service.model.InstaMojoRefund;
import myapps.solutions.huddil.dao.ICancellationDAO;
import myapps.solutions.huddil.model.CancellationCost;
import myapps.solutions.huddil.model.CancellationNotificationTemplate;
import myapps.solutions.huddil.model.UserSearchResult;
import myapps.solutions.huddil.utils.Notifications;
import myapps.solutions.huddil.utils.ResponseCode;
import myapps.solutions.huddil.utils.UserType;

@Transactional(value = "huddilTranscationManager")
@Repository
public class CancellationDAOImpl implements ICancellationDAO {

	@PersistenceContext(unitName = "huddil")
	private EntityManager huddilEM;

	@Override
	public CancellationCost calculateOrCancel(int type, int operation, int bookingId, String sessionId, String reason)
			throws IOException, AddressException, MessagingException {

		StoredProcedureQuery query = huddilEM.createStoredProcedureQuery("performCancellation", "cancellationMailData")
				.registerStoredProcedureParameter("p_type", Integer.class, ParameterMode.IN)
				.registerStoredProcedureParameter("p_bookingId", Integer.class, ParameterMode.IN)
				.registerStoredProcedureParameter("p_facilityId", Integer.class, ParameterMode.IN)
				.registerStoredProcedureParameter("p_sessionId", String.class, ParameterMode.IN)
				.registerStoredProcedureParameter("p_reason", String.class, ParameterMode.IN)
				.registerStoredProcedureParameter("p_operation", Integer.class, ParameterMode.IN)
				.registerStoredProcedureParameter("p_fromDateTime", Date.class, ParameterMode.IN)
				.registerStoredProcedureParameter("p_toDateTime", Date.class, ParameterMode.IN)
				.registerStoredProcedureParameter("p_cancel", Integer.class, ParameterMode.OUT)
				.registerStoredProcedureParameter("p_result", Integer.class, ParameterMode.OUT)
				.registerStoredProcedureParameter("p_refund", Double.class, ParameterMode.OUT)
				.registerStoredProcedureParameter("p_cancellationPrice", Double.class, ParameterMode.OUT)
				.registerStoredProcedureParameter("p_totalPrice", Double.class, ParameterMode.OUT)
				.registerStoredProcedureParameter("p_count", Integer.class, ParameterMode.OUT)
				.setParameter("p_type", type).setParameter("p_bookingId", bookingId).setParameter("p_facilityId", 0)
				.setParameter("p_sessionId", sessionId).setParameter("p_reason", reason)
				.setParameter("p_operation", operation).setParameter("p_fromDateTime", new Date())
				.setParameter("p_toDateTime", new Date());
		query.execute();
		int result = Integer.parseInt(query.getOutputParameterValue("p_result").toString());
		Double refundAmt;
		CancellationCost cancellationCost;
		CancellationNotificationTemplate notificationTemplate;
		UserSearchResult user;
		switch (result) {
		case 1:
			return new CancellationCost(ResponseCode.invalidSessionId);
		case 2:
			return new CancellationCost(ResponseCode.accessRestricted);
		case 3:
			return new CancellationCost(ResponseCode.CancelBookingInvalidBookingId);
		case 5:
			return new CancellationCost(ResponseCode.invalidData);
		case 6:
			return new CancellationCost(ResponseCode.CancelBookingPaymentInProgress);
		case 7:
			return new CancellationCost(ResponseCode.CancelBookingMeetingInProgress);
		case 8:
			return new CancellationCost(ResponseCode.CancelBookingInvalidStatus);
		case 9:
			return new CancellationCost(ResponseCode.CancelBookingNotAuth);
		case 10:
			return new CancellationCost(ResponseCode.CancelBookingOffline);
		case 11:
			notificationTemplate = (CancellationNotificationTemplate) query.getSingleResult();
			notificationTemplate.setRefundAmt(0.0);
			user = getUserPreference(sessionId);
			if (user.getUserType() == UserType.consumer)
				Notifications.sendCancellationDetails(notificationTemplate, true);
			else
				Notifications.sendCancellationDetails(notificationTemplate, false);
			return new CancellationCost(ResponseCode.CancelBookingOfflineCancelled);
		case 12:
			refundAmt = Double.parseDouble(query.getOutputParameterValue("p_refund").toString());
			cancellationCost = new CancellationCost(ResponseCode.CancelBookingOnline, 0, refundAmt,
					Double.parseDouble(query.getOutputParameterValue("p_cancellationPrice").toString()),
					Double.parseDouble(query.getOutputParameterValue("p_totalPrice").toString()));
			return cancellationCost;
		case 13:
			refundAmt = Double.parseDouble(query.getOutputParameterValue("p_refund").toString());
			cancellationCost = new CancellationCost(ResponseCode.CancelBookingOnline, 0, refundAmt,
					Double.parseDouble(query.getOutputParameterValue("p_cancellationPrice").toString()),
					Double.parseDouble(query.getOutputParameterValue("p_totalPrice").toString()));
			notificationTemplate = (CancellationNotificationTemplate) query.getSingleResult();
			InstaMojoRefund refund = InstaMojoService.getInstance().createRefund(notificationTemplate.getPaymentId(),
					cancellationCost.getRefundAmt(), reason, "huddil");
			if (refund != null) {
				cancellationCost.setResponseCode(ResponseCode.CancelBookingOnlineCancelled);
				cancellationCost.setRefundId(refund.getId());
				huddilEM.createNativeQuery("UPDATE cancellation SET refundId = :refundId WHERE paymentId = :paymentId")
						.setParameter("refundId", refund.getId()).setParameter("paymentId", refund.getPaymentId())
						.executeUpdate();
				user = getUserPreference(sessionId);
				if (user.getUserType() == UserType.consumer)
					Notifications.sendCancellationDetails(notificationTemplate, true);
				else
					Notifications.sendCancellationDetails(notificationTemplate, false);
			} else
				cancellationCost.setResponseCode(ResponseCode.CancelBookingFailed);
			return cancellationCost;
		}
		return null;
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
