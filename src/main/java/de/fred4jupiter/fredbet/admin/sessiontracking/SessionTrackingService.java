package de.fred4jupiter.fredbet.admin.sessiontracking;

import de.fred4jupiter.fredbet.domain.entity.SessionTracking;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class SessionTrackingService {

    private static final Logger LOG = LoggerFactory.getLogger(SessionTrackingService.class);

    private final SessionTrackingRepository sessionTrackingRepository;

    public SessionTrackingService(SessionTrackingRepository sessionTrackingRepository) {
        this.sessionTrackingRepository = sessionTrackingRepository;
    }

    public void registerLogin(String userName, String sessionId) {
        Optional<SessionTracking> sessionTrackingOpt = sessionTrackingRepository.findById(userName);
        SessionTracking sessionTracking = sessionTrackingOpt.orElseGet(SessionTracking::new);

        sessionTracking.setUserName(userName);
        sessionTracking.setSessionId(sessionId);
        sessionTracking.setLastLogin(LocalDateTime.now());
        sessionTrackingRepository.save(sessionTracking);
    }

    public void registerLogout(String sessionId) {
        if (StringUtils.isBlank(sessionId)) {
            LOG.debug("sessionId is blank. Do not delete any saved session.");
            return;
        }

        int deleted = sessionTrackingRepository.deleteBySessionId(sessionId);
        LOG.debug("Deleted {} sessions with id {}", deleted, sessionId);
    }

    public List<SessionTracking> findLoggedInUsers() {
        List<SessionTracking> loggedInUsers = sessionTrackingRepository.findAllByOrderByLastLoginDesc();
        return loggedInUsers.stream().filter(sessionTracking -> {
            Duration duration = Duration.between(sessionTracking.getLastLogin(), LocalDateTime.now());
            return duration.compareTo(Duration.ofDays(1)) < 0; // show only active users that are not older than 1 day
        }).toList();
    }
}
