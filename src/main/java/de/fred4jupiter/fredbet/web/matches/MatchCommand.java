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

	public MatchCommand() {
	}

	public MatchCommand(MessageUtil messageUtil) {
		super(messageUtil);
	}

	private boolean hasResults() {
		return teamResultOne != null && teamResultTwo != null;
	}

	public boolean isBettable() {
		if (hasMatchStarted() || hasMatchFinished() || hasResults()) {
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

}
