package de.fred4jupiter.fredbet.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import de.fred4jupiter.fredbet.domain.AppUser;
import de.fred4jupiter.fredbet.domain.ImageMetaData;
import de.fred4jupiter.fredbet.security.FredBetPermission;
import de.fred4jupiter.fredbet.security.SecurityService;
import de.fred4jupiter.fredbet.service.config.RuntimeConfigurationService;

/**
 * Security utility class for access in Thymeleaf templates.
 * 
 * @author michael
 *
 */
@Component
public class WebSecurityUtil {

	@Autowired
	private RuntimeConfigurationService runtimeConfigurationService;

	@Autowired
	private SecurityService securityService;

	public String getCurrentUserProfileImageKey() {
		return getUserProfileImageKeyFor(securityService.getCurrentUserName());
	}

	public String getUserProfileImageKeyFor(String username) {
		ImageMetaData imageMetaData = securityService.getProfileImageMetaDataFor(username);
		return imageMetaData != null ? imageMetaData.getImageKey() : null;
	}

	public boolean isRoleSelectionDisabledForUser(String username) {
		final AppUser currentUser = securityService.getCurrentUser();
		return currentUser.getUsername().equals(username) || !(currentUser.hasPermission(FredBetPermission.PERM_CHANGE_USER_ROLE));
	}

	public boolean isUsersFirstLogin() {
		try {
			AppUser currentUser = securityService.getCurrentUser();
			return currentUser.isFirstLogin();
		} catch (UsernameNotFoundException e) {
			return false;
		}
	}

	public boolean isUserLoggedIn() {
		return securityService.isUserLoggedIn();
	}

	/**
	 * Will be used in navigation bar.
	 * 
	 * @return
	 */
	public boolean isDemoDataMenuEntryEnabled() {
		return runtimeConfigurationService.loadRuntimeConfig().isShowDemoDataNavigationEntry();
	}
	
	public boolean isChangePasswordOnFirstLogin() {
		return runtimeConfigurationService.loadRuntimeConfig().isChangePasswordOnFirstLogin();
	}

	public String getCurrentUsername() {
		return securityService.getCurrentUserName();
	}
}
