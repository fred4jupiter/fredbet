package de.fred4jupiter.fredbet.domain;

public final class BetBuilder {
    private String userName;
    private Match match;
    private Integer goalsTeamOne;
    private Integer goalsTeamTwo;
    private Integer points;
    private boolean penaltyWinnerOne;
    private boolean joker;

    private BetBuilder() {
    }

    public static BetBuilder create() {
        return new BetBuilder();
    }

    public BetBuilder withUserName(String userName) {
        this.userName = userName;
        return this;
    }

    public BetBuilder withMatch(Match match) {
        this.match = match;
        return this;
    }

    public BetBuilder withGoals(Integer goalsTeamOne, Integer goalsTeamTwo) {
        this.goalsTeamOne = goalsTeamOne;
        this.goalsTeamTwo = goalsTeamTwo;
        return this;
    }

    public BetBuilder withPoints(Integer points) {
        this.points = points;
        return this;
    }

    public BetBuilder withPenaltyWinnerOne(boolean penaltyWinnerOne) {
        this.penaltyWinnerOne = penaltyWinnerOne;
        return this;
    }

    public BetBuilder withJoker(boolean joker) {
        this.joker = joker;
        return this;
    }

    public Bet build() {
        Bet bet = new Bet();
        bet.setUserName(userName);
        bet.setMatch(match);
        bet.setGoalsTeamOne(goalsTeamOne);
        bet.setGoalsTeamTwo(goalsTeamTwo);
        bet.setPoints(points);
        bet.setPenaltyWinnerOne(penaltyWinnerOne);
        bet.setJoker(joker);
        return bet;
    }
}
