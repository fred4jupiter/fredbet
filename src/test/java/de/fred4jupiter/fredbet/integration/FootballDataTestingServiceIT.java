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

    @Value("classpath:/football-data-json-test/penaltyShootout.json")
    private Resource penaltyShootoutResource;

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
        assertThat(match.hasResultSet()).isTrue();
        assertThat(match.getGoalsTeamOne()).isEqualTo(5);
        assertThat(match.getGoalsTeamTwo()).isEqualTo(1);
        assertThat(match.isTeamOneWinner()).isTrue();
        assertThat(match.isTeamTwoWinner()).isFalse();
    }

    @Test
    void importPenaltyShootoutMatch() throws IOException {
        assertThat(penaltyShootoutResource).isNotNull();

        footballDataTestingService.syncDataFromJson(penaltyShootoutResource.getContentAsByteArray());

        List<Match> matches = matchService.findAll();
        assertThat(matches).hasSize(1);
        Match match = matches.getFirst();
        assertThat(match).isNotNull();
        assertThat(match.getExternalId()).isEqualTo("428788");
        assertThat(match.isGroupMatch()).isFalse();
        assertThat(match.isGroup(Group.ROUND_OF_SIXTEEN)).isTrue();
        assertThat(match.isUndecidedResult()).isTrue();
        assertThat(match.hasResultSet()).isTrue();

        assertThat(match.getGoalsTeamOne()).isEqualTo(0);
        assertThat(match.getGoalsTeamTwo()).isEqualTo(0);
        assertThat(match.isPenaltyWinnerOne()).isTrue();
    }

}
