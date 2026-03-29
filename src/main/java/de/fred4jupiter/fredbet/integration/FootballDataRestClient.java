package de.fred4jupiter.fredbet.integration;

import de.fred4jupiter.fredbet.integration.model.FdCompetitionList;
import de.fred4jupiter.fredbet.integration.model.FdMatches;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

@Component
class FootballDataRestClient {

    private final RestClient restClient;

    public FootballDataRestClient(RestClient restClient) {
        this.restClient = restClient;
    }

    public FdCompetitionList fetchCompetitions() {
        try {
            return restClient.get().uri("/competitions/").retrieve().body(FdCompetitionList.class);
        } catch (HttpClientErrorException e) {
            throw new FootballDataException(e.getMessage(), e);
        }
    }

    public FdMatches fetchMatches(Competition competition) {
        try {
            return restClient.get().uri("/competitions/{competitionCode}/matches", competition.code())
                .attribute("season", competition.seasonYear()).retrieve().body(FdMatches.class);
        } catch (HttpClientErrorException e) {
            throw new FootballDataException(e.getMessage(), e);
        }
    }
}
