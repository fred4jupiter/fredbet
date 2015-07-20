package de.fred4jupiter.fredbet.web;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Component
public class SecurityBean {

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
}
