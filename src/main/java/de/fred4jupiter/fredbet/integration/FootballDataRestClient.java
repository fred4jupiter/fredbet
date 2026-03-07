package de.fred4jupiter.fredbet.integration;

import de.fred4jupiter.fredbet.integration.model.FdCompetition;
import de.fred4jupiter.fredbet.integration.model.FdCompetitionList;
import de.fred4jupiter.fredbet.integration.model.FdMatch;
import de.fred4jupiter.fredbet.integration.model.FdMatches;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.Collections;
import java.util.List;

@Component
public class FootballDataRestClient {

    private final RestClient restClient;

    public FootballDataRestClient(RestClient restClient) {
        this.restClient = restClient;
    }

    public List<FdCompetition> fetchCompetitions() {
        FdCompetitionList fdCompetitionList = restClient.get().uri("/competitions/").retrieve().body(FdCompetitionList.class);
        return fdCompetitionList != null ? fdCompetitionList.competitions() : Collections.emptyList();
    }

    public List<FdMatch> fetchMatches(String competitionCode, int season) {
        FdMatches fdMatches = restClient.get().uri("/competitions/{competitionCode}/matches", competitionCode)
            .attribute("season", season).retrieve().body(FdMatches.class);
        return fdMatches != null ? fdMatches.matches() : Collections.emptyList();
    }
}
