package de.fred4jupiter.fredbet.integration;

import de.fred4jupiter.fredbet.integration.model.FdCompetitionList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
public class FootballDataLoader {

    private static final Logger LOG = LoggerFactory.getLogger(FootballDataLoader.class);

    private final FootballDataRestClient footballDataRestClient;

    FootballDataLoader(FootballDataRestClient footballDataRestClient) {
        this.footballDataRestClient = footballDataRestClient;
    }

    public List<Competition> loadCompetitions() {
        FdCompetitionList fdCompetitionList = footballDataRestClient.fetchCompetitions();

        if (fdCompetitionList == null || fdCompetitionList.competitions() == null || fdCompetitionList.competitions().isEmpty()) {
            LOG.warn("No competitions found from football data api!");
            return Collections.emptyList();
        }

        return fdCompetitionList.competitions().stream()
            .filter(comp -> "EC".equalsIgnoreCase(comp.code()) || "WC".equalsIgnoreCase(comp.code()))
            .map(fdCompetition -> new Competition(fdCompetition.id(), fdCompetition.name(),
                fdCompetition.code(), fdCompetition.currentSeason().getSeasonYear()))
            .toList();
    }
}
