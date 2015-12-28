package de.fred4jupiter.fredbet.data;

import java.time.LocalDateTime;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import de.fred4jupiter.fredbet.FredBetProfile;
import de.fred4jupiter.fredbet.FredBetRole;
import de.fred4jupiter.fredbet.domain.AppUser;
import de.fred4jupiter.fredbet.domain.Group;
import de.fred4jupiter.fredbet.domain.MatchBuilder;
import de.fred4jupiter.fredbet.service.BettingService;
import de.fred4jupiter.fredbet.service.MatchService;
import de.fred4jupiter.fredbet.service.UserService;

@Component
public class DataBasePopulator {

    private static final Logger LOG = LoggerFactory.getLogger(DataBasePopulator.class);

    @Autowired
    private MatchService matchService;

    @Autowired
    private Environment environment;

    @Autowired
    private UserService userService;

    @Autowired
    private BettingService bettingService;

    @PostConstruct
    private void initDatabaseWithDemoData() {
        createDefaultUsers();

        if (environment.acceptsProfiles(FredBetProfile.DEMODATA)) {
            createDemoData();
        }
    }

    /**
     * Deletes all current bets and matches and inserts new demo data.
     */
    public void createDemoData() {
    	LOG.info("createDemoData: deleting all existend bets and matches ...");
		bettingService.deleteAllBets();
		matchService.deleteAllMatches();

		createMatches();
	}

    private void createMatches() {
    	LOG.info("createMatches: inserting demo matches ...");
        matchService.save(MatchBuilder.create().withTeams("Frankreich", "Rumänien").withGroup(Group.GROUP_A)
                .withStadium("Saint-Denis").withKickOffDate(LocalDateTime.of(2016, 6, 10, 21, 0)).build());

        matchService.save(MatchBuilder.create().withTeams("Albanien", "Schweiz").withGroup(Group.GROUP_A)
                .withStadium("Lens").withKickOffDate(LocalDateTime.of(2016, 6, 11, 15, 0)).build());

        matchService.save(MatchBuilder.create().withTeams("Wales", "Slowakei").withGroup(Group.GROUP_B)
                .withStadium("Bordeaux").withKickOffDate(LocalDateTime.of(2016, 6, 11, 18, 0)).build());

        matchService.save(MatchBuilder.create().withTeams("England", "Russland").withGroup(Group.GROUP_B)
                .withStadium("Marseille").withKickOffDate(LocalDateTime.of(2016, 6, 11, 21, 0)).build());

        matchService.save(MatchBuilder.create().withTeams("Türkei", "Kroatien").withGroup(Group.GROUP_D)
                .withStadium("Paris").withKickOffDate(LocalDateTime.of(2016, 6, 12, 15, 0)).build());
    }

    private void createDefaultUsers() {
    	LOG.info("createDefaultUsers: creating default users ...");
        // will also be used for remote shell login
        userService.save(new AppUser("admin", "Pinky4Ever", false, FredBetRole.ROLE_USER, FredBetRole.ROLE_ADMIN, FredBetRole.ROLE_EDIT_MATCH));
        userService.save(new AppUser("michael", "Pinky4Ever", FredBetRole.ROLE_USER, FredBetRole.ROLE_ADMIN, FredBetRole.ROLE_EDIT_MATCH));

        userService.save(new AppUser("janz", "janz", FredBetRole.ROLE_USER, FredBetRole.ROLE_EDIT_MATCH));
        userService.save(new AppUser("joernf", "joernf", FredBetRole.ROLE_USER, FredBetRole.ROLE_EDIT_MATCH));

        userService.save(new AppUser("edit", "edit", FredBetRole.ROLE_USER, FredBetRole.ROLE_EDIT_MATCH));
        userService.save(new AppUser("normal", "normal", FredBetRole.ROLE_USER));
    }
}
