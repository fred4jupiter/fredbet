package de.fred4jupiter.fredbet.domain;

import java.time.LocalDateTime;
import java.util.Date;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.format.annotation.DateTimeFormat;

import de.fred4jupiter.fredbet.util.DateUtils;

@Document
public class Match {

	@Id
	private String id;

	private Team teamOne;

	private Team teamTwo;

	private Group group;

	private Integer goalsTeamOne;

	private Integer goalsTeamTwo;

	@DateTimeFormat(pattern = "dd.MM.yyyy HH:mm")
	private Date kickOffDate;

	private String stadium;

	public boolean hasResultSet() {
		return goalsTeamOne != null && goalsTeamTwo != null;
	}
	
	public boolean hasStarted() {
		LocalDateTime kickOffLocalDateTime = DateUtils.toLocalDateTime(kickOffDate);
		return LocalDateTime.now().isAfter(kickOffLocalDateTime);
	}

	public void setTeamOne(Team teamOne) {
		this.teamOne = teamOne;
	}

	public void setTeamTwo(Team teamTwo) {
		this.teamTwo = teamTwo;
	}

	public void enterResult(Integer goalsTeamOne, Integer goalsTeamTwo) {
		this.goalsTeamOne = goalsTeamOne;
		this.goalsTeamTwo = goalsTeamTwo;
	}

	public Integer getGoalDifference() {
		if (goalsTeamOne == null || goalsTeamTwo == null) {
			throw new IllegalStateException("Match has not finished! No goal results set!");
		}
		return Math.abs(goalsTeamOne.intValue() - goalsTeamTwo.intValue());
	}

	public boolean isTeamOneWinner() {
		if (goalsTeamOne == null || goalsTeamTwo == null) {
			throw new IllegalStateException("Match has not finished! No goal results set!");
		}

		return goalsTeamOne.intValue() > goalsTeamTwo.intValue();
	}

	public boolean isTeamTwoWinner() {
		if (goalsTeamOne == null || goalsTeamTwo == null) {
			throw new IllegalStateException("Match has not finished! No goal results set!");
		}

		return goalsTeamTwo.intValue() > goalsTeamOne.intValue();
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

	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (obj == this) {
			return true;
		}
		if (obj.getClass() != getClass()) {
			return false;
		}
		Match match = (Match) obj;
		EqualsBuilder builder = new EqualsBuilder();
		builder.append(id, match.id);
		builder.append(teamOne, match.teamOne);
		builder.append(teamTwo, match.teamTwo);
		builder.append(group, match.group);
		builder.append(goalsTeamOne, match.goalsTeamOne);
		builder.append(goalsTeamTwo, match.goalsTeamTwo);
		builder.append(kickOffDate, match.kickOffDate);
		builder.append(stadium, match.stadium);

		return builder.isEquals();
	}

	@Override
	public int hashCode() {
		HashCodeBuilder builder = new HashCodeBuilder();
		builder.append(id);
		builder.append(teamOne);
		builder.append(teamTwo);
		builder.append(group);
		builder.append(goalsTeamOne);
		builder.append(goalsTeamTwo);
		builder.append(kickOffDate);
		builder.append(stadium);
		return builder.toHashCode();
	}

	@Override
	public String toString() {
		ToStringBuilder builder = new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE);
		builder.append("id", id);
		builder.append("teamOne", teamOne);
		builder.append("teamTwo", teamTwo);
		builder.append("group", group);
		builder.append("goalsTeamOne", goalsTeamOne);
		builder.append("goalsTeamTwo", goalsTeamTwo);
		builder.append("kickOffDate", kickOffDate);
		builder.append("stadium", stadium);
		return builder.toString();
	}

	public Team getTeamOne() {
		return teamOne;
	}

	public Team getTeamTwo() {
		return teamTwo;
	}

	public String getId() {
		return id;
	}

	public Group getGroup() {
		return group;
	}

	public void setGroup(Group group) {
		this.group = group;
	}

	public String getStadium() {
		return stadium;
	}

	public void setStadium(String stadium) {
		this.stadium = stadium;
	}

	public Date getKickOffDate() {
		return kickOffDate;
	}

	public void setKickOffDate(Date kickOffDate) {
		this.kickOffDate = kickOffDate;
	}
}
