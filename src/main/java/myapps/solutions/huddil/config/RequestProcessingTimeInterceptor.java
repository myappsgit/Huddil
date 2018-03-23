package myapps.solutions.huddil.config;

import java.text.SimpleDateFormat;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

@Component
public class RequestProcessingTimeInterceptor extends HandlerInterceptorAdapter {

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		long startTime = System.currentTimeMillis();
		System.out.println("Request URL::" + request.getRequestURL().toString() + ":: Start Time = "
				+ formatDate(System.currentTimeMillis()));
		request.setAttribute("startTime", startTime);
		// if returned false, we need to make sure 'response' is sent
		return true;
	}

	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
			ModelAndView modelAndView) throws Exception {
		// System.out.println("Request URL::" +
		// request.getRequestURL().toString() + " Sent to Handler :: Current
		// Time = " + formatDate(System.currentTimeMillis()));
		// we can add attributes in the modelAndView and use that in the view
		// page
	}

	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
			throws Exception {
		long startTime = (Long) request.getAttribute("startTime");
		System.out.println("Request URL::" + request.getRequestURL().toString() + ":: End Time = "
				+ formatDate(System.currentTimeMillis()));
		System.out.println("Request URL::" + request.getRequestURL().toString() + ":: Time Taken = "
				+ (System.currentTimeMillis() - startTime));
	}

	private static String formatDate(long date) {
		SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm");
		return formatter.format(date);
	}
}