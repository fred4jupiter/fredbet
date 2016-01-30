package de.fred4jupiter.fredbet.domain;

public class TeamBuilder {

	private String name;

	private TeamBuilder() {

	}

	public static TeamBuilder create() {
		return new TeamBuilder();
	}

	public TeamBuilder withName(String name) {
		this.name = name;
		return this;
	}

	public Team build() {
		Team team = null;
		Country country = Country.fromName(name);
		if (country == null) {
			team = new Team(name);
		} else {
			team = new Team(country);
		}
		return team;
	}
}
