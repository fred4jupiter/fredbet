package de.fred4jupiter.fredbet.service.grouppoints;

import de.fred4jupiter.fredbet.IntegrationTest;
import de.fred4jupiter.fredbet.data.DatabasePopulator;
import de.fred4jupiter.fredbet.domain.Group;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Locale;

import static org.assertj.core.api.Assertions.assertThat;

@IntegrationTest
public class GroupPointsServiceIT {

    private static final Logger LOG = LoggerFactory.getLogger(GroupPointsServiceIT.class);

    @Autowired
    private GroupPointsService groupPointsService;

    @Autowired
    private DatabasePopulator databasePopulator;

    @Test
    public void calculateGroupTablePoints() {
        databasePopulator.createRandomMatches();
        databasePopulator.createDemoBetsForAllUsers();
        databasePopulator.createDemoResultsForAllMatches();

        GroupPointsContainer groupPointsContainer = groupPointsService.calculateGroupTablePoints(new Locale("de", "DE"));
        assertThat(groupPointsContainer).isNotNull();
        LOG.debug("groupPointsContainer: {}", groupPointsContainer);

        List<Group> groups = groupPointsContainer.getGroups();
        groups.forEach(group -> {
            LOG.info("group: {}", group);
            List<GroupTeamPoints> list = groupPointsContainer.getForGroup(group);
            list.forEach(groupTeamPoints -> LOG.info("groupTeamPoints: {}", groupTeamPoints));
        });
    }
}
