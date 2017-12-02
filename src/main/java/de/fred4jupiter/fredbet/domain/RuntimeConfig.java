package de.fred4jupiter.fredbet.domain;

public class RuntimeConfig {

	private boolean enabledParentChildRanking;
	
	private boolean enableChangingUsername;

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

}
