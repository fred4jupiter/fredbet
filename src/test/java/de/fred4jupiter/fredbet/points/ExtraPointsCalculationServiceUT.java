package de.fred4jupiter.fredbet.points;

import de.fred4jupiter.fredbet.common.UnitTest;
import de.fred4jupiter.fredbet.domain.Country;
import de.fred4jupiter.fredbet.domain.ExtraBet;
import de.fred4jupiter.fredbet.domain.Group;
import de.fred4jupiter.fredbet.domain.Match;
import de.fred4jupiter.fredbet.repository.ExtraBetRepository;
import de.fred4jupiter.fredbet.service.MatchGoalsChangedEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@UnitTest
public class ExtraPointsCalculationServiceUT {

    @InjectMocks
    private ExtraPointsCalculationService extraPointsCalculationService;

    @Mock
    private ExtraBetRepository extraBetRepository;

    @Mock
    private PointsConfigService pointsConfigService;

    @Mock
    private MatchGoalsChangedEvent matchGoalsChangedEvent;

    @Mock
    private PointsConfiguration pointsConfig;

    @Mock
    private ExtraPointsConfiguration extraPointsConfig;

    private Match match;

    private ExtraBet extraBet;

    @BeforeEach
    public void setup() {
        match = new Match();

        extraBet = new ExtraBet();
        extraBet.setUserName("Alfredo");
        extraBet.setFinalWinner(Country.GERMANY);
        extraBet.setSemiFinalWinner(Country.FRANCE);
        extraBet.setThirdFinalWinner(Country.SPAIN);
    }

    @Test
    public void noPointsOnOtherMatchThanFinalOrGameOfThird() {
        when(matchGoalsChangedEvent.getMatch()).thenReturn(match);

        match.setGroup(Group.GROUP_B);

        extraPointsCalculationService.onApplicationEvent(matchGoalsChangedEvent);

        assertThat(extraBet.getPointsOne()).isEqualTo(0);
        assertThat(extraBet.getPointsTwo()).isEqualTo(0);
        assertThat(extraBet.getPointsThree()).isEqualTo(0);
        assertThat(extraBet.getPoints()).isEqualTo(0);
    }

    @Test
    public void finalMatchButWrongBet() {
        when(matchGoalsChangedEvent.getMatch()).thenReturn(match);

        match.setGroup(Group.FINAL);
        match.getTeamOne().setCountry(Country.AFGHANISTAN);
        match.getTeamTwo().setCountry(Country.CUBA);

        when(extraBetRepository.findAll()).thenReturn(Collections.singletonList(extraBet));

        extraPointsCalculationService.onApplicationEvent(matchGoalsChangedEvent);

        assertThat(extraBet.getPointsOne()).isEqualTo(0);
        assertThat(extraBet.getPointsTwo()).isEqualTo(0);
        assertThat(extraBet.getPointsThree()).isEqualTo(0);
        assertThat(extraBet.getPoints()).isEqualTo(0);
    }

    @Test
    public void finalAndSemiFinalCorrect() {
        when(matchGoalsChangedEvent.getMatch()).thenReturn(match);

        match.setGroup(Group.FINAL);
        match.getTeamOne().setCountry(Country.GERMANY);
        match.getTeamTwo().setCountry(Country.FRANCE);
        match.setGoalsTeamOne(2);
        match.setGoalsTeamTwo(1);

        when(pointsConfigService.loadPointsConfig()).thenReturn(pointsConfig);
        when(pointsConfig.getExtraPointsConfig()).thenReturn(extraPointsConfig);
        when(extraBetRepository.findAll()).thenReturn(Collections.singletonList(extraBet));
        when(extraPointsConfig.getPointsFinalWinner()).thenReturn(10);
        when(extraPointsConfig.getPointsSemiFinalWinner()).thenReturn(5);

        extraPointsCalculationService.onApplicationEvent(matchGoalsChangedEvent);

        assertThat(extraBet.getPointsOne()).isEqualTo(10);
        assertThat(extraBet.getPointsTwo()).isEqualTo(5);
        assertThat(extraBet.getPointsThree()).isEqualTo(0);
        assertThat(extraBet.getPoints()).isEqualTo(15);

        verify(extraBetRepository).save(extraBet);
    }

    @Test
    public void gameOfThirdCorrect() {
        when(matchGoalsChangedEvent.getMatch()).thenReturn(match);

        extraBet.setThirdFinalWinner(Country.GERMANY);

        match.setGroup(Group.GAME_FOR_THIRD);
        match.getTeamOne().setCountry(Country.GERMANY);
        match.getTeamTwo().setCountry(Country.FRANCE);
        match.setGoalsTeamOne(2);
        match.setGoalsTeamTwo(1);

        when(pointsConfigService.loadPointsConfig()).thenReturn(pointsConfig);
        when(pointsConfig.getExtraPointsConfig()).thenReturn(extraPointsConfig);
        when(extraBetRepository.findAll()).thenReturn(Collections.singletonList(extraBet));
        when(extraPointsConfig.getPointsThirdFinalWinner()).thenReturn(2);

        extraPointsCalculationService.onApplicationEvent(matchGoalsChangedEvent);

        assertThat(extraBet.getPointsOne()).isEqualTo(0);
        assertThat(extraBet.getPointsTwo()).isEqualTo(0);
        assertThat(extraBet.getPointsThree()).isEqualTo(2);
        assertThat(extraBet.getPoints()).isEqualTo(2);

        verify(extraBetRepository).save(extraBet);
    }
}
