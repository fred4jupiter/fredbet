package de.fred4jupiter.fredbet.data;

import com.github.javafaker.Faker;
import de.fred4jupiter.fredbet.admin.CacheAdministrationService;
import de.fred4jupiter.fredbet.betting.BettingService;
import de.fred4jupiter.fredbet.betting.ExtraBettingService;
import de.fred4jupiter.fredbet.betting.JokerService;
import de.fred4jupiter.fredbet.domain.Group;
import de.fred4jupiter.fredbet.domain.builder.AppUserBuilder;
import de.fred4jupiter.fredbet.domain.builder.MatchBuilder;
import de.fred4jupiter.fredbet.domain.entity.AppUser;
import de.fred4jupiter.fredbet.domain.entity.Match;
import de.fred4jupiter.fredbet.domain.entity.Team;
import de.fred4jupiter.fredbet.match.MatchService;
import de.fred4jupiter.fredbet.security.FredBetUserGroup;
import de.fred4jupiter.fredbet.team.TeamService;
import de.fred4jupiter.fredbet.user.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;
import java.util.function.Consumer;
import java.util.stream.IntStream;

@Component
@Transactional
public class DataPopulator {

    private static final Logger LOG = LoggerFactory.getLogger(DataPopulator.class);

    /**
     * Each demo match needs two different teams.
     */
    private static final int MIN_TEAMS_FOR_DEMO_DATA = 2;

    private final MatchService matchService;

    private final UserService userService;

    private final BettingService bettingService;

    private final RandomValueGenerator randomValueGenerator;

    private final JokerService jokerService;

    private final ExtraBettingService extraBettingService;

    private final Faker faker = new Faker(Locale.getDefault());

    private final CacheAdministrationService cacheAdministrationService;

    private final TeamService teamService;

    private final DataService dataService;

    public DataPopulator(MatchService matchService, UserService userService,
                         BettingService bettingService, RandomValueGenerator randomValueGenerator,
                         JokerService jokerService, ExtraBettingService extraBettingService,
                         CacheAdministrationService cacheAdministrationService,
                         TeamService teamService, DataService dataService) {
        this.matchService = matchService;
        this.userService = userService;
        this.bettingService = bettingService;
        this.randomValueGenerator = randomValueGenerator;
        this.jokerService = jokerService;
        this.extraBettingService = extraBettingService;
        this.cacheAdministrationService = cacheAdministrationService;
        this.teamService = teamService;
        this.dataService = dataService;
    }

    @Async
    public void executeAsync(Consumer<DataPopulator> populatorCallback) {
        populatorCallback.accept(this);
    }

    public void createDemoData() {
        createDemoData(new DemoDataCreation(GroupSelection.ROUND_OF_SIXTEEN, true, true, true));
    }

    /**
     * Demo matches are created with the teams stored in the database, so these teams have to be created first.
     */
    public boolean hasEnoughTeamsForDemoData() {
        return teamService.findAllSelectableTeams().size() >= MIN_TEAMS_FOR_DEMO_DATA;
    }

    public void createDemoData(DemoDataCreation demoDataCreation) {
        final List<Team> teams = teamService.findAllSelectableTeams();
        if (teams.size() < MIN_TEAMS_FOR_DEMO_DATA) {
            throw new IllegalStateException("At least %d teams are needed to create demo data, but found %d!".formatted(MIN_TEAMS_FOR_DEMO_DATA, teams.size()));
        }

        dataService.deleteAllBetsMatchesAndTeams();

        final KickOffDateCreator kickOffDateCreator = new KickOffDateCreator();

        final GroupSelection groupSelection = demoDataCreation.groupSelection();

        // create group matches
        createMatchesForGroup(teams, kickOffDateCreator, Group.GROUP_A, 4);
        createMatchesForGroup(teams, kickOffDateCreator, Group.GROUP_B, 4);
        createMatchesForGroup(teams, kickOffDateCreator, Group.GROUP_C, 4);
        createMatchesForGroup(teams, kickOffDateCreator, Group.GROUP_D, 4);
        createMatchesForGroup(teams, kickOffDateCreator, Group.GROUP_E, 4);
        createMatchesForGroup(teams, kickOffDateCreator, Group.GROUP_F, 4);
        createMatchesForGroup(teams, kickOffDateCreator, Group.GROUP_G, 4);
        createMatchesForGroup(teams, kickOffDateCreator, Group.GROUP_H, 4);

        if (GroupSelection.ROUND_OF_THIRTY_TWO.equals(groupSelection)) {
            createMatchesForGroup(teams, kickOffDateCreator, Group.GROUP_I, 4);
            createMatchesForGroup(teams, kickOffDateCreator, Group.GROUP_J, 4);
            createMatchesForGroup(teams, kickOffDateCreator, Group.GROUP_K, 4);
            createMatchesForGroup(teams, kickOffDateCreator, Group.GROUP_L, 4);

            createMatchesForGroup(teams, kickOffDateCreator, Group.ROUND_OF_THIRTY_TWO, 16);
        }

        createMatchesForGroup(teams, kickOffDateCreator, Group.ROUND_OF_SIXTEEN, 8);
        createMatchesForGroup(teams, kickOffDateCreator, Group.QUARTER_FINAL, 4);
        createMatchesForGroup(teams, kickOffDateCreator, Group.SEMI_FINAL, 2);
        createMatchesForGroup(teams, kickOffDateCreator, Group.FINAL, 1);

        if (demoDataCreation.withGameOfThird()) {
            createMatchesForGroup(teams, kickOffDateCreator, Group.GAME_FOR_THIRD, 1);
        }

        if (demoDataCreation.withBets()) {
            createDemoBetsForAllUsers();
        }
        if (demoDataCreation.withResults()) {
            createDemoResultsForAllMatches();
        }

        // clear all caches
        cacheAdministrationService.clearCaches();
    }

    private void createMatchesForGroup(List<Team> teams, KickOffDateCreator kickOffDateCreator,
                                       Group group, int numberOfMatches) {
        LOG.debug("creating group {} with {} matches.", group, numberOfMatches);

        IntStream.rangeClosed(1, numberOfMatches).forEach(counter -> {
            TeamPair teamPair = randomValueGenerator.generateTeamPair(teams);
            Match match = MatchBuilder.create(teamService)
                .withTeams(teamPair.teamOne(), teamPair.teamTwo())
                .withGroup(group)
                .withStadium(nextStadium())
                .withKickOffDate(kickOffDateCreator.createNextKickOffDate()).build();
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

            extraBettingService.createExtraBetForUser(appUser.getUsername());
        });
        LOG.debug("created demo bets for all users finished.");
    }

    private void createBetForUser(AppUser appUser, Match match, boolean joker) {
        GoalResult goalResult = randomValueGenerator.generateGoalResult();

        bettingService.createAndSaveBetting(builder -> {
            builder.withGoals(goalResult.goalsTeamOne(), goalResult.goalsTeamTwo()).withJoker(joker).withUserName(appUser.getUsername()).withMatch(match);
        });
    }

    public void createDemoResultsForAllMatches() {
        LOG.info("createDemoResultsForAllUsers...");
        matchService.enterMatchResultsForAllMatches(match -> {
            GoalResult goalResult = randomValueGenerator.generateGoalResult();
            match.setGoalsTeamOne(goalResult.goalsTeamOne());
            match.setGoalsTeamTwo(goalResult.goalsTeamTwo());
        });
        LOG.info("created demo results for all users");
    }

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


    // TODO: remove this method
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
