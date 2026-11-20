package de.fred4jupiter.fredbet.csvimport;

import de.fred4jupiter.fredbet.domain.Group;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class GroupResolverUT {

    private final GroupResolver groupResolver = new GroupResolver();

    @Test
    public void resolveGroups() {
        assertThat(groupResolver.resolve("GROUP_A")).isEqualTo(Group.GROUP_A);
        assertThat(groupResolver.resolve("b")).isEqualTo(Group.GROUP_B);
        assertThat(groupResolver.resolve("Group C")).isEqualTo(Group.GROUP_C);
        assertThat(groupResolver.resolve("Gruppe D")).isEqualTo(Group.GROUP_D);
        assertThat(groupResolver.resolve("LAST_16")).isEqualTo(Group.ROUND_OF_SIXTEEN);
        assertThat(groupResolver.resolve("Achtelfinale")).isEqualTo(Group.ROUND_OF_SIXTEEN);
        assertThat(groupResolver.resolve("Final")).isEqualTo(Group.FINAL);
    }

    @Test
    public void unknownGroupIsNotResolved() {
        assertThat(groupResolver.resolve("Group Z")).isNull();
        assertThat(groupResolver.resolve("")).isNull();
    }
}
