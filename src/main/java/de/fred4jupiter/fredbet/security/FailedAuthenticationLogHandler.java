package de.fred4jupiter.fredbet.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent;
import org.springframework.stereotype.Component;

@Component
public class FailedAuthenticationLogHandler implements ApplicationListener<AuthenticationFailureBadCredentialsEvent> {

	private static final Logger LOG = LoggerFactory.getLogger(FailedAuthenticationLogHandler.class);

	@Override
	public void onApplicationEvent(AuthenticationFailureBadCredentialsEvent event) {
		Object username = event.getAuthentication().getPrincipal();
		LOG.info("Failed login using username='{}'", username);
	}

}
