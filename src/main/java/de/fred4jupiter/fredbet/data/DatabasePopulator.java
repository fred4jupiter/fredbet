package de.fred4jupiter.fredbet.data;

import de.fred4jupiter.fredbet.domain.*;
import de.fred4jupiter.fredbet.props.FredBetProfile;
import de.fred4jupiter.fredbet.props.FredbetProperties;
import de.fred4jupiter.fredbet.security.FredBetUserGroup;
import de.fred4jupiter.fredbet.service.BettingService;
import de.fred4jupiter.fredbet.service.InfoService;
import de.fred4jupiter.fredbet.service.JokerService;
import de.fred4jupiter.fredbet.service.MatchService;
import de.fred4jupiter.fredbet.service.image.ImageAdministrationService;
import de.fred4jupiter.fredbet.service.user.UserAlreadyExistsException;
import de.fred4jupiter.fredbet.service.user.UserService;
import de.fred4jupiter.fredbet.web.info.InfoType;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.IntStream;

@Component
public class DatabasePopulator {

    private static final int NUMBER_OF_DEMO_USERS = 12;

    private static final Logger LOG = LoggerFactory.getLogger(DatabasePopulator.class);

    private final MatchService matchService;

    private final Environment environment;

    private final UserService userService;

    private final BettingService bettingService;

    private final RandomValueGenerator randomValueGenerator;

    private final InfoService infoService;

    private final ImageAdministrationService imageAdministrationService;

    private final JokerService jokerService;

    private final FredbetProperties fredbetProperties;

    private final FakeDataPopulator fakeDataPopulator;

    private final StaticResourceLoader staticResourceLoader;

    public DatabasePopulator(MatchService matchService, Environment environment, UserService userService,
                             BettingService bettingService, RandomValueGenerator randomValueGenerator,
                             InfoService infoService, ImageAdministrationService imageAdministrationService,
                             JokerService jokerService, FredbetProperties fredbetProperties, FakeDataPopulator fakeDataPopulator,
                             StaticResourceLoader staticResourceLoader) {
        this.matchService = matchService;
        this.environment = environment;
        this.userService = userService;
        this.bettingService = bettingService;
        this.randomValueGenerator = randomValueGenerator;
        this.infoService = infoService;
        this.imageAdministrationService = imageAdministrationService;
        this.jokerService = jokerService;
        this.fredbetProperties = fredbetProperties;
        this.fakeDataPopulator = fakeDataPopulator;
        this.staticResourceLoader = staticResourceLoader;
    }

    public void initDatabaseWithDemoData() {
        if (!isRunInIntegrationTest()) {
            createAdminUser();
            addRulesIfEmpty();
        }

        if (!isRunInIntegrationTest() && fredbetProperties.isCreateDemoData()) {
            createDemoUsers(NUMBER_OF_DEMO_USERS);
            createRandomMatches();
        }

        imageAdministrationService.createDefaultImageGroup();
    }

    private void addRulesIfEmpty() {
        String rulesInGerman = staticResourceLoader.loadDefaultRules();
        infoService.saveInfoContentIfNotPresent(InfoType.RULES, rulesInGerman, "de");
    }

    private boolean isRunInIntegrationTest() {
        return environment.acceptsProfiles(Profiles.of(FredBetProfile.INTEGRATION_TEST));
    }

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

        createRandomForGroup(localDateTime.plusDays(9), Group.ROUND_OF_SIXTEEN, 8);
        createRandomForGroup(localDateTime.plusDays(10), Group.QUARTER_FINAL, 4);
        createRandomForGroup(localDateTime.plusDays(11), Group.SEMI_FINAL, 2);
        createRandomForGroup(localDateTime.plusDays(12), Group.FINAL, 1);
        createRandomForGroup(localDateTime.plusDays(13), Group.GAME_FOR_THIRD, 1);
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
        bettingService.createAndSaveBetting(appUser.getUsername(), match, goalsTeamOne, goalsTeamTwo, joker);
    }

    public void createDemoResultsForAllMatches() {
        LOG.info("createDemoResultsForAllUsers...");
        matchService.enterMatchResultsForAllMatches(match -> {
            match.setGoalsTeamOne(randomValueGenerator.generateRandomValue());
            match.setGoalsTeamTwo(randomValueGenerator.generateRandomValue());
        });
    }

    public void createDemoUsers(int numberOfDemoUsers) {
        LOG.info("createAdditionalUsers: creating {} additional demo users ...", numberOfDemoUsers);

        final byte[] demoImage = staticResourceLoader.loadDemoUserProfileImage();

        IntStream.rangeClosed(1, numberOfDemoUsers).forEach(counter -> {
            final String usernameAndPassword = this.fakeDataPopulator.nextRandomUsername();
            AppUser user = AppUserBuilder.create().withUsernameAndPassword(usernameAndPassword, usernameAndPassword)
                    .withUserGroup(FredBetUserGroup.ROLE_USER).build();
            LOG.debug("creating demo user {}: {}", counter, usernameAndPassword);
            boolean isNewUser = saveIfNotPresent(user);
            if (isNewUser && fakeDataPopulator.nextRandomBoolean()) {
                this.imageAdministrationService.saveUserProfileImage(demoImage, user);
            }
        });
    }

    private void createAdminUser() {
        AppUser appUser = AppUserBuilder.create()
                .withUsernameAndPassword(fredbetProperties.getAdminUsername(), fredbetProperties.getAdminPassword())
                .withUserGroup(FredBetUserGroup.ROLE_ADMIN)
                .deletable(false)
                .build();
        boolean created = saveIfNotPresent(appUser);
        LOG.info("created new admin user: {}", created);
    }

    public boolean saveIfNotPresent(AppUser appUser) {
        try {
            userService.createUser(appUser);
            return true;
        } catch (UserAlreadyExistsException e) {
            LOG.debug(e.getMessage());
            return false;
        }
    }

    @Transactional
    public void deleteAllBetsAndMatches() {
        bettingService.deleteAllBets();
        matchService.deleteAllMatches();
    }

}
