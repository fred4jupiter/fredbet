package de.fred4jupiter.fredbet.web.matches;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import de.fred4jupiter.fredbet.web.AbstractMatchHeaderCommand;
import de.fred4jupiter.fredbet.web.MessageUtil;

public class MatchCommand extends AbstractMatchHeaderCommand {

	private Long matchId;

	private Integer teamResultOne;

	private Integer teamResultTwo;

	private Integer userBetGoalsTeamOne;

	private Integer userBetGoalsTeamTwo;

	private Integer points;

	private boolean deletable;

	private boolean penaltyWinnerOneBet;

	private boolean penaltyWinnerOneMatch;

	public MatchCommand(MessageUtil messageUtil) {
		super(messageUtil);
	}

	public boolean isBettable() {
		if (hasMatchStarted() || hasMatchFinished()) {
			return false;
		}

		return true;
	}

	public boolean hasMatchFinished() {
		return teamResultOne != null && teamResultTwo != null;
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

	public Long getMatchId() {
		return matchId;
	}

	public void setMatchId(Long matchId) {
		this.matchId = matchId;
	}

	@Override
	public String toString() {
		ToStringBuilder builder = new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE);
		builder.appendSuper(super.toString());
		builder.append("matchId", matchId);
		builder.append("teamResultOne", teamResultOne);
		builder.append("teamResultTwo", teamResultTwo);
		builder.append("group", group);
		builder.append("kickOffDate", getKickOffDate());
		return builder.toString();
	}

	public String getUserBetGoalsTeamOneCssClasses() {
		if (userBetGoalsTeamOne == null) {
			return "label-default";
		}

		return !isGroupMatch() && isPenaltyWinnerOneBet() ? "label-success badge-penalty-winner" : "label-success";
	}

	public String getUserBetGoalsTeamTwoCssClasses() {
		if (userBetGoalsTeamTwo == null) {
			return "label-default";
		}

		return !isGroupMatch() && !isPenaltyWinnerOneBet() ? "label-success badge-penalty-winner" : "label-success";
	}

	public String getTeamResultOneCssClasses() {
		if (teamResultOne == null) {
			return "label-default";
		}

		return !isGroupMatch() && isPenaltyWinnerOneMatch() ? "label-info badge-penalty-winner" : "label-info";
	}

	public String getTeamResultTwoCssClasses() {
		if (teamResultTwo == null) {
			return "label-default";
		}

		return !isGroupMatch() && !isPenaltyWinnerOneMatch() ? "label-info badge-penalty-winner" : "label-info";
	}

	public Integer getUserBetGoalsTeamOne() {
		return userBetGoalsTeamOne;
	}

	public void setUserBetGoalsTeamOne(Integer userBetgoalsTeamOne) {
		this.userBetGoalsTeamOne = userBetgoalsTeamOne;
	}

	public Integer getUserBetGoalsTeamTwo() {
		return userBetGoalsTeamTwo;
	}

	public void setUserBetGoalsTeamTwo(Integer userBetGoalsTeamTwo) {
		this.userBetGoalsTeamTwo = userBetGoalsTeamTwo;
	}

	public Integer getPoints() {
		if (hasMatchFinished() && points == null) {
			return Integer.valueOf(0);
		}
		return points;
	}

	public void setPoints(Integer points) {
		this.points = points;
	}

	public boolean isOnlyOneResultSet() {
		return (getTeamResultOne() == null && getTeamResultTwo() != null) || (getTeamResultOne() != null && getTeamResultTwo() == null);
	}

	public boolean hasValidGoals() {
		return (getTeamResultOne() != null && getTeamResultOne().intValue() < 0)
				|| (getTeamResultTwo() != null && getTeamResultTwo().intValue() < 0);
	}

	public boolean isTeamNamesEmpty() {
		return (isBlank(countryTeamOne) && isBlank(countryTeamTwo))
				&& (StringUtils.isBlank(nameTeamOne) && StringUtils.isBlank(nameTeamTwo));
	}

	public boolean isDeletable() {
		return deletable;
	}

	public void setDeletable(boolean deletable) {
		this.deletable = deletable;
	}

	public boolean isGroupMatch() {
		return this.group.name().startsWith("GROUP");
	}

	public boolean isPenaltyWinnerOneBet() {
		return penaltyWinnerOneBet;
	}

	public void setPenaltyWinnerOneBet(boolean penaltyWinnerOneBet) {
		this.penaltyWinnerOneBet = penaltyWinnerOneBet;
	}

	public boolean isPenaltyWinnerOneMatch() {
		return penaltyWinnerOneMatch;
	}

	public void setPenaltyWinnerOneMatch(boolean penaltyWinnerOneMatch) {
		this.penaltyWinnerOneMatch = penaltyWinnerOneMatch;
	}

	public boolean isUndecidedBetting() {
		return getGoalDifferenceBetting().intValue() == 0;
	}
	
	private Integer getGoalDifferenceBetting() {
		if (userBetGoalsTeamOne == null || userBetGoalsTeamTwo == null) {
			throw new IllegalStateException("No goal bets set!");
		}
		return Math.abs(userBetGoalsTeamOne.intValue() - userBetGoalsTeamTwo.intValue());
	}

	public boolean isUndecidedResult() {
		return getGoalDifferenceMatch().intValue() == 0;
	}
	
	private Integer getGoalDifferenceMatch() {
		if (teamResultOne == null || teamResultTwo == null) {
			throw new IllegalStateException("No goals match set!");
		}
		return Math.abs(teamResultOne.intValue() - teamResultTwo.intValue());
	}
}
