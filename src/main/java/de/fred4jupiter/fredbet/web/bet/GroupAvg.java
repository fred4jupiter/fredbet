package de.fred4jupiter.fredbet.web.bet;

public class GroupAvg {

    private final int avgGoalsTeamOne;

    private final int avgGoalsTeamTwo;

    public GroupAvg(Double avgGoalsTeamOne, Double avgGoalsTeamTwo) {
        this.avgGoalsTeamOne = avgGoalsTeamOne.intValue();
        this.avgGoalsTeamTwo = avgGoalsTeamTwo.intValue();
    }

    public int getAvgGoalsTeamOne() {
        return avgGoalsTeamOne;
    }

    public int getAvgGoalsTeamTwo() {
        return avgGoalsTeamTwo;
    }

}
