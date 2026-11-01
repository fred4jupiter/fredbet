package de.fred4jupiter.fredbet.data;

import de.fred4jupiter.fredbet.domain.entity.Team;
import de.fred4jupiter.fredbet.match.MatchRepository;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Component
public class RandomValueGenerator {

    private final MatchRepository matchRepository;

    public RandomValueGenerator(MatchRepository matchRepository) {
        this.matchRepository = matchRepository;
    }

    public GoalResult generateGoalResult() {
        return new GoalResult(generateRandomValueInRange(1, 5), generateRandomValueInRange(1, 5));
    }

    public GoalResult generateGoalResult(int min, int max) {
        return new GoalResult(generateRandomValueInRange(min, max), generateRandomValueInRange(min, max));
    }

    private Integer generateRandomValueInRange(int min, int max) {
        return ThreadLocalRandom.current().nextInt(min, max + 1);
    }

    public Boolean generateRandomBoolean() {
        return ThreadLocalRandom.current().nextBoolean();
    }

    /**
     * Picks two different teams of the given teams randomly.
     */
    public TeamPair generateTeamPair(List<Team> teams) {
        if (teams.size() < 2) {
            throw new IllegalArgumentException("At least two teams are needed to generate a team pair!");
        }
        List<Team> pair = distinctRandomElements(teams, 2);
        return new TeamPair(pair.get(0), pair.get(1));
    }

    public TeamTriple generateTeamTriple() {
        List<Team> allTeams = matchRepository.getAllTeamsOfMatches();
        if (allTeams.isEmpty()) {
            throw new IllegalArgumentException("Could not create triple, because no matches found.");
        }

        if (allTeams.size() == 1) {
            Team team = allTeams.getFirst();
            return new TeamTriple(team, team, team);
        }

        if (allTeams.size() == 2) {
            return new TeamTriple(allTeams.getFirst(), allTeams.get(1), allTeams.get(1));
        }

        if (allTeams.size() == 3) {
            return new TeamTriple(allTeams.getFirst(), allTeams.get(1), allTeams.get(2));
        }

        List<Team> countries = distinctRandomElements(allTeams, 3);
        return new TeamTriple(countries.get(0), countries.get(1), countries.get(2));
    }

    <T> List<T> distinctRandomElements(List<T> list, int numberOfElements) {
        if (list.isEmpty()) {
            return Collections.emptyList();
        }

        if (list.size() < numberOfElements) {
            throw new IllegalArgumentException("Could not return " + numberOfElements + ", because there are only " + list.size() + " elements in list.");
        }

        ArrayList<T> tmpList = new ArrayList<>(list);
        Collections.shuffle(tmpList);
        return tmpList.subList(0, numberOfElements);
    }
}
