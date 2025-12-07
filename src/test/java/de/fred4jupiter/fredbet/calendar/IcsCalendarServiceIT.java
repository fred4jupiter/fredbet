package de.fred4jupiter.fredbet.calendar;

import de.fred4jupiter.fredbet.common.TransactionalIntegrationTest;
import de.fred4jupiter.fredbet.domain.Group;
import de.fred4jupiter.fredbet.domain.builder.MatchBuilder;
import de.fred4jupiter.fredbet.domain.entity.Match;
import de.fred4jupiter.fredbet.match.MatchRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.Locale;

import static org.assertj.core.api.Assertions.assertThat;

@TransactionalIntegrationTest
public class IcsCalendarServiceIT {

    @Autowired
    private IcsCalendarService icsCalendarService;

    @Autowired
    private MatchRepository matchRepository;

    @Test
    void createCalendarEventFromMatch() {
        Match match1 = MatchBuilder.create().withGroup(Group.GROUP_A).withTeams("A", "B").withKickOffDate(LocalDateTime.MAX).build();
        Match savedMatch = matchRepository.save(match1);

        IcsFile icsFile = icsCalendarService.createCalendarEventFromMatch(savedMatch.getId(), Locale.GERMAN);
        assertThat(icsFile).isNotNull();
        assertThat(icsFile.fileName()).isNotNull();
        assertThat(icsFile.binary()).isNotNull();
    }
}
