package de.fred4jupiter.fredbet.web.matches;

import de.fred4jupiter.fredbet.betting.BettingService;
import de.fred4jupiter.fredbet.common.UnitTest;
import de.fred4jupiter.fredbet.domain.entity.Bet;
import de.fred4jupiter.fredbet.domain.entity.Match;
import de.fred4jupiter.fredbet.match.MatchService;
import de.fred4jupiter.fredbet.security.SecurityService;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@UnitTest
public class MatchCommandMapperUT {

    @Mock
    private BettingService bettingService;

    @Mock
    private SecurityService securityService;

    @Test
    public void findMatchesMapsExistingBetToMatchCommand() {
        Match match = new Match();
        match.setGoalsTeamOne(1);
        match.setGoalsTeamTwo(0);
        Bet bet = new Bet();
        bet.setMatch(match);
        bet.setGoalsTeamOne(1);
        bet.setGoalsTeamTwo(0);

        MatchService matchService = org.mockito.Mockito.mock(MatchService.class);
        when(securityService.getCurrentUserName()).thenReturn("alfredo");
        when(bettingService.findAllByUsername("alfredo")).thenReturn(List.of(bet));

        MatchCommandMapper matchCommandMapper = new MatchCommandMapper(bettingService, matchService, null, securityService);
        List<MatchCommand> commands = matchCommandMapper.findMatches((username, service) -> {
            assertThat(service).isSameAs(matchService);
            assertThat(username).isEqualTo("alfredo");
            return List.of(match);
        });

        assertThat(commands).hasSize(1);
        assertThat(commands.getFirst().getBet()).isSameAs(bet);
        assertThat(commands.getFirst().getMatch()).isSameAs(match);
    }

    @Test
    public void findMatchesReturnsCommandsWithoutBetsWhenUserHasNoBets() {
        Match match = new Match();
        MatchService matchService = org.mockito.Mockito.mock(MatchService.class);
        when(securityService.getCurrentUserName()).thenReturn("alfredo");
        when(bettingService.findAllByUsername("alfredo")).thenReturn(List.of());

        MatchCommandMapper matchCommandMapper = new MatchCommandMapper(bettingService, matchService, null, securityService);
        List<MatchCommand> commands = matchCommandMapper.findMatches(service -> {
            assertThat(service).isSameAs(matchService);
            return List.of(match);
        });

        assertThat(commands).hasSize(1);
        assertThat(commands.getFirst().getBet()).isNull();
    }
}

