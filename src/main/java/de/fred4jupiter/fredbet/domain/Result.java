package de.fred4jupiter.fredbet.domain;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
public class Result {

	@Id
	private String id;

	private Integer goalsTeamOne;

	private Integer goalsTeamTwo;

	public Result() {

	}

	@PersistenceConstructor
	public Result(Integer goalsTeamOne, Integer goalsTeamTwo) {
		this.goalsTeamOne = goalsTeamOne;
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
		Result result = (Result) obj;
		EqualsBuilder builder = new EqualsBuilder();
		builder.append(id, result.id);
		builder.append(goalsTeamOne, result.goalsTeamOne);
		builder.append(goalsTeamTwo, result.goalsTeamTwo);

		return builder.isEquals();
	}

	@Override
	public int hashCode() {
		HashCodeBuilder builder = new HashCodeBuilder();
		builder.append(id);
		builder.append(goalsTeamOne);
		builder.append(goalsTeamTwo);
		return builder.toHashCode();
	}

	@Override
	public String toString() {
		ToStringBuilder builder = new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE);
		builder.append("id", id);
		builder.append("goalsTeamOne", goalsTeamOne);
		builder.append("goalsTeamTwo", goalsTeamTwo);
		return builder.toString();
	}

	public Integer getGoalsTeamOne() {
		return goalsTeamOne;
	}

	public Integer getGoalsTeamTwo() {
		return goalsTeamTwo;
	}

	public String getId() {
		return id;
	}

	public void setGoalsTeamOne(Integer goalsTeamOne) {
		this.goalsTeamOne = goalsTeamOne;
	}

	public void setGoalsTeamTwo(Integer goalsTeamTwo) {
		this.goalsTeamTwo = goalsTeamTwo;
	}
}
