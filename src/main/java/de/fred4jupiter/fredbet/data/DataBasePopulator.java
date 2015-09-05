package de.fred4jupiter.fredbet.data;

import java.time.LocalDateTime;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import de.fred4jupiter.fredbet.FredBetRole;
import de.fred4jupiter.fredbet.domain.AppUser;
import de.fred4jupiter.fredbet.domain.Group;
import de.fred4jupiter.fredbet.domain.Match;
import de.fred4jupiter.fredbet.domain.MatchBuilder;
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

    @PostConstruct
    public void initDatabaseWithDemoData() {
        LOG.info("initDatabaseWithDemoData: inserting demo data...");

        createDefaultUsers();

        // this we be executed in demodata profile only
        if (environment.acceptsProfiles("demodata")) {
            Match match1 = MatchBuilder.create().withTeams("Bulgarien", "Irland").withGroup(Group.GROUP_A)
                    .withStadium("Westfalenstadium, Dortmund").withKickOffDate(LocalDateTime.now().plusMinutes(5)).build();
            matchService.save(match1);

            Match match2 = MatchBuilder.create().withTeams("Deutschland", "Frankfreich").withGroup(Group.GROUP_B)
                    .withStadium("Weserstadium, bremen").withKickOffDate(LocalDateTime.now().plusMinutes(10)).build();
            matchService.save(match2);

            Match match3 = MatchBuilder.create().withTeams("Belgien", "England").withGroup(Group.GROUP_D).withStadium("AOL Arena, München")
                    .withKickOffDate(LocalDateTime.now().plusMinutes(15)).build();
            matchService.save(match3);
        }
    }

    private void createDefaultUsers() {
        // will also be used for remote shell login
        userService.save(new AppUser("admin", "admin", FredBetRole.ROLE_USER, FredBetRole.ROLE_ADMIN, FredBetRole.ROLE_EDIT_MATCH));
        
        userService.save(new AppUser("edit", "edit", FredBetRole.ROLE_USER, FredBetRole.ROLE_EDIT_MATCH));
        userService.save(new AppUser("normal", "normal", FredBetRole.ROLE_USER));
    }

}
