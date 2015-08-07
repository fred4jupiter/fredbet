package de.fred4jupiter.fredbet.web;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

public class ActivePageHandlerInterceptor implements HandlerInterceptor {

	private static final Logger LOG = LoggerFactory.getLogger(ActivePageHandlerInterceptor.class);

	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView)
			throws Exception {
		String requestURI = request.getRequestURI();
		LOG.debug("requestURI: " + requestURI);

		if (requestURI.contains("matches")) {
			addStateAttributeFor("matches", modelAndView);
		} else if (requestURI.contains("bet/open")) {
			addStateAttributeFor("bet_open", modelAndView);
		} else if (requestURI.contains("bet")) {
			addStateAttributeFor("bet", modelAndView);
		} else if (requestURI.contains("ranking")) {
			addStateAttributeFor("ranking", modelAndView);
		} else if (requestURI.contains("user")) {
			addStateAttributeFor("user", modelAndView);
		}
	}

	private void addStateAttributeFor(String page, ModelAndView modelAndView) {
		modelAndView.addObject("pageState_" + page, "active");
	}

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		return true;
	}

	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
	}

}
