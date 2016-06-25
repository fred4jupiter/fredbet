package de.fred4jupiter.fredbet.web;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
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

	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView)
			throws Exception {
		final String requestURI = request.getRequestURI();
		if (requestURI.contains("error")) {
			return;
		}

		final String page = StringUtils.substring(requestURI, 1);
		final String replaced = StringUtils.replace(page, "/", "_");
		modelAndView.addObject(PAGE_STATE_REFIX + replaced, CSS_ACTIVE);

		// add top navigation items with submenus

		// Tippen
		if (requestURI.contains("/bet/")) {
			modelAndView.addObject(PAGE_STATE_REFIX + "betting", CSS_ACTIVE);
		}

		// Gruppen
		if (requestURI.contains("group")) {
			if (containsMainGroups(requestURI) || containsFinalGroups(requestURI)) {
				modelAndView.addObject(PAGE_STATE_REFIX + "group", CSS_ACTIVE);
			}
		}

		// Administration
		if (requestURI.contains("user") || requestURI.contains("buildinfo") || requestURI.contains("administration")) {
			modelAndView.addObject(PAGE_STATE_REFIX + "administrationMenu", CSS_ACTIVE);
		}

		// Infos
		if (requestURI.contains("info/")) {
			modelAndView.addObject(PAGE_STATE_REFIX + "info", CSS_ACTIVE);
		}

		// user profile
		if (requestURI.contains("profile")) {
			modelAndView.addObject(PAGE_STATE_REFIX + "profile", CSS_ACTIVE);
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
