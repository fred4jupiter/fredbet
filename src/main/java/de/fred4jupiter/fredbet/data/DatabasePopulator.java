package de.fred4jupiter.fredbet.data;

import com.github.javafaker.Faker;
import de.fred4jupiter.fredbet.domain.*;
import de.fred4jupiter.fredbet.props.FredBetProfile;
import de.fred4jupiter.fredbet.props.FredbetConstants;
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
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

@Component
public class DatabasePopulator {

    private static final int NUMBER_OF_DEMO_USERS = 12;

    private static final String DEFAULT_PASSWORD_ADMIN_USER = FredbetConstants.TECHNICAL_USERNAME;

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

    private final Faker faker = new Faker();

    public DatabasePopulator(MatchService matchService, Environment environment, UserService userService,
                             BettingService bettingService, RandomValueGenerator randomValueGenerator,
                             InfoService infoService, ImageAdministrationService imageAdministrationService,
                             JokerService jokerService, FredbetProperties fredbetProperties) {
        this.matchService = matchService;
        this.environment = environment;
        this.userService = userService;
        this.bettingService = bettingService;
        this.randomValueGenerator = randomValueGenerator;
        this.infoService = infoService;
        this.imageAdministrationService = imageAdministrationService;
        this.jokerService = jokerService;
        this.fredbetProperties = fredbetProperties;
    }

    public void initDatabaseWithDemoData() {
        if (!isRunInIntegrationTest()) {
            createDefaultUsers();
            addRulesIfEmpty();
        }

        if (!isRunInIntegrationTest() && fredbetProperties.isCreateDemoData()) {
            createDemoUsers(NUMBER_OF_DEMO_USERS);
            createRandomMatches();
        }

        imageAdministrationService.createDefaultImageGroup();
    }

    private boolean isRunInIntegrationTest() {
        return environment.acceptsProfiles(Profiles.of(FredBetProfile.INTEGRATION_TEST));
    }

    public void createRandomMatches() {
        bettingService.deleteAllBets();
        matchService.deleteAllMatches();

        LocalDateTime localDateTime = LocalDateTime.now().plusHours(1);

        localDateTime = createRandomForGroup(localDateTime, Group.GROUP_A, 4);
        localDateTime = createRandomForGroup(localDateTime, Group.GROUP_B, 4);
        localDateTime = createRandomForGroup(localDateTime, Group.GROUP_C, 4);
        localDateTime = createRandomForGroup(localDateTime, Group.GROUP_D, 4);
        localDateTime = createRandomForGroup(localDateTime, Group.GROUP_E, 4);
        localDateTime = createRandomForGroup(localDateTime, Group.GROUP_F, 4);
        localDateTime = createRandomForGroup(localDateTime, Group.GROUP_G, 4);
        localDateTime = createRandomForGroup(localDateTime, Group.GROUP_H, 4);

        localDateTime = createRandomForGroup(localDateTime, Group.ROUND_OF_SIXTEEN, 8);
        localDateTime = createRandomForGroup(localDateTime, Group.QUARTER_FINAL, 4);
        localDateTime = createRandomForGroup(localDateTime, Group.SEMI_FINAL, 2);
        localDateTime = createRandomForGroup(localDateTime, Group.FINAL, 1);
        createRandomForGroup(localDateTime, Group.GAME_FOR_THIRD, 1);
    }

    private LocalDateTime createRandomForGroup(LocalDateTime localDateTime, Group group, int numberOfMatches) {
        LocalDateTime tmpTime = localDateTime;
        for (int i = 0; i < numberOfMatches; i++) {
            ImmutablePair<Country, Country> teamPair = randomValueGenerator.generateTeamPair();
            Match match = MatchBuilder.create().withTeams(teamPair.getLeft(), teamPair.getRight()).withGroup(group).withStadium("Somewhere")
                    .withKickOffDate(tmpTime).build();
            matchService.save(match);

            tmpTime = tmpTime.plusDays(1).plusMinutes(10);
        }

        return tmpTime;
    }

    public void createDemoBetsForAllUsers() {
        LOG.info("createDemoBetsForAllUsers...");
        bettingService.deleteAllBets();

        List<Match> allMatches = matchService.findAll();
        List<AppUser> users = userService.findAll();
        users.forEach(appUser -> {
            for (Match match : allMatches) {
                boolean jokerAllowed = false;
                if (randomValueGenerator.generateRandomBoolean()) {
                    jokerAllowed = jokerService.isSettingJokerAllowed(appUser.getUsername(), match.getId());
                }
                createBetForUser(appUser, match, jokerAllowed);
            }

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

    private void addRulesIfEmpty() {
        ClassPathResource classPathResource = new ClassPathResource("content/rules_de.txt");
        try (ByteArrayOutputStream byteOut = new ByteArrayOutputStream()) {
            IOUtils.copyLarge(classPathResource.getInputStream(), byteOut);
            String rulesInGerman = byteOut.toString(StandardCharsets.UTF_8);

            infoService.saveInfoContentIfNotPresent(InfoType.RULES, rulesInGerman, "de");
        } catch (IOException e) {
            LOG.error(e.getMessage(), e);
        }
    }

    public void createDemoUsers(int numberOfDemoUsers) {
        LOG.info("createAdditionalUsers: creating {} additional demo users ...", numberOfDemoUsers);

        final byte[] demoImage = loadDemoUserProfileImage();

        for (int i = 1; i <= numberOfDemoUsers; i++) {
            final String usernameAndPassword = RandomStringUtils.randomAlphanumeric(6);
            AppUser user = AppUserBuilder.create().withUsernameAndPassword(usernameAndPassword, usernameAndPassword)
                    .withUserGroup(FredBetUserGroup.ROLE_USER).build();
            boolean isNewUser = saveIfNotPresent(user);
            if (isNewUser && faker.random().nextBoolean()) {
                this.imageAdministrationService.saveUserProfileImage(demoImage, user);
            }
        }
    }

    private byte[] loadDemoUserProfileImage() {
        ClassPathResource classPathResource = new ClassPathResource("static/images/profile_demo_image.jpg");
        try (InputStream in = classPathResource.getInputStream()) {
            return IOUtils.toByteArray(in);
        } catch (IOException e) {
            throw new IllegalStateException("Could not load demo image from classpath. " + e.getMessage());
        }
    }

    private void createDefaultUsers() {
        LOG.info("createDefaultUsers: creating default users ...");

        // admin user will also be used for remote shell login
        saveIfNotPresent(AppUserBuilder.create().withUsernameAndPassword(FredbetConstants.TECHNICAL_USERNAME, DEFAULT_PASSWORD_ADMIN_USER)
                .withUserGroup(FredBetUserGroup.ROLE_ADMIN).deletable(false).build());

        List<String> additionalAdminUsers = fredbetProperties.getAdditionalAdminUsers();
        if (additionalAdminUsers != null && !additionalAdminUsers.isEmpty()) {
            additionalAdminUsers.forEach(username -> {
                saveIfNotPresent(AppUserBuilder.create().withUsernameAndPassword(username, username).withUserGroup(FredBetUserGroup.ROLE_ADMIN).build());
            });
        }
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
