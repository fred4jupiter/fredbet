package de.fred4jupiter.fredbet.web;

import de.fred4jupiter.fredbet.domain.AppUser;
import de.fred4jupiter.fredbet.security.UserAccessor;

/**
 * For security relatived access from within thymeleaf templates and command objects.
 * 
 * @author mstaehler
 *
 */
public final class SecurityWebUtils {

    private SecurityWebUtils() {
        // only static methods
    }
    
    public static AppUser getCurrentUser() {
    	return UserAccessor.getCurrentUser();
    }
    
    public static boolean hasPermission(String permission) {
    	return getCurrentUser().hasPermission(permission);
    }
}
