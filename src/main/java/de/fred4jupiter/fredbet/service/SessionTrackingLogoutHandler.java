package de.fred4jupiter.fredbet.service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.security.web.session.HttpSessionDestroyedEvent;
import org.springframework.stereotype.Component;

@Component
public class SessionTrackingLogoutHandler implements LogoutHandler, ApplicationListener<HttpSessionDestroyedEvent> {

	private static final Logger LOG = LoggerFactory.getLogger(SessionTrackingLogoutHandler.class);

	@Autowired
	private SessionTrackingService sessionTrackingService;
	
	@Override
	public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
		LOG.info("user with name {} has logged out", authentication.getName());
		sessionTrackingService.registerLogout(authentication.getName());
	}

	@Override
	public void onApplicationEvent(HttpSessionDestroyedEvent event) {
		String sessionId = event.getSession().getId();
		LOG.info("user session with sessionId {} has been destroyed", sessionId);
	}

}
