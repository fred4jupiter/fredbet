package de.fred4jupiter.fredbet.web.ranking;

public class RankingCommand {

	private String username;

	private Integer points;

	public RankingCommand(String username, Integer points) {
		super();
		this.username = username;
		this.points = points;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public Integer getPoints() {
		return points;
	}

	public void setPoints(Integer points) {
		this.points = points;
	}
}
