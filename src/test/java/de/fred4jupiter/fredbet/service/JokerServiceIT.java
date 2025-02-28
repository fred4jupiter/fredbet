package de.fred4jupiter.fredbet.service;

import de.fred4jupiter.fredbet.common.TransactionalIntegrationTest;
import de.fred4jupiter.fredbet.domain.*;
import de.fred4jupiter.fredbet.repository.BetRepository;
import de.fred4jupiter.fredbet.security.FredBetUserGroup;
import de.fred4jupiter.fredbet.user.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@TransactionalIntegrationTest
public class JokerServiceIT {

    @Autowired
    private JokerService jokerService;

    @Autowired
    private UserService userService;

    @Autowired
    private MatchService matchService;

    @Autowired
    private BetRepository betRepository;

    @Test
    public void newUserHasNoJokerUsed() {
        AppUser appUser = AppUserBuilder.create().withUsernameAndPassword("mustermann", "mustermann").withUserGroup(FredBetUserGroup.ROLE_USER)
                .build();

        userService.createUser(appUser);

        Joker joker = jokerService.getJokerForUser(appUser.getUsername());
        assertNotNull(joker);
        assertEquals(Integer.valueOf(0), joker.numberOfJokersUsed());
        assertEquals(Integer.valueOf(3), joker.max());
    }

    @Test
    public void oneBetWithJoker() {
        AppUser appUser = AppUserBuilder.create().withUsernameAndPassword("mustermann", "mustermann").withUserGroup(FredBetUserGroup.ROLE_USER)
                .build();

        userService.createUser(appUser);

        Match match = MatchBuilder.create().withGroup(Group.GROUP_A).withTeams("A", "B").withGoals(1, 1).build();
        assertNotNull(match);
        matchService.save(match);

        Bet bet = new Bet();
        bet.setGoalsTeamOne(1);
        bet.setGoalsTeamTwo(2);
        bet.setMatch(match);
        bet.setUserName(appUser.getUsername());
        bet.setJoker(true);

        betRepository.save(bet);

        Joker joker = jokerService.getJokerForUser(appUser.getUsername());
        assertNotNull(joker);
        assertEquals(Integer.valueOf(1), joker.numberOfJokersUsed());
        assertEquals(Integer.valueOf(3), joker.max());
    }

}
