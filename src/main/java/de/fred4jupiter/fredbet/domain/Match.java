package de.fred4jupiter.fredbet.domain;

import java.time.LocalDateTime;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.springframework.format.annotation.DateTimeFormat;

import de.fred4jupiter.fredbet.util.DateUtils;

@Entity
@Table(name = "MATCHES")
public class Match {

	@Id
	@GeneratedValue
	@Column(name = "MATCH_ID")
	private Long id;
	
	private Country countryOne;

	private String teamNameOne;

	private Country countryTwo;

	private String teamNameTwo;

	@Enumerated(EnumType.STRING)
	@Column(name = "MATCH_GROUP")
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
	
	public Country getWinner() {
		if (!hasResultSet()) {
			return null;
		}
		
		if (isTeamOneWinner()) {
			return countryOne;
		}
		
		if (isTeamTwoWinner()) {
			return countryTwo;
		}
		
		return null;
	}
	
	public Country getLooser() {
		if (!hasResultSet()) {
			return null;
		}
		
		if (isTeamOneWinner()) {
			return countryTwo;
		}
		
		if (isTeamTwoWinner()) {
			return countryOne;
		}
		
		return null;
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
		builder.append(countryOne, match.countryOne);
		builder.append(countryTwo, match.countryTwo);
		builder.append(teamNameOne, match.teamNameOne);
		builder.append(teamNameTwo, match.teamNameTwo);
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
		builder.append(countryOne);
		builder.append(countryTwo);
		builder.append(teamNameOne);
		builder.append(teamNameTwo);
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
		builder.append("countryOne", countryOne);
		builder.append("countryTwo", countryTwo);
		builder.append("teamNameOne", teamNameOne);
		builder.append("teamNameTwo", teamNameTwo);
		builder.append("group", group);
		builder.append("goalsTeamOne", goalsTeamOne);
		builder.append("goalsTeamTwo", goalsTeamTwo);
		builder.append("kickOffDate", kickOffDate);
		builder.append("stadium", stadium);
		return builder.toString();
	}

	public Long getId() {
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

	public boolean isBetable() {
		return !hasStarted() && !hasResultSet();
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

	public Country getCountryOne() {
		return countryOne;
	}

	public void setCountryOne(Country countryOne) {
		this.countryOne = countryOne;
	}

	public Country getCountryTwo() {
		return countryTwo;
	}

	public void setCountryTwo(Country countryTwo) {
		this.countryTwo = countryTwo;
	}

	public boolean isFinal() {
		return Group.FINAL.equals(this.group);
	}
}
