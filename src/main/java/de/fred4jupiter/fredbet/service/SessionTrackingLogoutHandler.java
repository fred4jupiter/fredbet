package de.fred4jupiter.fredbet.service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Component;

@Component
public class SessionTrackingLogoutHandler implements LogoutHandler {

	private static final Logger LOG = LoggerFactory.getLogger(SessionTrackingLogoutHandler.class);

	@Autowired
	private SessionTrackingService sessionTrackingService;
	
	@Override
	public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
		LOG.info("user with name {} has logged out", authentication.getName());
		sessionTrackingService.registerLogout(authentication.getName());
	}

}
