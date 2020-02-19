package de.fred4jupiter.fredbet.service;

import de.fred4jupiter.fredbet.data.RandomValueGenerator;
import de.fred4jupiter.fredbet.domain.*;
import de.fred4jupiter.fredbet.security.FredBetUserGroup;
import de.fred4jupiter.fredbet.service.user.UserService;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * Convenient class for creating test data in integration tests.
 *
 * @author michael
 */
@Component
@Scope("prototype")
public class FredBetUsageBuilder {

    private final RandomValueGenerator randomValueGenerator;

    private final UserService userService;

    private final MatchService matchService;

    private final BettingService bettingService;

    private AppUser appUser;

    private Match match;

    private Bet bet;

    @Autowired
    private FredBetUsageBuilder(RandomValueGenerator randomValueGenerator, UserService userService, MatchService matchService,
                                BettingService bettingService) {
        this.randomValueGenerator = randomValueGenerator;
        this.userService = userService;
        this.matchService = matchService;
        this.bettingService = bettingService;
    }

    public FredBetUsageBuilder withAppUser() {
        return withAppUser("albert", "einstein");
    }

    public FredBetUsageBuilder withAppUser(String username, String password) {
        this.appUser = AppUserBuilder.create().withUsernameAndPassword(username, password).withUserGroup(FredBetUserGroup.ROLE_USER).build();
        return this;
    }

    public FredBetUsageBuilder withMatch() {
        ImmutablePair<Country, Country> teamPair = randomValueGenerator.generateTeamPair();
        this.match = MatchBuilder.create().withTeams(teamPair.getLeft(), teamPair.getRight()).withGroup(Group.GROUP_A)
                .withStadium("Somewhere").withKickOffDate(LocalDateTime.now().plusDays(1)).build();
        return this;
    }

    public FredBetUsageBuilder withBet() {
        Integer goalsTeamOne = randomValueGenerator.generateRandomValue();
        Integer goalsTeamTwo = randomValueGenerator.generateRandomValue();

        Bet bet = new Bet();
        bet.setGoalsTeamOne(goalsTeamOne);
        bet.setGoalsTeamTwo(goalsTeamTwo);
        bet.setMatch(match);
        bet.setUserName(appUser.getUsername());

        this.bet = bet;
        return this;
    }

    public AppUser build() {
        if (this.appUser == null) {
            withAppUser();
        }

        if (this.match == null) {
            withMatch();
        }

        if (this.bet == null) {
            withBet();
        }

        this.userService.createUser(this.appUser);
        this.matchService.save(this.match);
        this.bettingService.save(this.bet);

        return this.appUser;
    }
}
