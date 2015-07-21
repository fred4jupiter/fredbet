package de.fred4jupiter.fredbet.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
public class Match {

	@Id
	private String id;
	
	private Team teamOne;
	
	private Team teamTwo;

	public Team getTeamOne() {
		return teamOne;
	}

	public void setTeamOne(Team teamOne) {
		this.teamOne = teamOne;
	}

	public Team getTeamTwo() {
		return teamTwo;
	}

	public void setTeamTwo(Team teamTwo) {
		this.teamTwo = teamTwo;
	}

	public String getId() {
		return id;
	}
}
