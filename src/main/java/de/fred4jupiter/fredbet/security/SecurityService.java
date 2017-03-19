package de.fred4jupiter.fredbet.security;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import de.fred4jupiter.fredbet.domain.AppUser;
import de.fred4jupiter.fredbet.props.FredbetProperties;

@Service
public class SecurityService {

	@Autowired
	private FredbetProperties fredbetProperties;

	public boolean isUserLoggedIn() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication == null) {
			return false;
		}

		Object principal = authentication.getPrincipal();
		if (principal != null && principal instanceof UserDetails) {
			return true;
		}

		return false;
	}

	public String getCurrentUserName() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication == null) {
			return "Not logged in!";
		}

		return authentication.getName();
	}

	public boolean isDemoDataMenuEntryEnabled() {
		return fredbetProperties.isEnableDemoDataCreationNavigationEntry();
	}
	
	public AppUser getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new UsernameNotFoundException("User is not logged in!");
        }
        
        return (AppUser) authentication.getPrincipal();
    }
	
	public boolean isCurrentUserHavingPermission(String permission) {
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
