package de.fred4jupiter.fredbet.teambundle;

import de.fred4jupiter.fredbet.common.UnitTest;
import de.fred4jupiter.fredbet.domain.Country;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@UnitTest
public class TeamBundleProviderUT {

    @InjectMocks
    private TeamBundleProvider teamBundleProvider;

    @Test
    public void clubWorldCupContainsKnownClubTeams() {
        assertThat(teamBundleProvider.getTeams(TeamBundle.CLUB_WM))
            .contains(Country.REAL_MADRID, Country.BAYERN_MUENCHEN)
            .doesNotContain(Country.GERMANY);
    }

    @Test
    public void worldCupExcludesClubWorldCupTeams() {
        List<Country> worldCupTeams = teamBundleProvider.getTeams(TeamBundle.WORLD_CUP);

        assertThat(worldCupTeams).contains(Country.GERMANY);
        assertThat(worldCupTeams).doesNotContain(Country.REAL_MADRID);
    }
}

