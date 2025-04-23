package de.fred4jupiter.fredbet.user;

import de.fred4jupiter.fredbet.betting.BettingService;
import de.fred4jupiter.fredbet.data.GoalResult;
import de.fred4jupiter.fredbet.data.RandomValueGenerator;
import de.fred4jupiter.fredbet.data.TeamPair;
import de.fred4jupiter.fredbet.domain.*;
import de.fred4jupiter.fredbet.domain.builder.MatchBuilder;
import de.fred4jupiter.fredbet.domain.entity.AppUser;
import de.fred4jupiter.fredbet.domain.builder.AppUserBuilder;
import de.fred4jupiter.fredbet.domain.entity.Bet;
import de.fred4jupiter.fredbet.domain.entity.Match;
import de.fred4jupiter.fredbet.match.MatchService;
import de.fred4jupiter.fredbet.security.FredBetUserGroup;
import de.fred4jupiter.fredbet.teambundle.TeamBundle;
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

    public FredBetUsageBuilder withMatch(TeamBundle teamBundle) {
        TeamPair teamPair = randomValueGenerator.generateTeamPair(teamBundle);
        this.match = MatchBuilder.create().withTeams(teamPair.teamOne(), teamPair.teamTwo()).withGroup(Group.GROUP_A)
                .withStadium("Somewhere").withKickOffDate(LocalDateTime.now().plusDays(1)).build();
        return this;
    }

    public FredBetUsageBuilder withBet() {
        GoalResult goalResult = randomValueGenerator.generateGoalResult();

        Bet bet = new Bet();
        bet.setGoalsTeamOne(goalResult.goalsTeamOne());
        bet.setGoalsTeamTwo(goalResult.goalsTeamTwo());
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
            withMatch(TeamBundle.WORLD_CUP);
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
