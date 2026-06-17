package de.fred4jupiter.fredbet.points;

import de.fred4jupiter.fredbet.betting.repository.BetRepository;
import de.fred4jupiter.fredbet.common.UnitTest;
import de.fred4jupiter.fredbet.domain.Group;
import de.fred4jupiter.fredbet.domain.entity.Bet;
import de.fred4jupiter.fredbet.domain.entity.Match;
import de.fred4jupiter.fredbet.match.MatchGoalsChangedEvent;
import de.fred4jupiter.fredbet.match.MatchRepository;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@UnitTest
public class PointsCalculationServiceUT {

    @InjectMocks
    private PointsCalculationService pointsCalculationService;

    @Mock
    private BetRepository betRepository;

    @Mock
    private PointsCalculationUtil pointsCalculationUtil;

    @Mock
    private MatchRepository matchRepository;

    @Mock
    private MatchGoalsChangedEvent matchGoalsChangedEvent;

    @Test
    public void onApplicationEventCalculatesAndSavesPointsForFinishedMatch() {
        Match match = new Match();
        match.setGoalsTeamOne(2);
        match.setGoalsTeamTwo(1);
        Bet bet = new Bet();
        bet.setUserName("alfredo");
        when(matchGoalsChangedEvent.match()).thenReturn(match);
        when(betRepository.findByMatch(match)).thenReturn(List.of(bet));
        when(pointsCalculationUtil.calculatePointsFor(match, bet)).thenReturn(3);

        pointsCalculationService.onApplicationEvent(matchGoalsChangedEvent);

        assertThat(bet.getPoints()).isEqualTo(3);
        verify(betRepository).saveAll(List.of(bet));
    }

    @Test
    public void calculatePointsForClearsPointsWhenMatchHasNoResult() {
        Match match = new Match();
        Bet bet = new Bet();
        bet.setPoints(5);
        when(betRepository.findByMatch(match)).thenReturn(List.of(bet));

        pointsCalculationService.calculatePointsFor(match);

        assertThat(bet.getPoints()).isZero();
        verify(betRepository).saveAll(List.of(bet));
    }

    @Test
    public void onPointsConfigurationChangedEventRecalculatesAllFinishedMatches() {
        Match one = new Match();
        one.setGoalsTeamOne(1);
        one.setGoalsTeamTwo(0);
        one.setGroup(Group.GROUP_A);
        Match two = new Match();
        two.setGoalsTeamOne(0);
        two.setGoalsTeamTwo(0);
        two.setGroup(Group.FINAL);
        Bet betOne = new Bet();
        Bet betTwo = new Bet();

        when(matchRepository.findAllWithMatchResult()).thenReturn(List.of(one, two));
        when(betRepository.findByMatch(one)).thenReturn(List.of(betOne));
        when(betRepository.findByMatch(two)).thenReturn(List.of(betTwo));
        when(pointsCalculationUtil.calculatePointsFor(one, betOne)).thenReturn(2);
        when(pointsCalculationUtil.calculatePointsFor(two, betTwo)).thenReturn(4);

        pointsCalculationService.onPointsConfigurationChangedEvent(new PointsConfigurationChangedEvent());

        assertThat(betOne.getPoints()).isEqualTo(2);
        assertThat(betTwo.getPoints()).isEqualTo(4);
    }
}

