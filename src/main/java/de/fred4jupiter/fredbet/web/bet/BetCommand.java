package de.fred4jupiter.fredbet.web.bet;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class BetCommand {

	private String betId;
	
	private String matchId;
	
	private String teamNameOne;

	private String teamNameTwo;
	
	private Integer goalsTeamOne;

	private Integer goalsTeamTwo;
	
	@Override
	public String toString() {
		ToStringBuilder builder = new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE);
		builder.append("matchId", matchId);
		builder.append("betId", betId);
		builder.append("teamNameOne", teamNameOne);
		builder.append("teamNameTwo", teamNameTwo);
		builder.append("goalsTeamOne", goalsTeamOne);
		builder.append("goalsTeamTwo", goalsTeamTwo);
		return builder.toString();
	}

	public boolean hasGoalsSet() {
		return goalsTeamOne != null && goalsTeamTwo != null;
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
	
	
}
