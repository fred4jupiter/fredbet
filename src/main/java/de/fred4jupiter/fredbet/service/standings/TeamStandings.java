package de.fred4jupiter.fredbet.service.standings;

import de.fred4jupiter.fredbet.domain.Team;

import java.util.concurrent.atomic.AtomicInteger;

public class TeamStandings {

    private final String teamName;

    private final AtomicInteger numberOfMatches = new AtomicInteger();

    private final AtomicInteger numberOfWins = new AtomicInteger();

    private final AtomicInteger numberOfUndecided = new AtomicInteger();

    private final AtomicInteger numberOfLooses = new AtomicInteger();

    private final AtomicInteger numberOfGoals = new AtomicInteger();

    private final AtomicInteger numberOfGoalsAgainst = new AtomicInteger();

    private final AtomicInteger numberOfPoints = new AtomicInteger();

    public TeamStandings(String teamName) {
        this.teamName = teamName;
    }

    public void registerResultForTeam(Team team, Team opponent, boolean isWinner, boolean isUndecided) {
        numberOfMatches.incrementAndGet();
        if (isUndecided) {
            numberOfUndecided.incrementAndGet();
            numberOfPoints.addAndGet(1);
        } else {
            if (isWinner) {
                numberOfWins.incrementAndGet();
                numberOfPoints.addAndGet(3);
            } else {
                numberOfLooses.incrementAndGet();
            }
        }

        numberOfGoals.addAndGet(team.getGoals());
        numberOfGoalsAgainst.addAndGet(opponent.getGoals());
    }

    public String getTeamName() {
        return teamName;
    }

    public Integer getNumberOfMatches() {
        return numberOfMatches.get();
    }

    public Integer getNumberOfWins() {
        return numberOfWins.get();
    }

    public Integer getNumberOfUndecided() {
        return numberOfUndecided.get();
    }

    public Integer getNumberOfLooses() {
        return numberOfLooses.get();
    }

    public Integer getNumberOfGoals() {
        return numberOfGoals.get();
    }

    public Integer getNumberOfGoalsAgainst() {
        return numberOfGoalsAgainst.get();
    }

    public Integer getNumberOfGoalDifference() {
        return numberOfGoals.get() - numberOfGoalsAgainst.get();
    }

    public Integer getNumberOfPoints() {
        return numberOfPoints.get();
    }

    @Override
    public String toString() {
        return teamName +
                " | " +
                numberOfMatches +
                " | " +
                numberOfWins +
                " | " +
                numberOfUndecided +
                " | " +
                numberOfLooses +
                " | " +
                numberOfGoals +
                " | " +
                numberOfGoalsAgainst +
                " | " +
                getNumberOfGoalDifference() +
                " | " +
                numberOfPoints;
    }

    public String getCssClass(Integer rowCount) {
        if (rowCount == 1 || rowCount == 2) {
            return "success";
        }
        if (rowCount == 3) {
            return "warning";
        }
        return "";
    }
}
