package de.fred4jupiter.fredbet.domain;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotEmpty;

/**
 * These properties are changable at runtime (via admin page).
 * 
 * @author michael
 *
 */
public class RuntimeConfig {

	private boolean enabledParentChildRanking;

	private boolean enableChangingUsername;

	private boolean showDemoDataNavigationEntry;

	/**
	 * Creates demo data with additional users and matches.
	 */
	private boolean createDemoData;

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
	private Integer pointsFinalWinner = Integer.valueOf(10);

	/**
	 * Extra betting points for semi final winner.
	 */
	@NotNull
	@Min(value = 0)
	private Integer pointsSemiFinalWinner = Integer.valueOf(5);

	/**
	 * Extra betting points for third winner.
	 */
	@NotNull
	@Min(value = 0)
	private Integer pointsThirdFinalWinner = Integer.valueOf(2);
	
	/**
	 * Backup folder for H2 database backups.
	 */
	private String databaseBackupFolder;

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

	public boolean isCreateDemoData() {
		return createDemoData;
	}

	public void setCreateDemoData(boolean createDemoData) {
		this.createDemoData = createDemoData;
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

	public String getDatabaseBackupFolder() {
		return databaseBackupFolder;
	}

	public void setDatabaseBackupFolder(String databaseBackupFolder) {
		this.databaseBackupFolder = databaseBackupFolder;
	}

}
