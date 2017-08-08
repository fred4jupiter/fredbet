package de.fred4jupiter.fredbet;

import java.time.LocalDateTime;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import de.fred4jupiter.fredbet.data.RandomValueGenerator;
import de.fred4jupiter.fredbet.domain.AppUser;
import de.fred4jupiter.fredbet.domain.AppUserBuilder;
import de.fred4jupiter.fredbet.domain.Bet;
import de.fred4jupiter.fredbet.domain.Country;
import de.fred4jupiter.fredbet.domain.Group;
import de.fred4jupiter.fredbet.domain.Match;
import de.fred4jupiter.fredbet.domain.MatchBuilder;
import de.fred4jupiter.fredbet.security.FredBetRole;
import de.fred4jupiter.fredbet.service.BettingService;
import de.fred4jupiter.fredbet.service.MatchService;
import de.fred4jupiter.fredbet.service.UserService;

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
		this.appUser = AppUserBuilder.create().withUsernameAndPassword(username, password).withRole(FredBetRole.ROLE_USER).build();
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

		this.userService.insertAppUser(this.appUser);
		this.matchService.save(this.match);
		this.bettingService.save(this.bet);

		return this.appUser;
	}
}
