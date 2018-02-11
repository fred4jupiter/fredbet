package de.fred4jupiter.fredbet.web.matches;

import de.fred4jupiter.fredbet.domain.Country;

public class MatchResultCommand {

	private Long matchId;

	private Country countryTeamOne;
	private Country countryTeamTwo;

	private String teamNameOne;
	private String teamNameTwo;

	private Integer teamResultOne;
	private Integer teamResultTwo;

	private boolean penaltyWinnerOne;

	private boolean groupMatch;

	private boolean showCountryIcons;

	public Long getMatchId() {
		return matchId;
	}

	public void setMatchId(Long matchId) {
		this.matchId = matchId;
	}

	public boolean hasInvalidGoals() {
		return (getTeamResultOne() != null && getTeamResultOne().intValue() < 0)
				|| (getTeamResultTwo() != null && getTeamResultTwo().intValue() < 0);
	}

	public Integer getTeamResultOne() {
		return teamResultOne;
	}

	public void setTeamResultOne(Integer teamResultOne) {
		this.teamResultOne = teamResultOne;
	}

	public Integer getTeamResultTwo() {
		return teamResultTwo;
	}

	public void setTeamResultTwo(Integer teamResultTwo) {
		this.teamResultTwo = teamResultTwo;
	}

	public boolean isOnlyOneResultSet() {
		return (getTeamResultOne() == null && getTeamResultTwo() != null) || (getTeamResultOne() != null && getTeamResultTwo() == null);
	}

	public String getIconPathTeamOne() {
		if (this.countryTeamOne == null) {
			return "";
		}

		return this.countryTeamOne.getIconPath();
	}

	public String getIconPathTeamTwo() {
		if (this.countryTeamTwo == null) {
			return "";
		}

		return this.countryTeamTwo.getIconPath();
	}

	public boolean isPenaltyWinnerOne() {
		return penaltyWinnerOne;
	}

	public void setPenaltyWinnerOne(boolean penaltyWinnerOne) {
		this.penaltyWinnerOne = penaltyWinnerOne;
	}

	public boolean isGroupMatch() {
		return this.groupMatch;
	}

	public void setGroupMatch(boolean groupMatch) {
		this.groupMatch = groupMatch;
	}

	public Country getCountryTeamOne() {
		return countryTeamOne;
	}

	public void setCountryTeamOne(Country countryTeamOne) {
		this.countryTeamOne = countryTeamOne;
	}

	public Country getCountryTeamTwo() {
		return countryTeamTwo;
	}

	public void setCountryTeamTwo(Country countryTeamTwo) {
		this.countryTeamTwo = countryTeamTwo;
	}

	public boolean isShowCountryIcons() {
		return showCountryIcons;
	}

	public void setShowCountryIcons(boolean showCountryIcons) {
		this.showCountryIcons = showCountryIcons;
	}

	public String getTeamNameOne() {
		return teamNameOne;
	}

	public void setTeamNameOne(String teamNameOne) {
		this.teamNameOne = teamNameOne;
	}

	public String getTeamNameTwo() {
		return teamNameTwo;
	}

	public void setTeamNameTwo(String teamNameTwo) {
		this.teamNameTwo = teamNameTwo;
	}
}
