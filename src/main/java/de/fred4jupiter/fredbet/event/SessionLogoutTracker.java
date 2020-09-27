package de.fred4jupiter.fredbet.event;

import de.fred4jupiter.fredbet.service.admin.SessionTrackingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.security.web.session.HttpSessionDestroyedEvent;
import org.springframework.stereotype.Component;

@Component
public class SessionLogoutTracker implements ApplicationListener<HttpSessionDestroyedEvent> {

    private static final Logger LOG = LoggerFactory.getLogger(SessionLogoutTracker.class);

    private final SessionTrackingService sessionTrackingService;

    public SessionLogoutTracker(SessionTrackingService sessionTrackingService) {
        this.sessionTrackingService = sessionTrackingService;
    }

    @Override
    public void onApplicationEvent(HttpSessionDestroyedEvent event) {
        String sessionId = event.getSession().getId();
        sessionTrackingService.registerLogout(sessionId);
        LOG.info("Logout: sessionId={}", sessionId);
    }
}
