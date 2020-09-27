package de.fred4jupiter.fredbet.service.calendar;

import de.fred4jupiter.fredbet.domain.Match;
import de.fred4jupiter.fredbet.service.MatchService;
import de.fred4jupiter.fredbet.util.IcsCalendarBuilder;
import de.fred4jupiter.fredbet.util.MessageSourceUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.TimeZone;

@Service
public class IcsCalendarService {

    private static final Logger LOG = LoggerFactory.getLogger(IcsCalendarService.class);

    private final MatchService matchService;

    private final MessageSourceUtil messageSourceUtil;

    public IcsCalendarService(MatchService matchService, MessageSourceUtil messageSourceUtil) {
        this.matchService = matchService;
        this.messageSourceUtil = messageSourceUtil;
    }

    public IcsFile createCalendarEventFromMatch(Long matchId, Locale locale) {
        Match match = matchService.findByMatchId(matchId);
        if (match == null) {
            LOG.info("Cloud not find match with matchId={}", matchId);
            return null;
        }

        String title = createTitle(match, locale);
        String content = createContent(match, locale);

        byte[] binary = IcsCalendarBuilder.create().withTitle(title).withContent(content)
                .withLocation(match.getStadium())
                .withTimeZone(TimeZone.getDefault().getID())
                .withStartEnd(match.getKickOffDate(), match.getKickOffDate().plusHours(2))
                .build();

        return new IcsFile(binary, createFileName(match));
    }

    private String createFileName(Match match) {
        String timestamp = match.getKickOffDate().format(DateTimeFormatter.ofPattern("yyyyMMddHHmm"));
        return "Match_" + timestamp + "_" + match.getId() + ".ics";
    }

    private String createContent(Match match, Locale locale) {
        return messageSourceUtil.getMessageFor(match.getGroup().getTitleMsgKey(), locale);
    }

    private String createTitle(Match match, Locale locale) {
        String teamNameOne = messageSourceUtil.getTeamNameOne(match, locale);
        String teamNameTwo = messageSourceUtil.getTeamNameTwo(match, locale);
        return messageSourceUtil.getMessageFor("calendar.ics.title", locale, teamNameOne, teamNameTwo);
    }
}
