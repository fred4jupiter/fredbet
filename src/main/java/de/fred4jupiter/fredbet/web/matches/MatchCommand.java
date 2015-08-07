package de.fred4jupiter.fredbet.web.matches;

import java.util.Date;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.springframework.format.annotation.DateTimeFormat;

public class MatchCommand {

	private String matchId;

	private String teamNameOne;

	private String teamNameTwo;

	private Integer teamResultOne;

	private Integer teamResultTwo;
	
	private String group;
	
	@DateTimeFormat(pattern = "dd.MM.yyyy HH:mm")
	private Date kickOffDate;

	private String stadium;

	public MatchCommand() {
		super();
	}

	public MatchCommand(String teamNameOne, String teamNameTwo, Integer teamResultOne, Integer teamResultTwo) {
		super();
		this.teamNameOne = teamNameOne;
		this.teamNameTwo = teamNameTwo;
		this.teamResultOne = teamResultOne;
		this.teamResultTwo = teamResultTwo;
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

	public String getMatchId() {
		return matchId;
	}

	public void setMatchId(String matchId) {
		this.matchId = matchId;
	}
	
	@Override
	public String toString() {
		ToStringBuilder builder = new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE);
		builder.append("matchId", matchId);
		builder.append("teamNameOne", teamNameOne);
		builder.append("teamNameTwo", teamNameTwo);
		builder.append("teamResultOne", teamResultOne);
		builder.append("teamResultTwo", teamResultTwo);
		return builder.toString();
	}

	public Date getKickOffDate() {
		return kickOffDate;
	}

	public void setKickOffDate(Date kickOffDate) {
		this.kickOffDate = kickOffDate;
	}

	public String getStadium() {
		return stadium;
	}

	public void setStadium(String stadium) {
		this.stadium = stadium;
	}

	public String getGroup() {
		return group;
	}

	public void setGroup(String group) {
		this.group = group;
	}
}
