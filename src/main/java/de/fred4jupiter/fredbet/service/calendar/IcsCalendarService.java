package de.fred4jupiter.fredbet.service.calendar;

import de.fred4jupiter.fredbet.domain.Match;
import de.fred4jupiter.fredbet.service.MatchService;
import de.fred4jupiter.fredbet.util.IcsCalendarBuilder;
import de.fred4jupiter.fredbet.util.MessageSourceUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Locale;
import java.util.TimeZone;

@Service
public class IcsCalendarService {

    private static final Logger LOG = LoggerFactory.getLogger(IcsCalendarService.class);

    @Autowired
    private MatchService matchService;

    @Autowired
    private MessageSourceUtil messageSourceUtil;

    public byte[] createCalendarEventFromMatch(Long matchId, Locale locale) {
        Match match = matchService.findByMatchId(matchId);
        if (match == null) {
            LOG.info("Cloud not find match with matchId={}", matchId);
            return null;
        }

        String title = createTitle(match, locale);
        String content = createContent(match, locale);

        return IcsCalendarBuilder.create().withTitle(title).withContent(content)
                .withLocation(match.getStadium())
                .withTimeZone(TimeZone.getDefault().getID())
                .withStartEnd(match.getKickOffDate(), match.getKickOffDate().plusHours(2))
                .build();
    }

    private String createContent(Match match, Locale locale) {
        String group = messageSourceUtil.getMessageFor(match.getGroup().getTitleMsgKey(), locale);
        return messageSourceUtil.getMessageFor("calendar.ics.content", locale, group);
    }

    private String createTitle(Match match, Locale locale) {
        String teamNameOne = messageSourceUtil.getTeamNameOne(match, locale);
        String teamNameTwo = messageSourceUtil.getTeamNameTwo(match, locale);
        return messageSourceUtil.getMessageFor("calendar.ics.title", locale, teamNameOne, teamNameTwo);
    }
}
