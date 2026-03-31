package de.fred4jupiter.fredbet.integration;

import de.fred4jupiter.fredbet.common.IntegrationTest;
import de.fred4jupiter.fredbet.domain.Country;
import de.fred4jupiter.fredbet.integration.model.FdTeam;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;

@IntegrationTest
public class TeamNameToCountryResolverIT {

    @Autowired
    private TeamNameToCountryResolver resolver;

    @Test
    void resolveByTla() {
        FdTeam team = new FdTeam("1", "Germany", "DEU");
        Country country = resolver.resolveToCountry(team);
        assertThat(country).isEqualTo(Country.GERMANY);
    }

    @Test
    void resolveByName() {
        // provide an unknown but non-null TLA so resolver falls back to name-based lookup
        FdTeam team = new FdTeam("2", "Germany", "XXX");
        Country country = resolver.resolveToCountry(team);
        assertThat(country).isEqualTo(Country.GERMANY);
    }

    @Test
    void unknownTeamReturnsNull() {
        FdTeam team = new FdTeam("3", "Some Unknown Team", "XXX");
        assertThat(resolver.resolveToCountry(team)).isNull();
    }

    @Test
    void nullTeamReturnsNull() {
        assertThat(resolver.resolveToCountry(null)).isNull();
    }
}


