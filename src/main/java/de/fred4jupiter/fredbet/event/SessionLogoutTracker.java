package de.fred4jupiter.fredbet.event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.security.web.session.HttpSessionDestroyedEvent;
import org.springframework.stereotype.Component;

import de.fred4jupiter.fredbet.service.SessionTrackingService;

@Component
public class SessionLogoutTracker implements ApplicationListener<HttpSessionDestroyedEvent> {

	private static final Logger LOG = LoggerFactory.getLogger(SessionLogoutTracker.class);

	@Autowired
	private SessionTrackingService sessionTrackingService;

	@Override
	public void onApplicationEvent(HttpSessionDestroyedEvent event) {
		String sessionId = event.getSession().getId();
		sessionTrackingService.registerLogout(sessionId);
		LOG.info("Logout: sessionId={}", sessionId);
	}
}
