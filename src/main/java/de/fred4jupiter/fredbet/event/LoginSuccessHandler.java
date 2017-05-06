package de.fred4jupiter.fredbet.event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.stereotype.Component;

@Component
public class LoginSuccessHandler implements ApplicationListener<AuthenticationSuccessEvent> {

	private static final Logger LOG = LoggerFactory.getLogger(LoginSuccessHandler.class);

	@Override
	public void onApplicationEvent(AuthenticationSuccessEvent event) {
		String userName = event.getAuthentication().getName();
		LOG.debug("User with name {} has logged in.", userName);
	}

}
