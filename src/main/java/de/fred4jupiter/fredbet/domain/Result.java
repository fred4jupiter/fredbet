package de.fred4jupiter.fredbet.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
public class Result {

	@Id
	private String id;

	private int goalsTeamOne;

	private int goalsTeamTwo;

	public Result(int goalsTeamOne, int goalsTeamTwo) {
		this.goalsTeamOne = goalsTeamOne;
		this.goalsTeamTwo = goalsTeamTwo;
	}

	public int getGoalsTeamOne() {
		return goalsTeamOne;
	}

	public int getGoalsTeamTwo() {
		return goalsTeamTwo;
	}

	public String getId() {
		return id;
	}
}
