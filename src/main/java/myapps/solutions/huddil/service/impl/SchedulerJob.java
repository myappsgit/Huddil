package myapps.solutions.huddil.service.impl;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.StringJoiner;

import javax.mail.MessagingException;

import org.springframework.stereotype.Component;

import myapps.solutions.huddil.config.AutowiringSpringBeanJobFactory;
import myapps.solutions.huddil.dao.IBookingDAO;
import myapps.solutions.huddil.model.JobData;
import myapps.solutions.huddil.utils.Notifications;

@Component
public class SchedulerJob implements Runnable {

	private IBookingDAO bookingDao;

	private static boolean todayFirstTime = true;

	private CustomScheduler scheduler = CustomScheduler.getInstance();

	private long getTimeDiff(Date date, boolean minute) {
		if (minute)
			return ((date.getTime() - new Date().getTime()) / 60000);
		else
			return ((date.getTime() - new Date().getTime()) / 3600000);
	}

	@Override
	public void run() {
		System.out.println("Job started");
		try {
			bookingDao = AutowiringSpringBeanJobFactory.getApplicationContext().getBean(IBookingDAO.class);
			if (todayFirstTime) {
				bookingDao.leastCost(true);
				bookingDao.deleteOffersaddCommission();
				todayFirstTime = false;
			}
			StringJoiner ids = new StringJoiner(", ");
			List<JobData> jobData = bookingDao.getNextJob(true);
			if (!jobData.isEmpty()) {
				System.out.println("sending notification");
				for (JobData data : jobData) {
					if (data.isStartTime()) {
						Notifications.sendMeetingReminder(data, false);
						Notifications.sendMeetingReminder(data, true);
					} else {
						Notifications.sendMeetinEndReminder(data.getcName(), data.getcMobileNo(), data.iscMobileNoVerified());
						ids.add(Integer.toString(data.getBookingId()));
					}
				}
			}

			bookingDao.moveCompleteBooking(ids.toString());
			jobData = bookingDao.getNextJob(false);
			scheduler.shutdown();
			if (!jobData.isEmpty()) {
				long min = getTimeDiff(jobData.get(0).getDate(), true);
				System.out.println("scheduleing for next time" + min);
				scheduler.shutdown();
				if (jobData.get(0).isStartTime())
					scheduler.start(min - 120);
				else
					scheduler.start(min - 5);
			} else {
				System.out.println("scheduleing for next time tomorrow");
				scheduler.shutdown();
				todayFirstTime = true;
				Calendar c = Calendar.getInstance();
				c.setTime(new Date());
				c.setLenient(true);
				c.set(Calendar.DATE, c.get(Calendar.DATE) + 1);
				c.set(Calendar.HOUR_OF_DAY, 00);
				c.set(Calendar.MINUTE, 05);
				c.set(Calendar.SECOND, 00);
				c.set(Calendar.MILLISECOND, 0);
				scheduler.start(getTimeDiff(c.getTime(), true));
			}
		} catch (IOException | MessagingException e) {
			e.printStackTrace();
		}
	}
}
