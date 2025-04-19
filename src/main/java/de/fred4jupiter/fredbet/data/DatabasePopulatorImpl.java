package de.fred4jupiter.fredbet.data;

import com.github.javafaker.Faker;
import de.fred4jupiter.fredbet.betting.BettingService;
import de.fred4jupiter.fredbet.betting.ExtraBettingService;
import de.fred4jupiter.fredbet.betting.JokerService;
import de.fred4jupiter.fredbet.domain.Group;
import de.fred4jupiter.fredbet.domain.builder.AppUserBuilder;
import de.fred4jupiter.fredbet.domain.builder.MatchBuilder;
import de.fred4jupiter.fredbet.domain.entity.AppUser;
import de.fred4jupiter.fredbet.domain.entity.Match;
import de.fred4jupiter.fredbet.match.MatchService;
import de.fred4jupiter.fredbet.security.FredBetUserGroup;
import de.fred4jupiter.fredbet.teambundle.TeamBundle;
import de.fred4jupiter.fredbet.user.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import java.util.function.Consumer;
import java.util.stream.IntStream;

@Component
@Transactional
class DatabasePopulatorImpl implements DatabasePopulator {

    private static final Logger LOG = LoggerFactory.getLogger(DatabasePopulatorImpl.class);

    private final MatchService matchService;

    private final UserService userService;

    private final BettingService bettingService;

    private final RandomValueGenerator randomValueGenerator;

    private final JokerService jokerService;

    private final ExtraBettingService extraBettingService;

    private final Faker faker = new Faker(Locale.getDefault());

    public DatabasePopulatorImpl(MatchService matchService, UserService userService,
                                 BettingService bettingService, RandomValueGenerator randomValueGenerator,
                                 JokerService jokerService, ExtraBettingService extraBettingService) {
        this.matchService = matchService;
        this.userService = userService;
        this.bettingService = bettingService;
        this.randomValueGenerator = randomValueGenerator;
        this.jokerService = jokerService;
        this.extraBettingService = extraBettingService;
    }

    @Async
    public void executeAsync(Consumer<DatabasePopulator> populatorCallback) {
        populatorCallback.accept(this);
    }

    @Override
    public void createDemoData(DemoDataCreation demoDataCreation) {
        bettingService.deleteAllBets();
        matchService.deleteAllMatches();

        final LocalDateTime localDateTime = LocalDateTime.now();

        IntStream.rangeClosed(1, demoDataCreation.numberOfGroups()).forEach(count -> {
            createRandomForGroup(demoDataCreation.teamBundle(), localDateTime.plusDays(count), countToGroup(count), 4);
        });

        int numberOfGroups = demoDataCreation.numberOfGroups();
        createRandomForGroup(demoDataCreation.teamBundle(), localDateTime.plusDays(numberOfGroups + 1), Group.ROUND_OF_SIXTEEN, 8);
        createRandomForGroup(demoDataCreation.teamBundle(), localDateTime.plusDays(numberOfGroups + 2), Group.QUARTER_FINAL, 4);
        createRandomForGroup(demoDataCreation.teamBundle(), localDateTime.plusDays(numberOfGroups + 3), Group.SEMI_FINAL, 2);
        createRandomForGroup(demoDataCreation.teamBundle(), localDateTime.plusDays(numberOfGroups + 4), Group.FINAL, 1);
        createRandomForGroup(demoDataCreation.teamBundle(), localDateTime.plusDays(numberOfGroups + 5), Group.GAME_FOR_THIRD, 1);

        if (demoDataCreation.withBets()) {
            createDemoBetsForAllUsers();
        }
        if (demoDataCreation.withResults()) {
            createDemoResultsForAllMatches();
        }
    }

    private Group countToGroup(int count) {
        return switch (count) {
            case 1 -> Group.GROUP_A;
            case 2 -> Group.GROUP_B;
            case 3 -> Group.GROUP_C;
            case 4 -> Group.GROUP_D;
            case 5 -> Group.GROUP_E;
            case 6 -> Group.GROUP_F;
            case 7 -> Group.GROUP_G;
            case 8 -> Group.GROUP_H;
            case 9 -> Group.GROUP_I;
            case 10 -> Group.GROUP_J;
            case 11 -> Group.GROUP_K;
            case 12 -> Group.GROUP_L;
            default -> throw new IllegalArgumentException("More than 12 groups are not supported");
        };
    }

    private void createRandomForGroup(TeamBundle teamBundle, LocalDateTime localDateTime, Group group, int numberOfMatches) {
        LOG.debug("creating group {} with {} matches.", group, numberOfMatches);
        IntStream.rangeClosed(1, numberOfMatches).forEach(counter -> {
            TeamPair teamPair = randomValueGenerator.generateTeamPair(teamBundle);
            Match match = MatchBuilder.create()
                .withTeams(teamPair.teamOne(), teamPair.teamTwo())
                .withGroup(group)
                .withStadium(nextStadium())
                .withKickOffDate(localDateTime.plusHours(counter)).build();
            matchService.save(match);
        });
    }

    private void createDemoBetsForAllUsers() {
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

            extraBettingService.createExtraBetForUser(appUser.getUsername());
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

    private void createDemoResultsForAllMatches() {
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
            final String usernameAndPassword = nextRandomUsername();
            AppUser user = AppUserBuilder.create().withUsernameAndPassword(usernameAndPassword, usernameAndPassword)
                .withUserGroup(FredBetUserGroup.ROLE_USER).withIsChild(nextRandomBoolean()).build();
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

    private Boolean nextRandomBoolean() {
        return this.faker.random().nextBoolean();
    }

    private String nextRandomUsername() {
        return this.faker.name().firstName();
    }

    private String nextStadium() {
        return this.faker.country().capital();
    }
}
