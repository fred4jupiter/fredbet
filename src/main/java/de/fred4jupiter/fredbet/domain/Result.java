package de.fred4jupiter.fredbet.domain;

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

	public Integer getGoalsTeamOne() {
		return goalsTeamOne;
	}

	public Integer getGoalsTeamTwo() {
		return goalsTeamTwo;
	}

	public String getId() {
		return id;
	}
}
