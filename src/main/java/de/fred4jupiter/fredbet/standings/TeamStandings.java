package de.fred4jupiter.fredbet.standings;

import de.fred4jupiter.fredbet.domain.entity.Team;

import java.util.concurrent.atomic.AtomicInteger;

public class TeamStandings {

    private final Team team;

    private final AtomicInteger numberOfMatches = new AtomicInteger();

    private final AtomicInteger numberOfWins = new AtomicInteger();

    private final AtomicInteger numberOfUndecided = new AtomicInteger();

    private final AtomicInteger numberOfLooses = new AtomicInteger();

    private final AtomicInteger numberOfGoals = new AtomicInteger();

    private final AtomicInteger numberOfGoalsAgainst = new AtomicInteger();

    private final AtomicInteger numberOfPoints = new AtomicInteger();

    public TeamStandings(Team team) {
        this.team = team;
    }

    public void addMatch(Integer scored, Integer conceded) {
        numberOfMatches.incrementAndGet();
        numberOfGoals.addAndGet(scored);
        numberOfGoalsAgainst.addAndGet(conceded);

        if (scored > conceded) {
            numberOfWins.incrementAndGet();
            numberOfPoints.addAndGet(3);
        } else if (scored.equals(conceded)) {
            numberOfUndecided.incrementAndGet();
            numberOfPoints.addAndGet(1);
        } else {
            numberOfLooses.incrementAndGet();
        }
    }

    public Team getTeam() {
        return team;
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

//    @Override
//    public String toString() {
//        return teamName +
//                " | " +
//                numberOfMatches +
//                " | " +
//                numberOfWins +
//                " | " +
//                numberOfUndecided +
//                " | " +
//                numberOfLooses +
//                " | " +
//                numberOfGoals +
//                " | " +
//                numberOfGoalsAgainst +
//                " | " +
//                getNumberOfGoalDifference() +
//                " | " +
//                numberOfPoints;
//    }

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
