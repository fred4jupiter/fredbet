package de.fred4jupiter.fredbet.event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.event.InteractiveAuthenticationSuccessEvent;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;

import de.fred4jupiter.fredbet.service.SessionTrackingService;

@Component
public class SessionLoginTracker implements ApplicationListener<InteractiveAuthenticationSuccessEvent> {

	private static final Logger LOG = LoggerFactory.getLogger(SessionLoginTracker.class);

	@Autowired
	private SessionTrackingService sessionTrackingService;

	@Override
	public void onApplicationEvent(InteractiveAuthenticationSuccessEvent event) {
		UserDetails userDetails = (UserDetails) event.getAuthentication().getPrincipal();
		final String sessionId = RequestContextHolder.getRequestAttributes().getSessionId();
		sessionTrackingService.registerLogin(userDetails.getUsername(), sessionId);
		LOG.info("Login: user={}, sessionId={}", userDetails.getUsername(), sessionId);
	}
}
