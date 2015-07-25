package de.fred4jupiter.fredbet.domain;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
public class Match {

	@Id
	private String id;

	private Team teamOne;

	private Team teamTwo;

	private String group;

	private Result result;

	public Match() {
		this.teamOne = new Team();
		this.teamTwo = new Team();
		this.result = new Result();
	}

	public Match(String teamNameOne, String teamNameTwo) {
		this(new Team(teamNameOne), new Team(teamNameTwo));
	}

	@PersistenceConstructor
	public Match(Team teamOne, Team teamTwo) {
		this.teamOne = teamOne;
		this.teamTwo = teamTwo;
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
		builder.append(result, match.result);

		return builder.isEquals();
	}

	@Override
	public int hashCode() {
		HashCodeBuilder builder = new HashCodeBuilder();
		builder.append(id);
		builder.append(teamOne);
		builder.append(teamTwo);
		builder.append(group);
		builder.append(result);
		return builder.toHashCode();
	}

	@Override
	public String toString() {
		ToStringBuilder builder = new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE);
		builder.append("id", id);
		builder.append("teamOne", teamOne);
		builder.append("teamTwo", teamTwo);
		builder.append("group", group);
		builder.append("result", result);
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

	public Result getResult() {
		return result;
	}

	public void setResult(Result result) {
		this.result = result;
	}

	public String getGroup() {
		return group;
	}

	public void setGroup(String group) {
		this.group = group;
	}
}
