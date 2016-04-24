package de.fred4jupiter.fredbet.security;

import java.util.Collection;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import de.fred4jupiter.fredbet.domain.AppUser;

public final class SecurityUtils {

    private SecurityUtils() {
        // only static methods
    }
    
    public static AppUser getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new UsernameNotFoundException("User is not logged in!");
        }
        
        return (AppUser) authentication.getPrincipal();
    }
    
    public static boolean hasPermission(String permission) {
    	AppUser currentUser = getCurrentUser();
    	Collection<? extends GrantedAuthority> authorities = currentUser.getAuthorities();
    	for (GrantedAuthority grantedAuthority : authorities) {
			if (grantedAuthority.getAuthority().equals(permission)) {
				return true;
			}
		}
    	
    	return false;
    }
}
