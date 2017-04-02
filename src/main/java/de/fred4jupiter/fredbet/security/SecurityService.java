package de.fred4jupiter.fredbet.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import de.fred4jupiter.fredbet.domain.AppUser;
import de.fred4jupiter.fredbet.props.FredbetProperties;

/**
 * Provides security informations of the current user.
 * 
 * @author mstaehler
 *
 */
@Service
public class SecurityService {

	@Autowired
	private FredbetProperties fredbetProperties;

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

	/**
	 * Will be used in navigation bar.
	 * 
	 * @return
	 */
	public boolean isDemoDataMenuEntryEnabled() {
		return fredbetProperties.isEnableDemoDataCreationNavigationEntry();
	}

	public boolean isCurrentUserHavingPermission(String permission) {
		return getCurrentUser().hasPermission(permission);
	}

	public boolean isRoleSelectionDisabledForUser(String username) {
		final AppUser currentUser = getCurrentUser();
		return currentUser.getUsername().equals(username)
				|| !(currentUser.hasPermission(FredBetPermission.PERM_CHANGE_USER_ROLE));
	}

	public AppUser getCurrentUser() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication == null || !authentication.isAuthenticated()
				|| !(authentication.getPrincipal() instanceof AppUser)) {
			throw new UsernameNotFoundException("User is not logged in!");
		}

		return (AppUser) authentication.getPrincipal();
	}

}
