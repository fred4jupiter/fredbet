package de.fred4jupiter.fredbet.web;

import de.fred4jupiter.fredbet.domain.AppUser;
import de.fred4jupiter.fredbet.domain.ImageMetaData;
import de.fred4jupiter.fredbet.props.FredbetConstants;
import de.fred4jupiter.fredbet.security.FredBetPermission;
import de.fred4jupiter.fredbet.security.SecurityService;
import de.fred4jupiter.fredbet.service.config.RuntimeSettingsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

/**
 * Security utility class for access in Thymeleaf templates.
 *
 * @author michael
 */
@Component
public class WebSecurityUtil {

    private final RuntimeSettingsService runtimeSettingsService;

    private final SecurityService securityService;

    public WebSecurityUtil(RuntimeSettingsService runtimeSettingsService, SecurityService securityService) {
        this.runtimeSettingsService = runtimeSettingsService;
        this.securityService = securityService;
    }

    public String getCurrentUserProfileImageKey() {
        return getUserProfileImageKeyFor(securityService.getCurrentUserName());
    }

    public String getUserProfileImageKeyFor(String username) {
        ImageMetaData imageMetaData = securityService.getProfileImageMetaDataFor(username);
        return imageMetaData != null ? imageMetaData.getImageKey() : null;
    }

    public boolean isRoleSelectionDisabledForUser(String username) {
        if (FredbetConstants.TECHNICAL_USERNAME.equals(username)) {
            return true;
        }

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
        return runtimeSettingsService.loadRuntimeSettings().isShowDemoDataNavigationEntry();
    }

    public boolean isChangePasswordOnFirstLogin() {
        return runtimeSettingsService.loadRuntimeSettings().isChangePasswordOnFirstLogin();
    }

    public String getCurrentUsername() {
        return securityService.getCurrentUserName();
    }

    public boolean isCurrentUserTheDefaultAdminUser() {
        return FredbetConstants.TECHNICAL_USERNAME.equals(getCurrentUsername());
    }
}
