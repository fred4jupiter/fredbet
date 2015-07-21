package de.fred4jupiter.fredbet.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
public class Result {

	@Id
	private String id;
	
	private int goalsTeamOne;
	
	private int goalsTeamTwo;

	public int getGoalsTeamOne() {
		return goalsTeamOne;
	}

	public void setGoalsTeamOne(int goalsTeamOne) {
		this.goalsTeamOne = goalsTeamOne;
	}

	public int getGoalsTeamTwo() {
		return goalsTeamTwo;
	}

	public void setGoalsTeamTwo(int goalsTeamTwo) {
		this.goalsTeamTwo = goalsTeamTwo;
	}

	public String getId() {
		return id;
	}
}
