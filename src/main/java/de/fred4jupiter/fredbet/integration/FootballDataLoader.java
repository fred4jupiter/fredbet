package de.fred4jupiter.fredbet.integration;

import de.fred4jupiter.fredbet.integration.model.FdCompetition;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class FootballDataLoader {

    private final FootballDataRestClient footballDataRestClient;

    FootballDataLoader(FootballDataRestClient footballDataRestClient) {
        this.footballDataRestClient = footballDataRestClient;
    }

    public List<Competition> loadCompetitions() {
        List<FdCompetition> fdCompetitions = footballDataRestClient.fetchCompetitions();
        return fdCompetitions.stream()
            .filter(comp -> "EC".equalsIgnoreCase(comp.code()) || "WC".equalsIgnoreCase(comp.code()) )
            .map(fdCompetition -> new Competition(fdCompetition.id(), fdCompetition.name(),
                fdCompetition.code(), fdCompetition.currentSeason().getSeasonYear()))
            .toList();
    }
}
