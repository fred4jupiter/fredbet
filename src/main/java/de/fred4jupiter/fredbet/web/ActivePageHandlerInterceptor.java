package de.fred4jupiter.fredbet.web;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

public class ActivePageHandlerInterceptor implements HandlerInterceptor {

	private static final String CSS_ACTIVE = "active";

	private static final String PAGE_STATE_REFIX = "pageState_";
	
	private static final Logger LOG = LoggerFactory.getLogger(ActivePageHandlerInterceptor.class);

	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView)
			throws Exception {
		final String requestURI = request.getRequestURI();
		LOG.debug("requestURI: " + requestURI);

		final int numberOfSlashes = StringUtils.countMatches(requestURI, "/");
		if (numberOfSlashes == 1) {
			String page = StringUtils.substring(requestURI, 1);
			modelAndView.addObject(PAGE_STATE_REFIX + page, CSS_ACTIVE);
			return;
		}

		if (numberOfSlashes == 2) {
			String page = StringUtils.substring(requestURI, 1);
			page = StringUtils.replace(page, "/", "_");
			modelAndView.addObject(PAGE_STATE_REFIX + page, CSS_ACTIVE);
			return;
		}
		
		if (requestURI.contains("group")) {
			String page = StringUtils.substringAfter(requestURI, "group/");
			modelAndView.addObject(PAGE_STATE_REFIX + page, CSS_ACTIVE);
		}
	}

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		return true;
	}

	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
	}

}
