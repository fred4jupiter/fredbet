package de.fred4jupiter.fredbet.integration;

import de.fred4jupiter.fredbet.common.IntegrationTest;
import de.fred4jupiter.fredbet.integration.model.FdCompetition;
import de.fred4jupiter.fredbet.integration.model.FdMatch;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@IntegrationTest
public class FootballDataRestClientMT {

    private static final Logger LOG = LoggerFactory.getLogger(FootballDataRestClientMT.class);

    @Autowired
    private FootballDataRestClient footballDataRestClient;

    @Test
    void fetchCompetitions() {
        List<FdCompetition> fdCompetitions = footballDataRestClient.fetchCompetitions().competitions();
        assertThat(fdCompetitions).isNotNull();
        assertThat(fdCompetitions).isNotEmpty();

        fdCompetitions.forEach(competition -> LOG.debug("competition: {}, seasonYear: {}", competition, competition.currentSeason().getSeasonYear()));
    }

    @Test
    void fetchMatches() {
        List<FdMatch> matches = footballDataRestClient.fetchMatches("WC", 2026).matches();
        assertThat(matches).isNotNull();
        assertThat(matches).isNotEmpty();

        matches.forEach(match -> LOG.debug("match: {}", match));
    }

    @Test
    void fetchMatchesForOtherCompetitionAndSeason() {
        List<FdMatch> matches = footballDataRestClient.fetchMatches("EC", 2024).matches();
        assertThat(matches).isNotNull();
        assertThat(matches).isNotEmpty();

        matches.forEach(match -> LOG.debug("match: {}", match));
    }
}
