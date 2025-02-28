package de.fred4jupiter.fredbet.service;

import de.fred4jupiter.fredbet.common.TransactionalIntegrationTest;
import de.fred4jupiter.fredbet.data.DatabasePopulator;
import de.fred4jupiter.fredbet.domain.AppUser;
import de.fred4jupiter.fredbet.domain.AppUserBuilder;
import de.fred4jupiter.fredbet.domain.RankingSelection;
import de.fred4jupiter.fredbet.props.FredbetProperties;
import de.fred4jupiter.fredbet.repository.UsernamePoints;
import de.fred4jupiter.fredbet.service.ranking.RankingService;
import de.fred4jupiter.fredbet.user.UserAlreadyExistsException;
import de.fred4jupiter.fredbet.user.UserService;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@TransactionalIntegrationTest
public class RankingServiceIT {

    private static final Logger LOG = LoggerFactory.getLogger(RankingServiceIT.class);

    @Autowired
    private DatabasePopulator dataBasePopulator;

    @Autowired
    private RankingService rankingService;

    @Autowired
    private UserService userService;

    @Autowired
    private FredbetProperties fredbetProperties;

    @Test
    public void getRankingForUsersButWithoutTechnicalUser() {
        saveIfNotPresent(AppUserBuilder.create().withDemoData().build());
        saveIfNotPresent(AppUserBuilder.create().withDemoData().build());
        saveIfNotPresent(AppUserBuilder.create().withDemoData().build());
        saveIfNotPresent(AppUserBuilder.create().withDemoData().build());

        final String username = fredbetProperties.adminUsername();
        saveIfNotPresent(AppUserBuilder.create().withDemoData().withUsernameAndPassword(username, "test").build());

        dataBasePopulator.createRandomMatches();
        dataBasePopulator.createDemoBetsForAllUsers();
        dataBasePopulator.createDemoResultsForAllMatches();

        List<UsernamePoints> rankings = rankingService.calculateCurrentRanking(RankingSelection.MIXED);
        assertNotNull(rankings);
        assertFalse(rankings.isEmpty());

        assertThat(rankings).extracting(UsernamePoints::getUserName).doesNotContain(username);
    }

    @Test
    public void getRankingForAdultUsers() {
        saveIfNotPresent(AppUserBuilder.create().withDemoData().withUsernameAndPassword("fred", "fred").withIsChild(true).build());
        saveIfNotPresent(AppUserBuilder.create().withDemoData().withUsernameAndPassword("holger", "holger").withIsChild(false).build());

        dataBasePopulator.createRandomMatches();
        dataBasePopulator.createDemoBetsForAllUsers();
        dataBasePopulator.createDemoResultsForAllMatches();

        List<UsernamePoints> rankings = rankingService.calculateCurrentRanking(RankingSelection.ONLY_ADULTS);
        assertNotNull(rankings);
        assertFalse(rankings.isEmpty());

        assertThat(rankings).extracting(UsernamePoints::getUserName).doesNotContain("fred");
        assertThat(rankings).extracting(UsernamePoints::getUserName).contains("holger");
    }

    @Test
    public void getRankingForChildUsers() {
        saveIfNotPresent(AppUserBuilder.create().withDemoData().withUsernameAndPassword("fred", "fred").withIsChild(true).build());
        saveIfNotPresent(AppUserBuilder.create().withDemoData().withUsernameAndPassword("holger", "holger").withIsChild(false).build());

        dataBasePopulator.createRandomMatches();
        dataBasePopulator.createDemoBetsForAllUsers();
        dataBasePopulator.createDemoResultsForAllMatches();

        List<UsernamePoints> rankings = rankingService.calculateCurrentRanking(RankingSelection.ONLY_CHILDREN);
        assertNotNull(rankings);
        assertFalse(rankings.isEmpty());
        assertThat(rankings).extracting(UsernamePoints::getUserName).doesNotContain("holger");
        assertThat(rankings).extracting(UsernamePoints::getUserName).contains("fred");
    }

    private void saveIfNotPresent(AppUser appUser) {
        try {
            userService.createUser(appUser);
        } catch (UserAlreadyExistsException e) {
            LOG.debug(e.getMessage());
        }
    }
}
