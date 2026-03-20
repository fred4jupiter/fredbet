package de.fred4jupiter.fredbet.util;

import de.fred4jupiter.fredbet.common.UnitTest;
import de.fred4jupiter.fredbet.domain.Group;
import de.fred4jupiter.fredbet.domain.builder.MatchBuilder;
import de.fred4jupiter.fredbet.domain.entity.Match;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.TimeZone;

import static org.assertj.core.api.Assertions.assertThat;

@UnitTest
public class IcsCalendarBuilderUT {

    @Test
    void createIcsFile() {
        Match match = MatchBuilder.create()
            .withGroup(Group.GROUP_A)
            .withTeams("A", "B")
            .withKickOffDate(LocalDateTime.now())
            .build();

        byte[] binary = IcsCalendarBuilder.create().withTitle("Some match").withContent("There is a match")
            .withLocation(match.getStadium())
            .withTimeZone(TimeZone.getDefault().getID())
            .withStartEnd(match.getKickOffDate(), match.getKickOffDate())
            .build();
        assertThat(binary).isNotEmpty();

        boolean result = TempFileWriterUtil.writeToTempFolder(binary, "example.ical");
        assertThat(result).isTrue();
    }
}
