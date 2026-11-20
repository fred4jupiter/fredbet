package de.fred4jupiter.fredbet.csvimport;

import de.fred4jupiter.fredbet.domain.ClubTeam;
import de.fred4jupiter.fredbet.domain.Country;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class TeamNameResolverUT {

    private final TeamNameResolver teamNameResolver = new TeamNameResolver();

    @Test
    public void resolveByEnumNameQualifiedNameAndAlpha3Code() {
        assertThat(teamNameResolver.resolve("GERMANY")).isEqualTo(Country.GERMANY);
        assertThat(teamNameResolver.resolve("COUNTRY:FRANCE")).isEqualTo(Country.FRANCE);
        assertThat(teamNameResolver.resolve("deu")).isEqualTo(Country.GERMANY);
        assertThat(teamNameResolver.resolve("BAYERN_MUENCHEN")).isEqualTo(ClubTeam.BAYERN_MUENCHEN);
    }

    @Test
    public void resolveByTranslatedNameIgnoringCaseAndAccents() {
        assertThat(teamNameResolver.resolve("Germany")).isEqualTo(Country.GERMANY);
        assertThat(teamNameResolver.resolve("deutschland")).isEqualTo(Country.GERMANY);
        assertThat(teamNameResolver.resolve("Allemagne")).isEqualTo(Country.GERMANY);
        assertThat(teamNameResolver.resolve("Real Madrid")).isEqualTo(ClubTeam.REAL_MADRID);
    }

    @Test
    public void unknownTeamIsNotResolved() {
        assertThat(teamNameResolver.resolve("FC Somewhere")).isNull();
        assertThat(teamNameResolver.resolve("UNKNOWN:TEAM")).isNull();
        assertThat(teamNameResolver.resolve("  ")).isNull();
    }
}
