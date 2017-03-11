package de.fred4jupiter.fredbet.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import de.fred4jupiter.fredbet.props.FredbetProperties;

@Component
public class SecurityBean {

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
}
