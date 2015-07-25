package de.fred4jupiter.fredbet.domain;

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

	private Result result = new Result();

	public Match(String teamNameOne, String teamNameTwo) {
		this(new Team(teamNameOne), new Team(teamNameTwo));
	}

	@PersistenceConstructor
	public Match(Team teamOne, Team teamTwo) {
		this.teamOne = teamOne;
		this.teamTwo = teamTwo;
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
