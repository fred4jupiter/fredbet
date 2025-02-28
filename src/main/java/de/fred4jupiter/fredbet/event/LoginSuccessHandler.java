package de.fred4jupiter.fredbet.event;

import de.fred4jupiter.fredbet.domain.entity.AppUser;
import de.fred4jupiter.fredbet.user.AppUserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Optional;

@Component
public class LoginSuccessHandler implements ApplicationListener<AuthenticationSuccessEvent> {

    private static final Logger LOG = LoggerFactory.getLogger(LoginSuccessHandler.class);

    private final AppUserRepository appUserRepository;

    public LoginSuccessHandler(AppUserRepository appUserRepository) {
        this.appUserRepository = appUserRepository;
    }

    @Override
    public void onApplicationEvent(AuthenticationSuccessEvent event) {
        Object principal = event.getAuthentication().getPrincipal();
        if (principal instanceof AppUser appUser) {
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
