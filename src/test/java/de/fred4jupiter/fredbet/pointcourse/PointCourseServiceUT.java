package de.fred4jupiter.fredbet.pointcourse;

import de.fred4jupiter.fredbet.common.UnitTest;
import de.fred4jupiter.fredbet.betting.repository.BetRepository;
import de.fred4jupiter.fredbet.domain.builder.MatchBuilder;
import de.fred4jupiter.fredbet.domain.entity.Match;
import de.fred4jupiter.fredbet.match.MatchRepository;
import de.fred4jupiter.fredbet.util.MessageSourceUtil;
import de.fred4jupiter.fredbet.props.FredbetProperties;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@UnitTest
public class PointCourseServiceUT {

    @Mock
    private BetRepository betRepository;

    @Mock
    private MessageSourceUtil messageSourceUtil;

    @Mock
    private MatchRepository matchRepository;

    @Mock
    private FredbetProperties fredbetProperties;

    @InjectMocks
    private PointCourseService pointCourseService;

    @Test
    public void reportPointsCourse_whenNoBets_returnsEmptyChartData() {
        when(betRepository.queryPointsPerUser()).thenReturn(List.of());
        when(matchRepository.findFinishedMatches()).thenReturn(List.of());
        Mockito.lenient().when(fredbetProperties.adminUsername()).thenReturn("admin");

        ChartData chartData = pointCourseService.reportPointsCourse("user1", Locale.ENGLISH);

        assertThat(chartData).isNotNull();
        assertThat(chartData.getLabels()).isEmpty();
        assertThat(chartData.getDatasets()).isEmpty();
        assertThat(chartData.isEmpty()).isTrue();
    }

    @Test
    public void reportPointsCourse_withResults_buildsChartData() {
        // prepare matches
        Match m1 = MatchBuilder.create().withTeams("A","B").withKickOffDate(LocalDateTime.now().minusDays(2)).build();
        Match m2 = MatchBuilder.create().withTeams("C","D").withKickOffDate(LocalDateTime.now().minusDays(1)).build();

        when(betRepository.queryPointsPerUser()).thenReturn(List.of(new PointsPerUser("user1", 3L), new PointsPerUser("other", 5L)));
        Mockito.lenient().when(fredbetProperties.adminUsername()).thenReturn("admin");

        // when calculateMinMax picks 'other' as min or max, queryPointsCourse will be called with three users
        when(betRepository.queryPointsCourse(List.of("other", "user1", "other"))).thenReturn(List.of(
                new PointCourseResult("user1", 3, m1),
                new PointCourseResult("user1", 0, m2),
                new PointCourseResult("other", 5, m1),
                new PointCourseResult("other", 0, m2)
        ));

        when(matchRepository.findFinishedMatches()).thenReturn(List.of(m1, m2));

        ChartData chartData = pointCourseService.reportPointsCourse("user1", Locale.ENGLISH);

        assertThat(chartData).isNotNull();
        assertThat(chartData.getLabels()).hasSize(2);
        assertThat(chartData.getDatasets()).hasSize(2);

        // check dataset names contain usernames
        assertThat(chartData.getDatasets().stream().map(ds -> ds.label()).anyMatch(n -> n.equals("user1"))).isTrue();
        assertThat(chartData.getDatasets().stream().map(ds -> ds.label()).anyMatch(n -> n.equals("other"))).isTrue();

        assertThat(chartData.isEmpty()).isFalse();
    }
}
