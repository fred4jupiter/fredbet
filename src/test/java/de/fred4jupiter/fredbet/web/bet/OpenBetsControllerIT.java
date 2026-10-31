package de.fred4jupiter.fredbet.web.bet;

import de.fred4jupiter.fredbet.betting.BettingService;
import de.fred4jupiter.fredbet.common.MockMvcIntegrationTest;
import de.fred4jupiter.fredbet.domain.Group;
import de.fred4jupiter.fredbet.domain.entity.Match;
import de.fred4jupiter.fredbet.domain.entity.Team;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.assertj.MockMvcTester;
import org.springframework.test.web.servlet.assertj.MvcTestResult;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@MockMvcIntegrationTest
@WithUserDetails("admin")
public class OpenBetsControllerIT {

    @Autowired
    private MockMvcTester mvc;

    @MockitoBean
    private BettingService bettingService;

    @Test
    public void openMatchesAreGroupedByMatchDay() {
        LocalDateTime day = LocalDateTime.now().plusDays(10).withHour(18).withMinute(0);
        when(bettingService.findMatchesToBet(any())).thenReturn(List.of(
            match(101L, day, "FC One", "FC Two"),
            match(102L, day.plusHours(3), "FC Three", "FC Four"),
            match(103L, day.plusDays(1), "FC Five", "FC Six")));

        assertThat(mvc.get().uri("/bet/open"))
            .hasStatusOk()
            .bodyText()
            .contains(
                "/bet/createOrUpdate/101?redirectViewName=OPEN_BETS",
                "FC Six",
                "21:00")
            // two match days, so two headings
            .satisfies(html -> assertThat(html.split("list-open-day", -1)).hasSize(3));
    }

    @Test
    public void diceShowsMessageAfterRedirect() {
        MvcTestResult result = mvc.get().uri("/bet/dice").exchange();

        assertThat(result).hasStatus3xxRedirection().hasRedirectedUrl("/bet/open");
        assertThat(result).flash().containsKey("globalMessage");

        verify(bettingService).diceAllMatchesForUser("admin");
    }

    private Match match(Long id, LocalDateTime kickOff, String teamOne, String teamTwo) {
        Match match = new Match();
        ReflectionTestUtils.setField(match, "id", id);
        match.setTeamOne(team(id * 10, teamOne));
        match.setTeamTwo(team(id * 10 + 1, teamTwo));
        match.setKickOffDate(kickOff);
        match.setGroup(Group.GROUP_A);
        match.setStadium("Arena");
        return match;
    }

    private Team team(Long id, String name) {
        Team team = new Team(name);
        ReflectionTestUtils.setField(team, "id", id);
        return team;
    }

}
