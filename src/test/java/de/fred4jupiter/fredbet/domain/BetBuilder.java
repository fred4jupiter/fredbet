package de.fred4jupiter.fredbet.domain;

public final class BetBuilder {

	private final Bet bet;
	
	private BetBuilder() {
		this.bet = new Bet();
	}

	public static BetBuilder create() {
		return new BetBuilder();
	}
	
	public BetBuilder withMatch(Match match) {
		this.bet.setMatch(match);
		return this;
	}
	
	public BetBuilder withGoals(Integer goalsTeamOne, Integer goalsTeamTwo) {
		bet.setGoalsTeamOne(goalsTeamOne);
		bet.setGoalsTeamTwo(goalsTeamTwo);
		return this;
	}
	
	public BetBuilder withUsername(String userName) {
		bet.setUserName(userName);
		return this;
	}
	
	public BetBuilder withJoker(boolean joker) {
		bet.setJoker(joker);
		return this;
	}
	
	public Bet build() {
		return bet;
	}
}
