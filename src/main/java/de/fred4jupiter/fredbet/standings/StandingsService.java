package de.fred4jupiter.fredbet.standings;

import de.fred4jupiter.fredbet.domain.entity.Match;
import de.fred4jupiter.fredbet.match.MatchService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Locale;

@Service
public class StandingsService {

    private final MatchService matchService;

    public StandingsService(MatchService matchService) {
        this.matchService = matchService;
    }

    public StandingsContainer calculateStandings(Locale locale) {
        final StandingsContainer standingsContainer = new StandingsContainer();

        List<Match> matches = matchService.findAll().stream().filter(Match::hasResultSet).toList();
        matches.forEach(standingsContainer::registerResult);

        return standingsContainer;
    }

}
