package de.fred4jupiter.fredbet.domain;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;


/**
 * These properties are changable at runtime (via admin page).
 *
 * @author michael
 */
public class RuntimeSettings {

    private boolean enabledParentChildRanking;

    private boolean enableChangingUsername;

    private boolean showDemoDataNavigationEntry;

    /**
     * Sum points per user for selected country that will be shown in points
     * statistics.
     */
    private Country favouriteCountry;

    /**
     * Password used if the user password has been reset.
     */
    @NotEmpty
    private String passwordForReset;

    /**
     * If users have to change their password at first login.
     */
    private boolean changePasswordOnFirstLogin;

    @NotNull
    @Min(value = 0)
    private Integer jokerMaxCount = 3;

    private Integer imageUploadLimit;

    private boolean selfRegistrationEnabled;

    @NotNull
    @Length(min = 4)
    private String registrationCode;

    @NotNull
    private String timeZone;

    @NotNull
    private Theme bootswatchTheme = Theme.DEFAULT;

    @NotNull
    private NavbarLayout navbarLayout = NavbarLayout.DARK;

    public boolean isEnabledParentChildRanking() {
        return enabledParentChildRanking;
    }

    public void setEnabledParentChildRanking(boolean enabledParentChildRanking) {
        this.enabledParentChildRanking = enabledParentChildRanking;
    }

    public boolean isEnableChangingUsername() {
        return enableChangingUsername;
    }

    public void setEnableChangingUsername(boolean enableChangingUsername) {
        this.enableChangingUsername = enableChangingUsername;
    }

    public boolean isShowDemoDataNavigationEntry() {
        return showDemoDataNavigationEntry;
    }

    public void setShowDemoDataNavigationEntry(boolean showDemoDataNavigationEntry) {
        this.showDemoDataNavigationEntry = showDemoDataNavigationEntry;
    }

    public Country getFavouriteCountry() {
        return favouriteCountry;
    }

    public void setFavouriteCountry(Country favouriteCountry) {
        this.favouriteCountry = favouriteCountry;
    }

    public String getPasswordForReset() {
        return passwordForReset;
    }

    public void setPasswordForReset(String passwordForReset) {
        this.passwordForReset = passwordForReset;
    }

    public boolean isChangePasswordOnFirstLogin() {
        return changePasswordOnFirstLogin;
    }

    public void setChangePasswordOnFirstLogin(boolean changePasswordOnFirstLogin) {
        this.changePasswordOnFirstLogin = changePasswordOnFirstLogin;
    }

    public Integer getJokerMaxCount() {
        return jokerMaxCount;
    }

    public void setJokerMaxCount(Integer jokerMaxCount) {
        this.jokerMaxCount = jokerMaxCount;
    }

    public boolean isSelfRegistrationEnabled() {
        return selfRegistrationEnabled;
    }

    public void setSelfRegistrationEnabled(boolean selfRegistrationEnabled) {
        this.selfRegistrationEnabled = selfRegistrationEnabled;
    }

    public String getRegistrationCode() {
        return registrationCode;
    }

    public void setRegistrationCode(String registrationCode) {
        this.registrationCode = registrationCode;
    }

    public String getTimeZone() {
        return timeZone;
    }

    public void setTimeZone(String timeZone) {
        this.timeZone = timeZone;
    }

    public Theme getBootswatchTheme() {
        return bootswatchTheme;
    }

    public void setBootswatchTheme(Theme bootswatchTheme) {
        this.bootswatchTheme = bootswatchTheme;
    }

    public NavbarLayout getNavbarLayout() {
        return navbarLayout;
    }

    public void setNavbarLayout(NavbarLayout navbarLayout) {
        this.navbarLayout = navbarLayout;
    }

    public String getBootswatchThemeUrl() {
        final String selectedTheme = this.bootswatchTheme.toString().toLowerCase();
        if (Theme.DEFAULT.name().equalsIgnoreCase(selectedTheme)) {
            return "/webjars/bootstrap/css/bootstrap.min.css";
        }
        return "/webjars/bootswatch/" + selectedTheme + "/bootstrap.min.css";
    }

    public Integer getImageUploadLimit() {
        return imageUploadLimit;
    }

    public void setImageUploadLimit(Integer imageUploadLimit) {
        this.imageUploadLimit = imageUploadLimit;
    }
}
