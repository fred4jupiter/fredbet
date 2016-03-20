package de.fred4jupiter.fredbet.web;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

public class ExecutionTimeInterceptor extends HandlerInterceptorAdapter {

	private static final Logger LOG = LoggerFactory.getLogger(ExecutionTimeInterceptor.class);

	private static final String START_TIME_ATTRIBUTE_NAME = "startTime";

	// before the actual handler will be executed
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		long startTime = System.currentTimeMillis();
		request.setAttribute(START_TIME_ATTRIBUTE_NAME, startTime);
		return true;
	}

	// after the handler is executed
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView)
			throws Exception {
		long startTime = (Long) request.getAttribute(START_TIME_ATTRIBUTE_NAME);
		long endTime = System.currentTimeMillis();
		long executeTime = endTime - startTime;

		LOG.debug("executeTime: {}", executeTime);
	}
}
