package de.fred4jupiter.fredbet.statistic;

import de.fred4jupiter.fredbet.betting.repository.ExtraBetRepository;
import de.fred4jupiter.fredbet.common.UnitTest;
import de.fred4jupiter.fredbet.domain.Country;
import de.fred4jupiter.fredbet.settings.RuntimeSettings;
import de.fred4jupiter.fredbet.settings.RuntimeSettingsService;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

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
    private RuntimeSettingsService runtimeSettingsService;

    @InjectMocks
    private StatisticService statisticService;

    // TODO fix test
//    @Disabled
//    @Test
//    public void createStatistic_happyPath_setsFlagsAndExtraBetPoints() {
//        Statistic s1 = new Statistic("alice");
//        s1.setPointsGroup(5);
//        Statistic s2 = new Statistic("bob");
//        s2.setPointsGroup(3);
//
//        when(statisticRepository.createStatistic()).thenReturn(List.of(s1, s2));
//
//        // favourite country setup
//        RuntimeSettings rs = new RuntimeSettings();
//        rs.setFavouriteCountry(Country.GERMANY);
//        when(runtimeSettingsService.loadRuntimeSettings()).thenReturn(rs);
//
//        when(statisticRepository.sumPointsPerUserForFavoriteCountry(Country.GERMANY)).thenReturn(Map.of("alice", 2, "bob", 1));
//
//        ExtraBet eb = new ExtraBet();
//        eb.setUserName("alice");
//        eb.setPointsOne(1);
//        eb.setPointsTwo(1);
//        eb.setPointsThree(0);
//        when(extraBetRepository.findAll()).thenReturn(List.of(eb));
//
//        List<Statistic> result = statisticService.createStatistic();
//
//        assertThat(result).hasSize(2);
//
//        Statistic resAlice = result.stream().filter(s -> s.getUsername().equals("alice")).findFirst().get();
//        assertThat(resAlice.getPointsForExtraBets()).isEqualTo(2);
//        assertThat(resAlice.getPointsFavoriteCountry()).isEqualTo(2);
//        assertThat(resAlice.isMaxFavoriteCountryCandidate()).isTrue();
//        assertThat(resAlice.isMaxPointsCandidate()).isTrue();
//
//        Statistic resBob = result.stream().filter(s -> s.getUsername().equals("bob")).findFirst().get();
//        assertThat(resBob.isMinPointsCandidate()).isTrue();
//        assertThat(resBob.isMaxFavoriteCountryCandidate()).isFalse();
//    }

    @Test
    public void createStatistic_emptyRepository_returnsEmptyList() {
        when(statisticRepository.createStatistic()).thenReturn(List.of());
        when(runtimeSettingsService.loadRuntimeSettings()).thenReturn(new RuntimeSettings());
        when(extraBetRepository.findAll()).thenReturn(List.of());

        List<Statistic> result = statisticService.createStatistic();
        assertThat(result).isEmpty();
    }

    @Test
    public void createStatistic_favouriteCountryMaxIsZero_doesNotSetMaxFavoriteCandidate() {
        Statistic s = new Statistic("charlie");
        s.setPointsGroup(1);
        when(statisticRepository.createStatistic()).thenReturn(List.of(s));

        RuntimeSettings rs = new RuntimeSettings();
        rs.setFavouriteCountry(Country.SPAIN);
        when(runtimeSettingsService.loadRuntimeSettings()).thenReturn(rs);

        // favorite country points map contains 0 as max
        when(statisticRepository.sumPointsPerUserForFavoriteCountry(Country.SPAIN)).thenReturn(Map.of("charlie", 0));
        when(extraBetRepository.findAll()).thenReturn(List.of());

        List<Statistic> result = statisticService.createStatistic();
        Statistic res = result.get(0);
        assertThat(res.getPointsFavoriteCountry()).isEqualTo(0);
        assertThat(res.isMaxFavoriteCountryCandidate()).isFalse();
    }
}
