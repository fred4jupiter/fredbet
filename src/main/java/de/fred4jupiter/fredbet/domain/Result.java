package de.fred4jupiter.fredbet.domain;

import org.springframework.data.mongodb.core.mapping.Document;

@Document
public class Result {

	private int goalsTeamOne;
	
	private int goalsTeamTwo;
}
