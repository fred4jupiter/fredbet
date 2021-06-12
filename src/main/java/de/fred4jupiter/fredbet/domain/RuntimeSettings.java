package de.fred4jupiter.fredbet.domain;

import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;


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
     * Extra betting points for final winner.
     */
    @NotNull
    @Min(value = 0)
    private Integer pointsFinalWinner = 10;

    /**
     * Extra betting points for semi final winner.
     */
    @NotNull
    @Min(value = 0)
    private Integer pointsSemiFinalWinner = 5;

    /**
     * Extra betting points for third winner.
     */
    @NotNull
    @Min(value = 0)
    private Integer pointsThirdFinalWinner = 2;

    /**
     * If users have to change their password at first login.
     */
    private boolean changePasswordOnFirstLogin;

    @NotNull
    @Min(value = 0)
    private Integer jokerMaxCount = 3;

    private boolean selfRegistrationEnabled;

    @NotNull
    @Length(min = 4)
    private String registrationCode;

    @NotNull
    private String timeZone;

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

    public Integer getPointsFinalWinner() {
        return pointsFinalWinner;
    }

    public void setPointsFinalWinner(Integer pointsFinalWinner) {
        this.pointsFinalWinner = pointsFinalWinner;
    }

    public Integer getPointsSemiFinalWinner() {
        return pointsSemiFinalWinner;
    }

    public void setPointsSemiFinalWinner(Integer pointsSemiFinalWinner) {
        this.pointsSemiFinalWinner = pointsSemiFinalWinner;
    }

    public Integer getPointsThirdFinalWinner() {
        return pointsThirdFinalWinner;
    }

    public void setPointsThirdFinalWinner(Integer pointsThirdFinalWinner) {
        this.pointsThirdFinalWinner = pointsThirdFinalWinner;
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
}
