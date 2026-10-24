package de.fred4jupiter.fredbet.statistic;

import de.fred4jupiter.fredbet.betting.repository.BetRepository;
import de.fred4jupiter.fredbet.common.TransactionalIntegrationTest;
import de.fred4jupiter.fredbet.data.DataPopulator;
import de.fred4jupiter.fredbet.domain.Group;
import de.fred4jupiter.fredbet.domain.builder.BetBuilder;
import de.fred4jupiter.fredbet.domain.builder.MatchBuilder;
import de.fred4jupiter.fredbet.domain.entity.Match;
import de.fred4jupiter.fredbet.domain.entity.Team;
import de.fred4jupiter.fredbet.match.MatchService;
import de.fred4jupiter.fredbet.team.TeamService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@TransactionalIntegrationTest
public class StatisticRepositoryIT {

    @Autowired
    private StatisticRepository statisticRepository;

    @Autowired
    private MatchService matchService;

    @Autowired
    private BetRepository betRepository;

    @Autowired
    private TeamService teamService;

    @Autowired
    private DataPopulator dataPopulator;

    @BeforeEach
    public void setup() {
        dataPopulator.deleteAllBetsAndMatches();
    }

    @Test
    public void sumPointsPerUserForFavouriteTeam_onlyCountsMatchesOfFavouriteTeam() {
        Match matchWithFavourite = matchService.save(MatchBuilder.create(teamService).withGroup(Group.GROUP_A).withTeams("Fav", "B").withGoals(1, 0).build());
        Match otherMatchWithFavourite = matchService.save(MatchBuilder.create(teamService).withGroup(Group.GROUP_A).withTeams("C", "Fav").withGoals(0, 2).build());
        Match matchWithoutFavourite = matchService.save(MatchBuilder.create(teamService).withGroup(Group.GROUP_A).withTeams("B", "C").withGoals(1, 1).build());

        betRepository.save(BetBuilder.create().withUserName("alice").withMatch(matchWithFavourite).withGoals(1, 0).withPoints(3).build());
        betRepository.save(BetBuilder.create().withUserName("alice").withMatch(otherMatchWithFavourite).withGoals(0, 1).withPoints(1).build());
        betRepository.save(BetBuilder.create().withUserName("alice").withMatch(matchWithoutFavourite).withGoals(1, 1).withPoints(3).build());
        betRepository.save(BetBuilder.create().withUserName("bob").withMatch(matchWithoutFavourite).withGoals(1, 1).withPoints(3).build());
        betRepository.flush();

        Team favouriteTeam = matchWithFavourite.getTeamOne();
        Map<String, Integer> pointsPerUser = statisticRepository.sumPointsPerUserForFavouriteTeam(favouriteTeam);

        assertThat(pointsPerUser).containsExactly(Map.entry("alice", 4));
    }

    @Test
    public void sumPointsPerUserForFavouriteTeam_withoutFavouriteTeam_returnsEmptyMap() {
        assertThat(statisticRepository.sumPointsPerUserForFavouriteTeam(null)).isEmpty();
    }
}
