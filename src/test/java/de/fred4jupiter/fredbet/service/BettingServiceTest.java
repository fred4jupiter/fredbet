package de.fred4jupiter.fredbet.service;

import de.fred4jupiter.fredbet.AbstractTransactionalIntegrationTest;
import de.fred4jupiter.fredbet.domain.AppUser;
import de.fred4jupiter.fredbet.domain.AppUserBuilder;
import de.fred4jupiter.fredbet.domain.Bet;
import de.fred4jupiter.fredbet.domain.Group;
import de.fred4jupiter.fredbet.domain.Joker;
import de.fred4jupiter.fredbet.domain.Match;
import de.fred4jupiter.fredbet.domain.MatchBuilder;
import de.fred4jupiter.fredbet.security.FredBetRole;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;

import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.assertNotNull;

public class BettingServiceTest extends AbstractTransactionalIntegrationTest {
    @Autowired
    private BettingService bettingService;

    @Autowired
    private UserService userService;

    @Autowired
    private JokerService jokerService;

    @Autowired
    private MatchService matchService;

    @Test(expected = NumberOfJokersReachedException.class)
    public void shouldNotAllowToUseMoreJokersThanWhileBetting() {
        AppUser appUser = AppUserBuilder.create().withUsernameAndPassword("mustermann", "mustermann").withRole(FredBetRole.ROLE_USER)
                .build();

        userService.createUser(appUser);

        Match match1 = MatchBuilder.create().withGroup(Group.GROUP_A).withTeams("A", "B").withKickOffDate(LocalDateTime.MAX).build();
        Match match2 = MatchBuilder.create().withGroup(Group.GROUP_B).withTeams("C", "D").withKickOffDate(LocalDateTime.MAX).build();
        Match match3 = MatchBuilder.create().withGroup(Group.GROUP_C).withTeams("E", "F").withKickOffDate(LocalDateTime.MAX).build();
        Match match4 = MatchBuilder.create().withGroup(Group.GROUP_D).withTeams("G", "H").withKickOffDate(LocalDateTime.MAX).build();
        matchService.save(match1);
        matchService.save(match2);
        matchService.save(match3);
        matchService.save(match4);

        Bet bet1 = new Bet();
        bet1.setGoalsTeamOne(1);
        bet1.setGoalsTeamTwo(2);
        bet1.setMatch(match1);
        bet1.setUserName(appUser.getUsername());
        bet1.setJoker(true);

        Bet bet2 = new Bet();
        bet2.setGoalsTeamOne(1);
        bet2.setGoalsTeamTwo(2);
        bet2.setMatch(match2);
        bet2.setUserName(appUser.getUsername());
        bet2.setJoker(true);

        Bet bet3 = new Bet();
        bet3.setGoalsTeamOne(1);
        bet3.setGoalsTeamTwo(2);
        bet3.setMatch(match3);
        bet3.setUserName(appUser.getUsername());
        bet3.setJoker(true);

        Bet bet4 = new Bet();
        bet4.setGoalsTeamOne(1);
        bet4.setGoalsTeamTwo(2);
        bet4.setMatch(match4);
        bet4.setUserName(appUser.getUsername());
        bet4.setJoker(true);

        bettingService.save(bet1);
        bettingService.save(bet2);
        bettingService.save(bet3);

        Joker joker = jokerService.getJokerForUser(appUser.getUsername());
        assertNotNull(joker);
        assertEquals(Integer.valueOf(3), joker.getNumberOfJokersUsed());
        assertEquals(Integer.valueOf(3), joker.getMax());

        bettingService.save(bet4);
    }
}
