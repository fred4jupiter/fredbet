package de.fred4jupiter.fredbet.service.grouppoints;

import de.fred4jupiter.fredbet.domain.Match;
import de.fred4jupiter.fredbet.service.MatchService;
import de.fred4jupiter.fredbet.util.MessageSourceUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@Service
public class GroupPointsService {

    @Autowired
    private MatchService matchService;

    @Autowired
    private MessageSourceUtil messageSourceUtil;

    public GroupPointsContainer calculateGroupTablePoints(Locale locale) {
        final GroupPointsContainer groupPointsContainer = new GroupPointsContainer(messageSourceUtil);

        List<Match> matches = matchService.findAll().stream().filter(Match::hasResultSet).collect(Collectors.toList());
        matches.forEach(match -> {
            groupPointsContainer.registerResult(match, locale);
        });

        return groupPointsContainer;
    }

}
