package de.fred4jupiter.fredbet.domain;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
public class Bet {

	@Id
	private String id;

	@Indexed(unique = true)
	private String userName;

	@DBRef
	private Match match;

	private Integer goalsTeamOne;

	private Integer goalsTeamTwo;

	private Integer points;

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
		Bet bet = (Bet) obj;
		EqualsBuilder builder = new EqualsBuilder();
		builder.append(id, bet.id);
		builder.append(userName, bet.userName);
		builder.append(match, bet.match);
		builder.append(goalsTeamOne, bet.goalsTeamOne);
		builder.append(goalsTeamTwo, bet.goalsTeamTwo);
		builder.append(points, bet.points);

		return builder.isEquals();
	}

	@Override
	public int hashCode() {
		HashCodeBuilder builder = new HashCodeBuilder();
		builder.append(id);
		builder.append(userName);
		builder.append(match);
		builder.append(goalsTeamOne);
		builder.append(goalsTeamTwo);
		builder.append(points);
		return builder.toHashCode();
	}

	@Override
	public String toString() {
		ToStringBuilder builder = new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE);
		builder.append("id", id);
		builder.append("userName", userName);
		builder.append("match", match);
		builder.append("goalsTeamOne", goalsTeamOne);
		builder.append("goalsTeamTwo", goalsTeamTwo);
		builder.append("points", points);
		return builder.toString();
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

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public void setMatch(Match match) {
		this.match = match;
	}

	public String getId() {
		return id;
	}

	public String getUserName() {
		return userName;
	}

	public Match getMatch() {
		return match;
	}

	public Integer getPoints() {
		return points;
	}

	public void setPoints(Integer points) {
		this.points = points;
	}

}
