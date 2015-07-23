package de.fred4jupiter.fredbet.domain;

import org.springframework.data.annotation.Id;
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

	private int points;

	public Bet(String userName, Match match, Result result) {
		this.userName = userName;
		this.match = match;
		this.result = result;
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

	public int getPoints() {
		return points;
	}

	public void setPoints(int points) {
		this.points = points;
	}

}
