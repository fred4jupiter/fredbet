package de.fred4jupiter.fredbet.web.bet;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import de.fred4jupiter.fredbet.domain.Team;
import de.fred4jupiter.fredbet.web.MessageUtil;

public class BetCommand {

	private String betId;

	private String matchId;

	private Team teamOne;

	private Team teamTwo;

	private Integer goalsTeamOne;

	private Integer goalsTeamTwo;

	private MessageUtil messageUtil;

	public BetCommand() {
	}
	
	public BetCommand(MessageUtil messageUtil) {
		this.messageUtil = messageUtil;
	}
	
	public void setMessageUtil(MessageUtil messageUtil) {
		this.messageUtil = messageUtil;
	}

	@Override
	public String toString() {
		ToStringBuilder builder = new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE);
		builder.append("matchId", matchId);
		builder.append("betId", betId);
		builder.append("teamOne", teamOne);
		builder.append("teamTwo", teamTwo);
		builder.append("goalsTeamOne", goalsTeamOne);
		builder.append("goalsTeamTwo", goalsTeamTwo);
		return builder.toString();
	}

	public boolean hasGoalsSet() {
		return goalsTeamOne != null && goalsTeamTwo != null;
	}

	public String getTeamNameOne() {
		return messageUtil.getTeamName(teamOne);
	}

	public String getTeamNameTwo() {
		return messageUtil.getTeamName(teamTwo);
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

	public String getBetId() {
		return betId;
	}

	public void setBetId(String betId) {
		this.betId = betId;
	}

	public String getMatchId() {
		return matchId;
	}

	public void setMatchId(String matchId) {
		this.matchId = matchId;
	}

	public void setTeamOne(Team teamOne) {
		this.teamOne = teamOne;
	}

	public void setTeamTwo(Team teamTwo) {
		this.teamTwo = teamTwo;
	}

	public Team getTeamOne() {
		return teamOne;
	}

	public Team getTeamTwo() {
		return teamTwo;
	}
}
