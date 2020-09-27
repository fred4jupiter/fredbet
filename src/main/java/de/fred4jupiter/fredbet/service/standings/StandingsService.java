package de.fred4jupiter.fredbet.service.standings;

import de.fred4jupiter.fredbet.domain.Match;
import de.fred4jupiter.fredbet.service.MatchService;
import de.fred4jupiter.fredbet.util.MessageSourceUtil;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@Service
public class StandingsService {

    private final MatchService matchService;

    private final MessageSourceUtil messageSourceUtil;

    public StandingsService(MatchService matchService, MessageSourceUtil messageSourceUtil) {
        this.matchService = matchService;
        this.messageSourceUtil = messageSourceUtil;
    }

    public StandingsContainer calculateStandings(Locale locale) {
        final StandingsContainer standingsContainer = new StandingsContainer(messageSourceUtil);

        List<Match> matches = matchService.findAll().stream().filter(Match::hasResultSet).collect(Collectors.toList());
        matches.forEach(match -> {
            standingsContainer.registerResult(match, locale);
        });

        return standingsContainer;
    }

}
