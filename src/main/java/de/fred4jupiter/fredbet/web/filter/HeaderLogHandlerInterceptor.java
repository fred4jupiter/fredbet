package de.fred4jupiter.fredbet.web.filter;

import java.util.Collections;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

public class HeaderLogHandlerInterceptor implements HandlerInterceptor {

	private static final Logger LOG = LoggerFactory.getLogger(HeaderLogHandlerInterceptor.class);

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		HttpServletRequest req = (HttpServletRequest) request;
		LOG.debug("----- Request ---------");
		Collections.list(req.getHeaderNames()).forEach(n -> LOG.debug(n + ": " + req.getHeader(n)));
		return true;
	}

	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView)
			throws Exception {
		LOG.debug("----- response ---------");
		response.getHeaderNames().forEach(n -> LOG.debug(n + ": " + response.getHeader(n)));

		LOG.debug("response status: " + response.getStatus());
	}

	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
	}

}
