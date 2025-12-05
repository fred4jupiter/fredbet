package de.fred4jupiter.fredbet.web;

import de.fred4jupiter.fredbet.domain.entity.AppUser;
import de.fred4jupiter.fredbet.domain.entity.AppUserSetting;
import de.fred4jupiter.fredbet.domain.entity.ImageMetaData;
import de.fred4jupiter.fredbet.image.ImageAdministrationService;
import de.fred4jupiter.fredbet.props.FredbetProperties;
import de.fred4jupiter.fredbet.security.FredBetPermission;
import de.fred4jupiter.fredbet.security.SecurityService;
import de.fred4jupiter.fredbet.settings.RuntimeSettingsService;
import org.springframework.boot.autoconfigure.h2.H2ConsoleProperties;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Security utility class for access in Thymeleaf templates.
 *
 * @author michael
 */
@Component
public class WebSecurityUtil {

    private final RuntimeSettingsService runtimeSettingsService;

    private final SecurityService securityService;

    private final ImageAdministrationService imageAdministrationService;

    private final Optional<H2ConsoleProperties> h2ConsoleProperties;

    private final String adminUsername;

    public WebSecurityUtil(RuntimeSettingsService runtimeSettingsService, SecurityService securityService,
                           ImageAdministrationService imageAdministrationService,
                           Optional<H2ConsoleProperties> h2ConsoleProperties, FredbetProperties fredbetProperties) {
        this.runtimeSettingsService = runtimeSettingsService;
        this.securityService = securityService;
        this.imageAdministrationService = imageAdministrationService;
        this.h2ConsoleProperties = h2ConsoleProperties;
        this.adminUsername = fredbetProperties.adminUsername();
    }

    public String getCurrentUserProfileImageKey() {
        return getUserProfileImageKeyFor(securityService.getCurrentUserName());
    }

    public String getUserProfileImageKeyFor(String username) {
        ImageMetaData imageMetaData = imageAdministrationService.getProfileImageMetaDataFor(username);
        return imageMetaData != null ? imageMetaData.getImageKey() : null;
    }

    public boolean isRoleSelectionDisabledForUser(String username) {
        if (adminUsername.equals(username)) {
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
     * @return true if demo data menu is enabled.
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

    public AppUser getCurrentUser() {
        return securityService.getCurrentUser();
    }

    public boolean isCurrentUserTheDefaultAdminUser() {
        return adminUsername.equals(getCurrentUsername());
    }

    public String resolveH2Path() {
        return h2ConsoleProperties.isPresent() ? h2ConsoleProperties.get().getPath() : "";
    }

    public String getCurrentUserTheme() {
        if (!isUserLoggedIn()) {
            return AppUserSetting.DEFAULT_THEME;
        }

        return securityService.getCurrentUser().getAppUserSetting().getTheme();
    }

    public String getBootswatchThemeUrl() {
        if (!isUserLoggedIn()) {
            return AppUserSetting.DEFAULT_BOOTSWATCH_THEME.getBootswatchThemeUrl();
        }

        return securityService.getCurrentUser().getAppUserSetting().getBootswatchTheme().getBootswatchThemeUrl();
    }

    public String getNavbarTag() {
        if (!isUserLoggedIn()) {
            return AppUserSetting.DEFAULT_NAVBAR_LAYOUT.getNavbarTag();
        }

        return securityService.getCurrentUser().getAppUserSetting().getNavbarLayout().getNavbarTag();
    }
}
