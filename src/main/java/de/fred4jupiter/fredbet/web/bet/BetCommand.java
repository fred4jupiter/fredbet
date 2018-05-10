package de.fred4jupiter.fredbet.web.bet;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class BetCommand {

	private Long betId;

	private Long matchId;

	@NotNull
	@Min(value = 0)
	private Integer goalsTeamOne;

	@NotNull
	@Min(value = 0)
	private Integer goalsTeamTwo;

	private String redirectViewName;

	private String teamNameOne;

	private String teamNameTwo;

	private boolean groupMatch;

	private boolean penaltyWinnerOne;

	private String iconPathTeamOne;

	private String iconPathTeamTwo;

	private boolean showCountryIcons;

	private boolean useJoker;
	private Integer numberOfJokersUsed;
	
	/*
	 * maximum jokers allowed to use (general setting)
	 */
	private Integer maxJokers; 
	private boolean jokerEditable;

	@Override
	public String toString() {
		ToStringBuilder builder = new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE);
		builder.append("matchId", matchId);
		builder.append("betId", betId);
		builder.append("goalsTeamOne", goalsTeamOne);
		builder.append("goalsTeamTwo", goalsTeamTwo);
		builder.append("redirectViewName", redirectViewName);
		builder.append("teamNameOne", teamNameOne);
		builder.append("teamNameTwo", teamNameTwo);
		builder.append("groupMatch", groupMatch);
		builder.append("penaltyWinnerOne", penaltyWinnerOne);
		builder.append("iconPathTeamOne", iconPathTeamOne);
		builder.append("iconPathTeamTwo", iconPathTeamTwo);
		builder.append("showCountryIcons", showCountryIcons);
		builder.append("useJoker", useJoker);
		return builder.toString();
	}

	public String getTeamNameOne() {
		return teamNameOne;
	}

	public String getTeamNameTwo() {
		return teamNameTwo;
	}

	public Integer getGoalsTeamOne() {
		return goalsTeamOne;
	}

	public void setGoalsTeamOne(Integer goalsTeamOne) {
		this.goalsTeamOne = goalsTeamOne;
	}

	public Integer getGoalsTeamTwo() {
		return goalsTeamTwo;
	}

	public void setGoalsTeamTwo(Integer goalsTeamTwo) {
		this.goalsTeamTwo = goalsTeamTwo;
	}

	public Long getBetId() {
		return betId;
	}

	public void setBetId(Long betId) {
		this.betId = betId;
	}

	public Long getMatchId() {
		return matchId;
	}

	public void setMatchId(Long matchId) {
		this.matchId = matchId;
	}

	public boolean isShowCountryIcons() {
		return showCountryIcons;
	}

	public String getIconPathTeamOne() {
		return iconPathTeamOne;
	}

	public String getIconPathTeamTwo() {
		return iconPathTeamTwo;
	}

	public String getRedirectViewName() {
		return redirectViewName;
	}

	public void setRedirectViewName(String redirectViewName) {
		this.redirectViewName = redirectViewName;
	}

	public boolean isPenaltyWinnerOne() {
		return penaltyWinnerOne;
	}

	public void setPenaltyWinnerOne(boolean penaltyWinnerOne) {
		this.penaltyWinnerOne = penaltyWinnerOne;
	}

	public boolean isGroupMatch() {
		return groupMatch;
	}

	public void setTeamNameOne(String teamNameOne) {
		this.teamNameOne = teamNameOne;
	}

	public void setTeamNameTwo(String teamNameTwo) {
		this.teamNameTwo = teamNameTwo;
	}

	public void setGroupMatch(boolean groupMatch) {
		this.groupMatch = groupMatch;
	}

	public void setIconPathTeamOne(String iconPathTeamOne) {
		this.iconPathTeamOne = iconPathTeamOne;
	}

	public void setIconPathTeamTwo(String iconPathTeamTwo) {
		this.iconPathTeamTwo = iconPathTeamTwo;
	}

	public void setShowCountryIcons(boolean showCountryIcons) {
		this.showCountryIcons = showCountryIcons;
	}

	public boolean isUseJoker() {
		return useJoker;
	}

	public void setUseJoker(boolean useJoker) {
		this.useJoker = useJoker;
	}

	public Integer getNumberOfJokersUsed() {
		return numberOfJokersUsed;
	}

	public void setNumberOfJokersUsed(Integer numberOfJokersUsed) {
		this.numberOfJokersUsed = numberOfJokersUsed;
	}

	public Integer getMaxJokers() {
		return maxJokers;
	}

	public void setMaxJokers(Integer maxJokers) {
		this.maxJokers = maxJokers;
	}

	public boolean isJokerEditable() {
		return jokerEditable;
	}

	public void setJokerEditable(boolean jokerEditable) {
		this.jokerEditable = jokerEditable;
	}

}
