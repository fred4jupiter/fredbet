package de.fred4jupiter.fredbet.event;

import de.fred4jupiter.fredbet.admin.SessionTrackingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.event.InteractiveAuthenticationSuccessEvent;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

@Component
public class SessionLoginTracker implements ApplicationListener<InteractiveAuthenticationSuccessEvent> {

    private static final Logger LOG = LoggerFactory.getLogger(SessionLoginTracker.class);

    private final SessionTrackingService sessionTrackingService;

    public SessionLoginTracker(SessionTrackingService sessionTrackingService) {
        this.sessionTrackingService = sessionTrackingService;
    }

    @Override
    public void onApplicationEvent(InteractiveAuthenticationSuccessEvent event) {
        UserDetails userDetails = (UserDetails) event.getAuthentication().getPrincipal();
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        if (requestAttributes != null) {
            final String sessionId = requestAttributes.getSessionId();
            sessionTrackingService.registerLogin(userDetails.getUsername(), sessionId);
            LOG.info("Login: user={}, sessionId={}", userDetails.getUsername(), sessionId);
        }
    }
}
