package de.fred4jupiter.fredbet.service;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import de.fred4jupiter.fredbet.domain.SessionTracking;
import de.fred4jupiter.fredbet.repository.SessionTrackingRepository;

@Service
@Transactional
public class SessionTrackingService {

	@Autowired
	private SessionTrackingRepository sessionTrackingRepository;
	
	public void registerLogin(String userName) {
		SessionTracking sessionTracking = sessionTrackingRepository.findOne(userName);
		if (sessionTracking == null) {
			sessionTracking = new SessionTracking();
		}
		
		sessionTracking.setUserName(userName);
		sessionTracking.setLastLogin(new Date());
		sessionTrackingRepository.save(sessionTracking);
	}
	
	public void registerLogout(String userName) {
		SessionTracking sessionTracking = sessionTrackingRepository.findOne(userName);
		if (sessionTracking == null) {
			return;
		}
		
		sessionTrackingRepository.delete(sessionTracking);		
	}

	public List<SessionTracking> findLoggedInUsers() {
		return sessionTrackingRepository.findAllByOrderByUserNameAsc();
	}
}
