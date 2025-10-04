package de.fred4jupiter.fredbet.standings;

import de.fred4jupiter.fredbet.common.IntegrationTest;
import de.fred4jupiter.fredbet.data.DatabasePopulator;
import de.fred4jupiter.fredbet.data.DemoDataCreation;
import de.fred4jupiter.fredbet.data.GroupSelection;
import de.fred4jupiter.fredbet.domain.Group;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Locale;

import static org.assertj.core.api.Assertions.assertThat;

@IntegrationTest
public class StandingsServiceIT {

    private static final Logger LOG = LoggerFactory.getLogger(StandingsServiceIT.class);

    @Autowired
    private StandingsService standingsService;

    @Autowired
    private DatabasePopulator databasePopulator;

    @Test
    public void calculateGroupTablePoints() {
        databasePopulator.createDemoData(new DemoDataCreation(GroupSelection.GROUPS_16, true, true));

        StandingsContainer standingsContainer = standingsService.calculateStandings(Locale.of("de", "DE"));
        assertThat(standingsContainer).isNotNull();
        LOG.debug("groupPointsContainer: {}", standingsContainer);

        List<Group> groups = standingsContainer.getGroups();
        groups.forEach(group -> {
            LOG.info("group: {}", group);
            List<TeamStandings> list = standingsContainer.getForGroup(group);
            list.forEach(teamStandings -> LOG.info("groupTeamPoints: {}", teamStandings));
        });
    }
}
