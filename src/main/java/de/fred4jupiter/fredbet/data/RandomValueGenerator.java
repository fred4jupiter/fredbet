package de.fred4jupiter.fredbet.data;

import de.fred4jupiter.fredbet.domain.Country;
import de.fred4jupiter.fredbet.match.MatchRepository;
import de.fred4jupiter.fredbet.teambundle.TeamBundle;
import de.fred4jupiter.fredbet.teambundle.TeamBundleProvider;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

@Component
public class RandomValueGenerator {

    private final MatchRepository matchRepository;

    private final TeamBundleProvider teamBundleProvider;

    public RandomValueGenerator(MatchRepository matchRepository, TeamBundleProvider teamBundleProvider) {
        this.matchRepository = matchRepository;
        this.teamBundleProvider = teamBundleProvider;
    }

    public GoalResult generateGoalResult() {
        return new GoalResult(generateRandomValueInRange(1, 5), generateRandomValueInRange(1, 5));
    }

    public GoalResult generateGoalResult(int min, int max) {
        return new GoalResult(generateRandomValueInRange(min, max), generateRandomValueInRange(min, max));
    }

    private Integer generateRandomValueInRange(int min, int max) {
        Random rn = new Random();
        int range = max - min + 1;
        return rn.nextInt(range) + min;
    }

    public Boolean generateRandomBoolean() {
        return Math.random() < 0.5;
    }

    public TeamPair generateTeamPair(TeamBundle teamBundle) {
        List<Country> teams = teamBundleProvider.getTeams(teamBundle);
        List<Country> countries = distinctRandomElements(teams, 2);
        return new TeamPair(countries.get(0), countries.get(1));
    }

    public TeamTriple generateTeamTriple() {
        List<Country> allCountriesOfMatches = matchRepository.getAllCountriesOfMatches();
        if (allCountriesOfMatches.isEmpty()) {
            throw new IllegalArgumentException("Could not create triple, because not matches found.");
        }

        if (allCountriesOfMatches.size() == 1) {
            Country country = allCountriesOfMatches.getFirst();
            return new TeamTriple(country, country, country);
        }

        if (allCountriesOfMatches.size() == 2) {
            return new TeamTriple(allCountriesOfMatches.getFirst(), allCountriesOfMatches.get(1), allCountriesOfMatches.get(1));
        }

        if (allCountriesOfMatches.size() == 3) {
            return new TeamTriple(allCountriesOfMatches.getFirst(), allCountriesOfMatches.get(1), allCountriesOfMatches.get(2));
        }

        List<Country> countries = distinctRandomElements(allCountriesOfMatches, 3);
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
