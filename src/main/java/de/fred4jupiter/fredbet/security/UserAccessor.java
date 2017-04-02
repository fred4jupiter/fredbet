package de.fred4jupiter.fredbet.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import de.fred4jupiter.fredbet.domain.AppUser;

/**
 * Provides the current user.
 * 
 * @author mstaehler
 *
 */
public final class UserAccessor {

	private UserAccessor() {
		// only static methods
	}
	
	public static AppUser getCurrentUser() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication == null || !authentication.isAuthenticated() || !(authentication.getPrincipal() instanceof AppUser)) {
			throw new UsernameNotFoundException("User is not logged in!");
		}

		return (AppUser) authentication.getPrincipal();
	}
}
