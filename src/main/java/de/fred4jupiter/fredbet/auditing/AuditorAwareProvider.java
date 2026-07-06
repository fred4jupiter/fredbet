package de.fred4jupiter.fredbet.auditing;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class AuditorAwareProvider implements AuditorAware<String> {

    private static final Logger LOG = LoggerFactory.getLogger(AuditorAwareProvider.class);

    private static final String JOB_AUDITOR = "JOB";

    @Override
    public Optional<String> getCurrentAuditor() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        LOG.debug("getCurrentAuditor called - Authentication: {}", authentication != null ? authentication.getName() : "null");

        if (authentication == null || !authentication.isAuthenticated()) {
            LOG.debug("No authentication found or not authenticated");
            return Optional.of(JOB_AUDITOR);
        }

        String username = authentication.getName();
        LOG.debug("Returning auditor: {}", username);
        return Optional.of(username);
    }
}
