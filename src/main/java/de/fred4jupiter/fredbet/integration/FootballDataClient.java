package de.fred4jupiter.fredbet.integration;

import de.fred4jupiter.fredbet.integration.model.FdCompetitionList;
import de.fred4jupiter.fredbet.integration.model.FdMatches;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.service.annotation.GetExchange;

public interface FootballDataClient {

    @GetExchange("/competitions")
    FdCompetitionList fetchCompetitions();

    @GetExchange("/competitions/{competitionCode}/matches")
    FdMatches fetchMatches(@PathVariable String competitionCode, @RequestParam("season") Integer seasonYear);
}
