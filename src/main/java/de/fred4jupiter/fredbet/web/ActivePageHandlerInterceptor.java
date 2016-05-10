package de.fred4jupiter.fredbet.web;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import de.fred4jupiter.fredbet.domain.Group;

/**
 * Adds css class to html tags for showing the active tab/link.
 * 
 * @author michael
 *
 */
public class ActivePageHandlerInterceptor implements HandlerInterceptor {

	private static final String CSS_ACTIVE = "active";

	private static final String PAGE_STATE_REFIX = "pageState_";

	private static final Logger LOG = LoggerFactory.getLogger(ActivePageHandlerInterceptor.class);

	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView)
			throws Exception {
		final String requestURI = request.getRequestURI();
		LOG.debug("requestURI: " + requestURI);
		if (requestURI.contains("error")) {
			return;
		}

		if (requestURI.contains("user") || requestURI.contains("buildinfo") || requestURI.contains("administration")) {
			modelAndView.addObject(PAGE_STATE_REFIX + "administrationMenu", CSS_ACTIVE);
		}

		final int numberOfSlashes = StringUtils.countMatches(requestURI, "/");
		if (numberOfSlashes == 1) {
			String page = StringUtils.substring(requestURI, 1);
			modelAndView.addObject(PAGE_STATE_REFIX + page, CSS_ACTIVE);
			return;
		}

		if (requestURI.contains("userprofile")) {
			modelAndView.addObject(PAGE_STATE_REFIX + "userprofile", CSS_ACTIVE);
		}

		if (requestURI.contains("/bet/")) {
			modelAndView.addObject(PAGE_STATE_REFIX + "betting", CSS_ACTIVE);
		}

		if (requestURI.contains("info")) {
			modelAndView.addObject(PAGE_STATE_REFIX + "info", CSS_ACTIVE);
		}

		if (numberOfSlashes == 2) {
			String page = StringUtils.substring(requestURI, 1);
			page = StringUtils.replace(page, "/", "_");
			modelAndView.addObject(PAGE_STATE_REFIX + page, CSS_ACTIVE);
			return;
		}

		if (requestURI.contains("group")) {
			if (containsMainGroups(requestURI) || containsFinalGroups(requestURI)) {
				modelAndView.addObject(PAGE_STATE_REFIX + "group", CSS_ACTIVE);
			}

			String page = StringUtils.substringAfter(requestURI, "group/");
			modelAndView.addObject(PAGE_STATE_REFIX + page, CSS_ACTIVE);
		}
	}

	private boolean containsMainGroups(String requestURI) {
		for (Group group : Group.getMainGroups()) {
			if (requestURI.contains(group.getName())) {
				return true;
			}
		}
		return false;
	}

	private boolean containsFinalGroups(String requestURI) {
		for (Group group : Group.getFinalGroups()) {
			if (requestURI.contains(group.getName())) {
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		return true;
	}

	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
	}

}
