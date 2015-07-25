package de.fred4jupiter.fredbet.domain;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceConstructor;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
public class Bet {

	@Id
	private String id;

	@Indexed
	private String userName;

	@DBRef
	private Match match;

	private Result result;

	private Integer points;

	@PersistenceConstructor
	public Bet(String userName, Match match, Result result) {
		this.userName = userName;
		this.match = match;
		this.result = result;
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
		Bet bet = (Bet) obj;
		EqualsBuilder builder = new EqualsBuilder();
		builder.append(id, bet.id);
		builder.append(userName, bet.userName);
		builder.append(match, bet.match);
		builder.append(result, bet.result);
		builder.append(points, bet.points);

		return builder.isEquals();
	}

	@Override
	public int hashCode() {
		HashCodeBuilder builder = new HashCodeBuilder();
		builder.append(id);
		builder.append(userName);
		builder.append(match);
		builder.append(result);
		builder.append(points);
		return builder.toHashCode();
	}

	@Override
	public String toString() {
		ToStringBuilder builder = new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE);
		builder.append("id", id);
		builder.append("userName", userName);
		builder.append("match", match);
		builder.append("result", result);
		builder.append("points", points);
		return builder.toString();
	}
	
	public Result getResult() {
		return result;
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
