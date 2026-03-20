package de.fred4jupiter.fredbet.security;

import de.fred4jupiter.fredbet.domain.entity.AppUser;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Provides security information of the current user.
 *
 * @author mstaehler
 */
@Service
public class SecurityService {

    public boolean isUserLoggedIn() {
        try {
            getCurrentUser();
            return true;
        } catch (UsernameNotFoundException e) {
            return false;
        }
    }

    public String getCurrentUserName() {
        return getCurrentUser().getUsername();
    }

    public boolean isCurrentUserHavingPermission(String permission) {
        return getCurrentUser().hasPermission(permission);
    }

    public AppUser getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || !(authentication.getPrincipal() instanceof AppUser)) {
            throw new UsernameNotFoundException("User is not logged in!");
        }

        return (AppUser) authentication.getPrincipal();
    }

    public void resetFirstLogin(AppUser appUser) {
        appUser.setFirstLogin(false);

        try {
            AppUser currentUser = getCurrentUser();
            currentUser.setFirstLogin(false);
        } catch (UsernameNotFoundException e) {
            // ignore if user is not logged in
        }
    }
}
