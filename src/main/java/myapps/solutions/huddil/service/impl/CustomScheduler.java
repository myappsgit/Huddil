package myapps.solutions.huddil.service.impl;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class CustomScheduler {

	private static ScheduledExecutorService service = Executors.newScheduledThreadPool(1);
	private static ScheduledFuture<?> scheduler;

	private static CustomScheduler customScheduler;
	private static Date scheduledAt = new Date();

	public static CustomScheduler getInstance() {
		if (customScheduler == null)
			customScheduler = new CustomScheduler();
		return customScheduler;
	}

	public void start(long delay) throws IOException {
		Calendar c = Calendar.getInstance();
		c.setTime(new Date());
		c.setLenient(true);
		c.set(Calendar.MINUTE, c.get(Calendar.MINUTE) + (int) delay);
		if (scheduledAt.after(c.getTime()))
			shutdown();
		if (scheduler == null || scheduler.isCancelled() || scheduler.isDone()) {
			System.out.println("Scheduled after " + delay + "min");
			scheduler = service.schedule(new SchedulerJob(), delay, TimeUnit.MINUTES);
			scheduledAt = c.getTime();
		}
	}

	public void shutdown() {
		if (scheduler != null)
			scheduler.cancel(false);
	}
}
