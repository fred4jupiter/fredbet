package de.fred4jupiter.fredbet.data;

import de.fred4jupiter.fredbet.betting.BettingService;
import de.fred4jupiter.fredbet.match.MatchService;
import de.fred4jupiter.fredbet.team.TeamService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Service
public class DataService {

    private final MatchService matchService;

    private final TeamService teamService;

    private final BettingService bettingService;

    public DataService(MatchService matchService, TeamService teamService, BettingService bettingService) {
        this.matchService = matchService;
        this.teamService = teamService;
        this.bettingService = bettingService;
    }

    @Transactional
    public void deleteAllBetsMatchesAndTeams() {
        bettingService.deleteAllBets();
        matchService.deleteAllMatches();
        teamService.deleteAllTeams();
    }
}
