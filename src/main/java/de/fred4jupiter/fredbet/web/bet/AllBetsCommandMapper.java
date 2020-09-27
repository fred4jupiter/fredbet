package de.fred4jupiter.fredbet.web.bet;

import de.fred4jupiter.fredbet.domain.Bet;
import de.fred4jupiter.fredbet.domain.Match;
import de.fred4jupiter.fredbet.service.BettingService;
import de.fred4jupiter.fredbet.service.MatchService;
import de.fred4jupiter.fredbet.web.WebMessageUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class AllBetsCommandMapper {

    private static final Logger LOG = LoggerFactory.getLogger(AllBetsCommandMapper.class);

    private final MatchService matchService;

    private final BettingService bettingService;

    private final WebMessageUtil webMessageUtil;

    public AllBetsCommandMapper(MatchService matchService, BettingService bettingService, WebMessageUtil webMessageUtil) {
        this.matchService = matchService;
        this.bettingService = bettingService;
        this.webMessageUtil = webMessageUtil;
    }

    public AllBetsCommand findAllBetsForMatchId(final Long matchId) {
        if (matchId == null) {
            return null;
        }
        Match match = matchService.findMatchById(matchId);
        if (match == null) {
            LOG.warn("Match with matchId={} could not be found!", matchId);
            return null;
        }

        List<Bet> filtered = bettingService.findAllBetsForMatchId(matchId);
        return toAllBetsCommand(match, filtered);
    }

    private AllBetsCommand toAllBetsCommand(Match match, List<Bet> filtered) {
        AllBetsCommand allBetsCommand = new AllBetsCommand();
        allBetsCommand.setAllBetsForMatch(filtered);
        allBetsCommand.setMatch(match);

        allBetsCommand.setTeamNameOne(webMessageUtil.getTeamNameOne(match));
        allBetsCommand.setTeamNameTwo(webMessageUtil.getTeamNameTwo(match));

        allBetsCommand.setCountryTeamOne(match.getTeamOne().getCountry());
        allBetsCommand.setCountryTeamTwo(match.getTeamTwo().getCountry());
        allBetsCommand.setKickOffDate(match.getKickOffDate());
        allBetsCommand.setStadium(match.getStadium());
        allBetsCommand.setGroup(match.getGroup());
        return allBetsCommand;
    }
}
