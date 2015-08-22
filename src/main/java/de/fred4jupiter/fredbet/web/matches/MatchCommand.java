	package de.fred4jupiter.fredbet.web.matches;

import java.time.LocalDateTime;
import java.util.Date;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.springframework.format.annotation.DateTimeFormat;

import de.fred4jupiter.fredbet.domain.Group;
import de.fred4jupiter.fredbet.util.DateUtils;

public class MatchCommand {

	private String matchId;

	private String teamNameOne;

	private String teamNameTwo;

	private Integer teamResultOne;

	private Integer teamResultTwo;

	private Group group;

	@DateTimeFormat(pattern = "dd.MM.yyyy HH:mm")
	private Date kickOffDate;

	private String stadium;

	private Integer userBetGoalsTeamOne;

	private Integer userBetGoalsTeamTwo;

	private Integer points;

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

	public boolean hasMatchStarted() {
		LocalDateTime kickOffLocalDateTime = DateUtils.toLocalDateTime(kickOffDate);
		return LocalDateTime.now().isAfter(kickOffLocalDateTime);
	}
	
	public boolean hasMatchFinished() {
		return teamResultOne != null && teamResultTwo != null;
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
		builder.append("group", group);
		builder.append("kickOffDate", kickOffDate);
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

	public Group getGroup() {
		return group;
	}

	public void setGroup(Group group) {
		this.group = group;
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
		return points;
	}

	public void setPoints(Integer points) {
		this.points = points;
	}
}
