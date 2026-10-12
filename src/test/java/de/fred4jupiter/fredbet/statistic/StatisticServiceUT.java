package de.fred4jupiter.fredbet.statistic;

import de.fred4jupiter.fredbet.betting.repository.ExtraBetRepository;
import de.fred4jupiter.fredbet.common.UnitTest;
import de.fred4jupiter.fredbet.domain.Country;
import de.fred4jupiter.fredbet.domain.entity.Team;
import de.fred4jupiter.fredbet.team.TeamService;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@UnitTest
public class StatisticServiceUT {

    @Mock
    private StatisticRepository statisticRepository;

    @Mock
    private ExtraBetRepository extraBetRepository;

    @Mock
    private TeamService teamService;

    @InjectMocks
    private StatisticService statisticService;

    @Test
    public void createStatistic_emptyRepository_returnsEmptyList() {
        when(statisticRepository.createStatistic()).thenReturn(List.of());
        when(extraBetRepository.findAll()).thenReturn(List.of());

        List<Statistic> result = statisticService.createStatistic();
        assertThat(result).isEmpty();
    }

    @Test
    public void createStatistic_favouriteTeamMaxIsZero_doesNotSetMaxFavouriteCandidate() {
        Statistic s = new Statistic("charlie");
        s.setPointsGroup(1);
        when(statisticRepository.createStatistic()).thenReturn(List.of(s));

        Team spain = new Team(Country.SPAIN);
        ReflectionTestUtils.setField(spain, "id", 1L);
        when(teamService.findFavouriteTeam()).thenReturn(spain);

        // favourite team points map contains 0 as max
        when(statisticRepository.sumPointsPerUserForFavouriteTeam(spain)).thenReturn(Map.of("charlie", 0));
        when(extraBetRepository.findAll()).thenReturn(List.of());

        List<Statistic> result = statisticService.createStatistic();
        Statistic res = result.get(0);
        assertThat(res.getPointsFavouriteTeam()).isEqualTo(0);
        assertThat(res.isMaxFavouriteTeamCandidate()).isFalse();
    }

    @Test
    public void createStatistic_withFavouriteTeam_setsPointsAndMaxFavouriteCandidate() {
        Statistic alice = new Statistic("alice");
        Statistic bob = new Statistic("bob");
        when(statisticRepository.createStatistic()).thenReturn(List.of(alice, bob));

        Team favourite = new Team("FC Test");
        ReflectionTestUtils.setField(favourite, "id", 2L);
        when(teamService.findFavouriteTeam()).thenReturn(favourite);
        when(statisticRepository.sumPointsPerUserForFavouriteTeam(favourite)).thenReturn(Map.of("alice", 4, "bob", 1));
        when(extraBetRepository.findAll()).thenReturn(List.of());

        List<Statistic> result = statisticService.createStatistic();

        Statistic resAlice = result.get(0);
        assertThat(resAlice.getFavouriteTeam()).isEqualTo(favourite);
        assertThat(resAlice.getPointsFavouriteTeam()).isEqualTo(4);
        assertThat(resAlice.isMaxFavouriteTeamCandidate()).isTrue();

        Statistic resBob = result.get(1);
        assertThat(resBob.getPointsFavouriteTeam()).isEqualTo(1);
        assertThat(resBob.isMaxFavouriteTeamCandidate()).isFalse();
    }
}
