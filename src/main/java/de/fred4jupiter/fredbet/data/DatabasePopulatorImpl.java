package de.fred4jupiter.fredbet.data;

import de.fred4jupiter.fredbet.domain.*;
import de.fred4jupiter.fredbet.domain.builder.MatchBuilder;
import de.fred4jupiter.fredbet.domain.entity.AppUser;
import de.fred4jupiter.fredbet.domain.builder.AppUserBuilder;
import de.fred4jupiter.fredbet.domain.entity.Match;
import de.fred4jupiter.fredbet.security.FredBetUserGroup;
import de.fred4jupiter.fredbet.betting.BettingService;
import de.fred4jupiter.fredbet.betting.JokerService;
import de.fred4jupiter.fredbet.match.MatchService;
import de.fred4jupiter.fredbet.user.UserService;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.IntStream;

@Component
class DatabasePopulatorImpl implements DatabasePopulator {

    private static final Logger LOG = LoggerFactory.getLogger(DatabasePopulatorImpl.class);

    private final MatchService matchService;

    private final UserService userService;

    private final BettingService bettingService;

    private final RandomValueGenerator randomValueGenerator;

    private final JokerService jokerService;

    private final FakeDataPopulator fakeDataPopulator;

    public DatabasePopulatorImpl(MatchService matchService, UserService userService,
                                 BettingService bettingService, RandomValueGenerator randomValueGenerator,
                                 JokerService jokerService, FakeDataPopulator fakeDataPopulator) {
        this.matchService = matchService;
        this.userService = userService;
        this.bettingService = bettingService;
        this.randomValueGenerator = randomValueGenerator;
        this.jokerService = jokerService;
        this.fakeDataPopulator = fakeDataPopulator;
    }

    @Override
    public void createRandomMatches() {
        bettingService.deleteAllBets();
        matchService.deleteAllMatches();

        final LocalDateTime localDateTime = LocalDateTime.now();
        createRandomForGroup(localDateTime.plusDays(1), Group.GROUP_A, 4);
        createRandomForGroup(localDateTime.plusDays(2), Group.GROUP_B, 4);
        createRandomForGroup(localDateTime.plusDays(3), Group.GROUP_C, 4);
        createRandomForGroup(localDateTime.plusDays(4), Group.GROUP_D, 4);
        createRandomForGroup(localDateTime.plusDays(5), Group.GROUP_E, 4);
        createRandomForGroup(localDateTime.plusDays(6), Group.GROUP_F, 4);
        createRandomForGroup(localDateTime.plusDays(7), Group.GROUP_G, 4);
        createRandomForGroup(localDateTime.plusDays(8), Group.GROUP_H, 4);
        createRandomForGroup(localDateTime.plusDays(9), Group.GROUP_I, 4);
        createRandomForGroup(localDateTime.plusDays(10), Group.GROUP_J, 4);
        createRandomForGroup(localDateTime.plusDays(11), Group.GROUP_K, 4);
        createRandomForGroup(localDateTime.plusDays(12), Group.GROUP_L, 4);

        createRandomForGroup(localDateTime.plusDays(13), Group.ROUND_OF_SIXTEEN, 8);
        createRandomForGroup(localDateTime.plusDays(14), Group.QUARTER_FINAL, 4);
        createRandomForGroup(localDateTime.plusDays(15), Group.SEMI_FINAL, 2);
        createRandomForGroup(localDateTime.plusDays(16), Group.FINAL, 1);
        createRandomForGroup(localDateTime.plusDays(17), Group.GAME_FOR_THIRD, 1);
    }

    private void createRandomForGroup(LocalDateTime localDateTime, Group group, int numberOfMatches) {
        IntStream.rangeClosed(1, numberOfMatches).forEach(counter -> {
            ImmutablePair<Country, Country> teamPair = randomValueGenerator.generateTeamPair();
            Match match = MatchBuilder.create()
                .withTeams(teamPair.getLeft(), teamPair.getRight())
                .withGroup(group)
                .withStadium(fakeDataPopulator.nextStadium())
                .withKickOffDate(localDateTime.plusHours(counter)).build();
            matchService.save(match);
        });
    }

    @Override
    public void createDemoBetsForAllUsers() {
        LOG.info("createDemoBetsForAllUsers...");
        bettingService.deleteAllBets();

        final List<Match> allMatches = matchService.findAll();
        final List<AppUser> users = userService.findAll();
        users.forEach(appUser -> {
            allMatches.forEach(match -> {
                boolean jokerAllowed = false;
                if (randomValueGenerator.generateRandomBoolean()) {
                    jokerAllowed = jokerService.isSettingJokerAllowed(appUser.getUsername(), match.getId());
                }
                createBetForUser(appUser, match, jokerAllowed);
            });

            bettingService.createExtraBetForUser(appUser.getUsername());
        });
        LOG.debug("created demo bets for all users finished.");
    }

    private void createBetForUser(AppUser appUser, Match match, boolean joker) {
        Integer goalsTeamOne = randomValueGenerator.generateRandomValue();
        Integer goalsTeamTwo = randomValueGenerator.generateRandomValue();
        bettingService.createAndSaveBetting(builder -> {
            builder.withGoals(goalsTeamOne, goalsTeamTwo).withJoker(joker).withUserName(appUser.getUsername()).withMatch(match);
        });
    }

    @Override
    public void createDemoResultsForAllMatches() {
        LOG.info("createDemoResultsForAllUsers...");
        matchService.enterMatchResultsForAllMatches(match -> {
            match.setGoalsTeamOne(randomValueGenerator.generateRandomValue());
            match.setGoalsTeamTwo(randomValueGenerator.generateRandomValue());
        });
    }

    @Override
    public void createDemoUsers(int numberOfDemoUsers) {
        LOG.info("createAdditionalUsers: creating {} additional demo users ...", numberOfDemoUsers);

        IntStream.rangeClosed(1, numberOfDemoUsers).forEach(counter -> {
            final String usernameAndPassword = this.fakeDataPopulator.nextRandomUsername();
            AppUser user = AppUserBuilder.create().withUsernameAndPassword(usernameAndPassword, usernameAndPassword)
                .withUserGroup(FredBetUserGroup.ROLE_USER).withIsChild(fakeDataPopulator.nextRandomBoolean()).build();
            LOG.debug("creating demo user {}: {}", counter, usernameAndPassword);
            userService.createUserIfNotExists(user);
        });
    }

    @Override
    @Transactional
    public void deleteAllBetsAndMatches() {
        bettingService.deleteAllBets();
        matchService.deleteAllMatches();
    }

}
