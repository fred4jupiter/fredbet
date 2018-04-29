package de.fred4jupiter.fredbet.event;

import java.time.LocalDateTime;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.stereotype.Component;

import de.fred4jupiter.fredbet.domain.AppUser;
import de.fred4jupiter.fredbet.repository.AppUserRepository;

@Component
public class LoginSuccessHandler implements ApplicationListener<AuthenticationSuccessEvent> {

	private static final Logger LOG = LoggerFactory.getLogger(LoginSuccessHandler.class);

	@Autowired
	private AppUserRepository appUserRepository;

	@Override
	public void onApplicationEvent(AuthenticationSuccessEvent event) {
		Object principal = event.getAuthentication().getPrincipal();
		if (principal != null && principal instanceof AppUser) {
			AppUser appUser = (AppUser) principal;
			LOG.debug("User with name {} has logged in.", appUser.getUsername());
			Optional<AppUser> appUserOpt = appUserRepository.findById(appUser.getId());
			if (appUserOpt.isPresent()) {
				AppUser foundAppUser = appUserOpt.get();
				foundAppUser.setLastLogin(LocalDateTime.now());
				appUserRepository.save(foundAppUser);
			}
		}
	}
}
