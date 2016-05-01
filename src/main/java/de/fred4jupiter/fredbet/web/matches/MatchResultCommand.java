package de.fred4jupiter.fredbet.web.matches;

import de.fred4jupiter.fredbet.domain.Country;
import de.fred4jupiter.fredbet.domain.Match;
import de.fred4jupiter.fredbet.web.MessageUtil;

public class MatchResultCommand {

	private Long matchId;

	private Country countryTeamOne;
	private Country countryTeamTwo;

	private String nameTeamOne;
	private String nameTeamTwo;

	private Integer teamResultOne;

	private Integer teamResultTwo;

	private final MessageUtil messageUtil;
	
	private boolean penaltyWinnerOne;

	private Match match;

	public MatchResultCommand(MessageUtil messageUtil) {
		this.messageUtil = messageUtil;
	}
	
	public MatchResultCommand(MessageUtil messageUtil, Match match) {
		this.messageUtil = messageUtil;
		this.match = match;
		this.matchId = match.getId();
		this.countryTeamOne = match.getCountryOne();
		this.countryTeamTwo = match.getCountryTwo();
		this.nameTeamOne = match.getTeamNameOne();
		this.nameTeamTwo = match.getTeamNameTwo();
		this.teamResultOne = match.getGoalsTeamOne();
		this.teamResultTwo = match.getGoalsTeamTwo();
		this.penaltyWinnerOne = match.isPenaltyWinnerOne();
	}

	public Long getMatchId() {
		return matchId;
	}

	public void setMatchId(Long matchId) {
		this.matchId = matchId;
	}

	public String getTeamNameOne() {
		if (this.countryTeamOne == null) {
			return nameTeamOne;
		}
		return isNotBlank(countryTeamOne) ? messageUtil.getCountryName(countryTeamOne) : nameTeamOne;
	}

	public String getTeamNameTwo() {
		if (this.countryTeamTwo == null) {
			return nameTeamTwo;
		}
		return isNotBlank(countryTeamTwo) ? messageUtil.getCountryName(countryTeamTwo) : nameTeamTwo;
	}

	private boolean isNotBlank(Country country) {
		if (country == null) {
			return false;
		}

		if (Country.NONE.equals(country)) {
			return false;
		}

		return true;
	}

	public boolean hasValidGoals() {
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

	public boolean isShowCountryIcons() {
		if (this.countryTeamOne == null || this.countryTeamTwo == null) {
			return false;
		}
		return isNotBlank(countryTeamOne) && isNotBlank(countryTeamTwo);
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
		return this.match.isGroupMatch();
	}
}
