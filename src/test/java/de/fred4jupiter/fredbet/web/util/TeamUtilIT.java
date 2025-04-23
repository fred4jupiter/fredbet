package de.fred4jupiter.fredbet.web.util;

import de.fred4jupiter.fredbet.common.TransactionalIntegrationTest;
import de.fred4jupiter.fredbet.domain.Country;
import de.fred4jupiter.fredbet.domain.Group;
import de.fred4jupiter.fredbet.domain.builder.MatchBuilder;
import de.fred4jupiter.fredbet.match.MatchRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;

import static org.assertj.core.api.Assertions.assertThat;

@TransactionalIntegrationTest
public class TeamUtilIT {

    @Autowired
    private TeamUtil teamUtil;

    @Autowired
    private MatchRepository matchRepository;

    @BeforeEach
    public void setup() {
        matchRepository.deleteAllInBatch();
        LocaleContextHolder.setDefaultLocale(Locale.ENGLISH);
    }

    @Test
    void resolveTeamName() {
        assertThat(teamUtil.i18n(Country.GERMANY)).isEqualTo("Germany");
    }

    @Test
    void resolveTeamNameForChampionsLeagueMember() {
        assertThat(teamUtil.i18n(Country.MANCHESTER_CITY)).isEqualTo("Manchester City");
    }

    @Test
    void getAvailableTeams() {
        List<TeamView> availableTeams = teamUtil.getAvailableTeams();
        assertThat(availableTeams).isNotEmpty();
        availableTeams.forEach(teamView -> {
            assertThat(teamView).isNotNull();
            assertThat(teamView.country()).isNotNull();
            assertThat(teamView.teamName()).isNotNull();
        });

    }

    @Test
    public void getAvailableCountriesForExtraBets() {
        matchRepository.save(MatchBuilder.create().withTeams(Country.GERMANY, Country.FRANCE).withGroup(Group.GROUP_B)
            .withStadium("Weserstadium, bremen").withKickOffDate(LocalDateTime.now().plusMinutes(20)).withGoals(1, 2).build());

        matchRepository.save(MatchBuilder.create().withTeams(Country.BULGARIA, Country.IRELAND).withGroup(Group.GROUP_A)
            .withStadium("Westfalenstadium, Dortmund").withKickOffDate(LocalDateTime.now().plusMinutes(10)).withGoals(1, 2).build());

        List<TeamView> availableTeamsBasedOnMatches = teamUtil.getAvailableTeamsBasedOnMatches();
        assertThat(availableTeamsBasedOnMatches).hasSize(4);

        List<Country> countries = availableTeamsBasedOnMatches.stream().map(TeamView::country).sorted().toList();
        assertThat(countries).contains(Country.BULGARIA, Country.GERMANY, Country.FRANCE, Country.IRELAND);
    }
}
