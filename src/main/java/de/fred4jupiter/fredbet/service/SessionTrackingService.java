package de.fred4jupiter.fredbet.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import de.fred4jupiter.fredbet.domain.SessionTracking;
import de.fred4jupiter.fredbet.repository.SessionTrackingRepository;
import de.fred4jupiter.fredbet.util.DateUtils;

@Service
@Transactional
public class SessionTrackingService {

	@Autowired
	private SessionTrackingRepository sessionTrackingRepository;

	public void registerLogin(String userName, String sessionId) {
		Optional<SessionTracking> sessionTrackingOpt = sessionTrackingRepository.findById(userName);
		SessionTracking sessionTracking = null;
		if (!sessionTrackingOpt.isPresent()) {
			sessionTracking = new SessionTracking();
		}
		else {
			sessionTracking = sessionTrackingOpt.get();
		}

		sessionTracking.setUserName(userName);
		sessionTracking.setSessionId(sessionId);
		sessionTracking.setLastLogin(LocalDateTime.now());
		sessionTrackingRepository.save(sessionTracking);
	}

	public void registerLogout(String sessionId) {
		SessionTracking sessionTracking = sessionTrackingRepository.findBySessionId(sessionId);
		if (sessionTracking == null) {
			return;
		}

		sessionTrackingRepository.delete(sessionTracking);
	}

	public List<SessionTracking> findLoggedInUsers() {
		return sessionTrackingRepository.findAllByOrderByUserNameAsc();
	}

	public void purgeOldActiveUsers() {
		LocalDateTime nowThirtyMinAgo = LocalDateTime.now().minusMinutes(30);
		List<SessionTracking> sessions = sessionTrackingRepository.findByLastLoginLessThan(DateUtils.toDate(nowThirtyMinAgo));
		sessionTrackingRepository.deleteAll(sessions);
	}
}
