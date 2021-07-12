package de.fred4jupiter.fredbet.service;

import de.fred4jupiter.fredbet.common.TransactionalIntegrationTest;
import de.fred4jupiter.fredbet.domain.*;
import de.fred4jupiter.fredbet.security.FredBetUserGroup;
import de.fred4jupiter.fredbet.service.user.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@TransactionalIntegrationTest
public class BettingServiceIT {

    @Autowired
    private BettingService bettingService;

    @Autowired
    private UserService userService;

    @Autowired
    private JokerService jokerService;

    @Autowired
    private MatchService matchService;

    @Test
    public void shouldNotAllowToUseMoreJokersThanWhileBetting() {
        AppUser appUser = AppUserBuilder.create().withUsernameAndPassword("mustermann", "mustermann").withUserGroup(FredBetUserGroup.ROLE_USER)
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

        assertThrows(NumberOfJokersReachedException.class, () -> {
            bettingService.save(bet4);
        });
    }

    @Test
    public void shouldAllowToBetIfMaximumNumberOfJokesIsReachedButJokerIsNotUsed() {
        AppUser appUser = AppUserBuilder.create().withUsernameAndPassword("mustermann", "mustermann").withUserGroup(FredBetUserGroup.ROLE_USER)
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
        bet4.setJoker(false);

        bettingService.save(bet1);
        bettingService.save(bet2);
        bettingService.save(bet3);

        Joker joker = jokerService.getJokerForUser(appUser.getUsername());
        assertNotNull(joker);
        assertEquals(Integer.valueOf(3), joker.getNumberOfJokersUsed());
        assertEquals(Integer.valueOf(3), joker.getMax());

        Long matchWithoutJoker = bettingService.save(bet4);
        assertNotNull(matchWithoutJoker);
    }

    @Test
    public void shouldAllowSavingAgainMatchWithLastJoker() {
        AppUser appUser = AppUserBuilder.create().withUsernameAndPassword("mustermann", "mustermann").withUserGroup(FredBetUserGroup.ROLE_USER)
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

        bettingService.save(bet1);
        bettingService.save(bet2);
        bettingService.save(bet3);

        Joker joker = jokerService.getJokerForUser(appUser.getUsername());
        assertNotNull(joker);
        assertEquals(Integer.valueOf(3), joker.getNumberOfJokersUsed());
        assertEquals(Integer.valueOf(3), joker.getMax());

        Long matchWithoutJoker = bettingService.save(bet3);
        assertNotNull(matchWithoutJoker);
    }
}
