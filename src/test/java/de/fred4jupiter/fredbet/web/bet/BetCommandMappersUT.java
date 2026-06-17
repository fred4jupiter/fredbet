package de.fred4jupiter.fredbet.web.bet;

import de.fred4jupiter.fredbet.betting.BettingService;
import de.fred4jupiter.fredbet.common.UnitTest;
import de.fred4jupiter.fredbet.domain.Country;
import de.fred4jupiter.fredbet.domain.entity.Bet;
import de.fred4jupiter.fredbet.domain.entity.ExtraBet;
import de.fred4jupiter.fredbet.domain.entity.Match;
import de.fred4jupiter.fredbet.match.MatchService;
import de.fred4jupiter.fredbet.web.WebMessageUtil;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@UnitTest
public class BetCommandMappersUT {

    @Mock
    private MatchService matchService;

    @Mock
    private BettingService bettingService;

    @Mock
    private WebMessageUtil webMessageUtil;

    @Test
    public void extraBetCommandMapperCopiesExtraBetAndFinalMatch() {
        ExtraBet extraBet = new ExtraBet();
        extraBet.setUserName("alfredo");
        extraBet.setFinalWinner(Country.GERMANY);
        extraBet.setSemiFinalWinner(Country.FRANCE);
        extraBet.setThirdFinalWinner(Country.SPAIN);
        extraBet.setPointsOne(10);
        extraBet.setPointsTwo(5);
        extraBet.setPointsThree(2);
        Match finalMatch = new Match();
        when(matchService.findFinalMatch()).thenReturn(Optional.of(finalMatch));

        ExtraBetCommand command = new ExtraBetCommandMapper(matchService).toExtraBetCommand(extraBet);

        assertThat(command.getFinalWinner()).isEqualTo(Country.GERMANY);
        assertThat(command.getSemiFinalWinner()).isEqualTo(Country.FRANCE);
        assertThat(command.getThirdFinalWinner()).isEqualTo(Country.SPAIN);
        assertThat(command.getFinalMatch()).isSameAs(finalMatch);
        assertThat(command.getPoints()).isEqualTo(17);
    }

    @Test
    public void allBetsCommandMapperReturnsNullForMissingMatch() {
        when(matchService.findMatchById(1L)).thenReturn(null);

        AllBetsCommand command = new AllBetsCommandMapper(matchService, bettingService, webMessageUtil).findAllBetsForMatchId(1L);

        assertThat(command).isNull();
    }

    @Test
    public void allBetsCommandMapperCreatesCommandForExistingMatch() {
        Match match = new Match();
        Bet bet = new Bet();
        bet.setGoalsTeamOne(2);
        bet.setGoalsTeamTwo(1);
        when(matchService.findMatchById(3L)).thenReturn(match);
        when(bettingService.findAllBetsForMatchId(3L)).thenReturn(List.of(bet));

        AllBetsCommand command = new AllBetsCommandMapper(matchService, bettingService, webMessageUtil).findAllBetsForMatchId(3L);

        assertThat(command).isNotNull();
        assertThat(command.getMatch()).isSameAs(match);
        assertThat(command.getAllBetsForMatch()).containsExactly(bet);
    }
}

