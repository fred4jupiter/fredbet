package de.fred4jupiter.fredbet.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.event.InteractiveAuthenticationSuccessEvent;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Component
public class SessionTrackingLoginListener implements ApplicationListener<InteractiveAuthenticationSuccessEvent> {

	private static final Logger LOG = LoggerFactory.getLogger(SessionTrackingLoginListener.class);
	
	@Autowired
	private SessionTrackingService sessionTrackingService;

	@Override
	public void onApplicationEvent(InteractiveAuthenticationSuccessEvent appEvent) {
		InteractiveAuthenticationSuccessEvent event = (InteractiveAuthenticationSuccessEvent) appEvent;
		UserDetails userDetails = (UserDetails) event.getAuthentication().getPrincipal();
		sessionTrackingService.registerLogin(userDetails.getUsername());
		LOG.info("user with name {} has logged in", userDetails.getUsername());
	}

}
