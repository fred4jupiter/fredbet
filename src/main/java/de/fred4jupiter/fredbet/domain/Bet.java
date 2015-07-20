package de.fred4jupiter.fredbet.domain;

import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
public class Bet {

	@DBRef
	private Match match;
	
	private int points;
}
