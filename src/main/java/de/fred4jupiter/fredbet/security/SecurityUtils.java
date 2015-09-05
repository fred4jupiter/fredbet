package de.fred4jupiter.fredbet.security;

import org.springframework.security.core.Authentication;
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
}
