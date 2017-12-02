package de.fred4jupiter.fredbet.domain;

public class RuntimeConfig {

	private boolean enabledParentChildRanking;

	private boolean enableChangingUsername;

	private boolean showDemoDataNavigationEntry;
	
	/**
	 * Creates demo data with additional users and matches.
	 */
	private boolean createDemoData;

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

}
