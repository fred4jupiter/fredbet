package de.fred4jupiter.fredbet.data;

import de.fred4jupiter.fredbet.betting.repository.BetRepository;
import de.fred4jupiter.fredbet.betting.repository.ExtraBetRepository;
import de.fred4jupiter.fredbet.common.IntegrationTest;
import de.fred4jupiter.fredbet.domain.Country;
import de.fred4jupiter.fredbet.domain.Group;
import de.fred4jupiter.fredbet.domain.builder.MatchBuilder;
import de.fred4jupiter.fredbet.domain.entity.Bet;
import de.fred4jupiter.fredbet.domain.entity.ExtraBet;
import de.fred4jupiter.fredbet.domain.entity.Match;
import de.fred4jupiter.fredbet.match.MatchRepository;
import de.fred4jupiter.fredbet.match.TeamRepository;
import de.fred4jupiter.fredbet.team.TeamService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.function.BooleanSupplier;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Not transactional, as the async methods delete in their own transaction and only see committed data.
 */
@IntegrationTest
public class DataServiceIT {

    private static final long ASYNC_TIMEOUT_MILLIS = 5000;

    @Autowired
    private DataService dataService;

    @Autowired
    private TeamService teamService;

    @Autowired
    private MatchRepository matchRepository;

    @Autowired
    private TeamRepository teamRepository;

    @Autowired
    private BetRepository betRepository;

    @Autowired
    private ExtraBetRepository extraBetRepository;

    @BeforeEach
    public void setup() {
        dataService.deleteAllBetsMatchesAndTeams();

        Match match = matchRepository.save(MatchBuilder.create(teamService).withTeams(Country.GERMANY, Country.FRANCE)
            .withGroup(Group.GROUP_A).withKickOffDate(LocalDateTime.now().plusDays(1)).build());

        Bet bet = new Bet();
        bet.setUserName("anna");
        bet.setMatch(match);
        bet.setGoalsTeamOne(2);
        bet.setGoalsTeamTwo(1);
        betRepository.save(bet);

        ExtraBet extraBet = new ExtraBet();
        extraBet.setUserName("anna");
        extraBet.setFinalWinner(match.getTeamOne());
        extraBetRepository.save(extraBet);

        assertThat(betRepository.count()).isEqualTo(1);
        assertThat(extraBetRepository.count()).isEqualTo(1);
        assertThat(matchRepository.count()).isEqualTo(1);
        assertThat(teamRepository.count()).isEqualTo(2);
    }

    @AfterEach
    public void cleanup() {
        dataService.deleteAllBetsMatchesAndTeams();
    }

    @Test
    public void deleteAllBetsMatchesAndTeams() {
        dataService.deleteAllBetsMatchesAndTeams();

        assertThat(betRepository.count()).isZero();
        assertThat(extraBetRepository.count()).isZero();
        assertThat(matchRepository.count()).isZero();
        assertThat(teamRepository.count()).isZero();
    }

    @Test
    public void deleteAllBetsMatchesAsyncKeepsTeams() throws InterruptedException {
        dataService.deleteAllBetsMatchesAsync();

        waitUntil(() -> matchRepository.count() == 0);
        assertThat(betRepository.count()).isZero();
        assertThat(extraBetRepository.count()).isZero();
        assertThat(teamRepository.count()).isEqualTo(2);
    }

    @Test
    public void deleteAllBetsMatchesAndTeamsAsync() throws InterruptedException {
        dataService.deleteAllBetsMatchesAndTeamsAsync();

        waitUntil(() -> teamRepository.count() == 0);
        assertThat(betRepository.count()).isZero();
        assertThat(extraBetRepository.count()).isZero();
        assertThat(matchRepository.count()).isZero();
    }

    private void waitUntil(BooleanSupplier condition) throws InterruptedException {
        final long deadline = System.currentTimeMillis() + ASYNC_TIMEOUT_MILLIS;
        while (!condition.getAsBoolean()) {
            assertThat(System.currentTimeMillis()).as("async deletion finished in time").isLessThan(deadline);
            Thread.sleep(50);
        }
    }
}
