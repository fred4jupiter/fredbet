package de.fred4jupiter.fredbet.integration;

import de.fred4jupiter.fredbet.common.TransactionalIntegrationTest;
import de.fred4jupiter.fredbet.domain.Group;
import de.fred4jupiter.fredbet.domain.entity.Match;
import de.fred4jupiter.fredbet.match.MatchService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@TransactionalIntegrationTest
public class FootballDataTestingServiceIT {

    @Autowired
    private FootballDataTestingService footballDataTestingService;

    @Value("classpath:/football-data-json-test/one_match.json")
    private Resource oneMatchResource;

    @Autowired
    private MatchService matchService;

    @Test
    void importOneMatch() throws IOException {
        assertThat(oneMatchResource).isNotNull();

        footballDataTestingService.syncDataFromJson(oneMatchResource.getContentAsByteArray());

        List<Match> matches = matchService.findAll();
        assertThat(matches).hasSize(1);
        Match match = matches.getFirst();
        assertThat(match).isNotNull();
        assertThat(match.getExternalId()).isEqualTo("428747");
        assertThat(match.isGroupMatch()).isTrue();
        assertThat(match.isGroup(Group.GROUP_A)).isTrue();
        assertThat(match.getTeamOne().getGoals()).isEqualTo(5);
        assertThat(match.getTeamTwo().getGoals()).isEqualTo(1);
        assertThat(match.isTeamOneWinner()).isTrue();
        assertThat(match.isTeamTwoWinner()).isFalse();
    }
}
