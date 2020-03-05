package de.fred4jupiter.fredbet.service.grouppoints;

import de.fred4jupiter.fredbet.domain.Team;

import java.util.concurrent.atomic.AtomicInteger;

public class GroupTeamPoints {

    private final String teamName;

    private final AtomicInteger numberOfMatches = new AtomicInteger();

    private final AtomicInteger numberOfWins = new AtomicInteger();

    private final AtomicInteger numberOfUndecided = new AtomicInteger();

    private final AtomicInteger numberOfDefeats = new AtomicInteger(); // Verloren

    private final AtomicInteger numberOfGoals = new AtomicInteger();

    private final AtomicInteger numberOfSheets = new AtomicInteger(); // Gegentore

    private final AtomicInteger numberOfGoalDifference = new AtomicInteger();

    private final AtomicInteger numberOfPoints = new AtomicInteger();

    public GroupTeamPoints(String teamName) {
        this.teamName = teamName;
    }

    public void registerResultForTeam(Team team, Integer goalDifference, Team opponent, boolean isWinner, boolean isUndecided) {
        numberOfMatches.incrementAndGet();
        if (isWinner) {
            numberOfWins.incrementAndGet();
        } else {
            numberOfDefeats.incrementAndGet();
        }

        if (isUndecided) {
            numberOfUndecided.incrementAndGet();
        }

        numberOfGoals.addAndGet(team.getGoals());
        numberOfSheets.addAndGet(opponent.getGoals());

        numberOfGoalDifference.addAndGet(goalDifference);

        // calculate points
        if (isWinner) {
            numberOfPoints.addAndGet(3);
        } else if (isUndecided) {
            numberOfPoints.addAndGet(1);
        }
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

    public Integer getNumberOfDefeats() {
        return numberOfDefeats.get();
    }

    public Integer getNumberOfGoals() {
        return numberOfGoals.get();
    }

    public Integer getNumberOfSheets() {
        return numberOfSheets.get();
    }

    public Integer getNumberOfGoalDifference() {
        return numberOfGoalDifference.get();
    }

    public Integer getNumberOfPoints() {
        return numberOfPoints.get();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(teamName);
        sb.append(" | ");
        sb.append(numberOfMatches);
        sb.append(" | ");
        sb.append(numberOfWins);
        sb.append(" | ");
        sb.append(numberOfUndecided);
        sb.append(" | ");
        sb.append(numberOfDefeats);
        sb.append(" | ");
        sb.append(numberOfGoals);
        sb.append(" | ");
        sb.append(numberOfSheets);
        sb.append(" | ");
        sb.append(numberOfGoalDifference);
        sb.append(" | ");
        sb.append(numberOfPoints);
        return sb.toString();
    }
}
