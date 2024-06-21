package de.fred4jupiter.fredbet.imexport;

import de.fred4jupiter.fredbet.domain.*;
import de.fred4jupiter.fredbet.service.BettingService;
import de.fred4jupiter.fredbet.service.MatchService;
import de.fred4jupiter.fredbet.service.user.UserService;
import de.fred4jupiter.fredbet.service.user.UserToExport;
import de.fred4jupiter.fredbet.util.JsonObjectConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class JsonExportService {

    private static final Logger LOG = LoggerFactory.getLogger(JsonExportService.class);

    private final JsonObjectConverter jsonObjectConverter;

    private final MatchService matchService;

    private final BettingService bettingService;

    private final UserService userService;

    public JsonExportService(JsonObjectConverter jsonObjectConverter, MatchService matchService, BettingService bettingService, UserService userService) {
        this.jsonObjectConverter = jsonObjectConverter;
        this.matchService = matchService;
        this.bettingService = bettingService;
        this.userService = userService;
    }

    public String exportAllToJson() {
        LOG.debug("start export to JSON...");
        final ImportExportContainer importExportContainer = new ImportExportContainer();
        List<AppUser> allUsers = userService.findAll();
        importExportContainer.setUsers(allUsers.stream().filter(AppUser::isDeletable).map(this::toUserToExport).toList());
        LOG.debug("exported users");

        List<Match> allMatches = matchService.findAll();
        importExportContainer.setMatches(allMatches.stream().map(this::toMatchToExport).toList());
        LOG.debug("exported matches");

        List<Bet> allBetting = bettingService.findAll();
        importExportContainer.setBets(allBetting.stream().map(this::toBetToExport).toList());
        LOG.debug("exported bets");

        List<ExtraBet> allExtraBets = bettingService.findAllExtraBets();
        importExportContainer.setExtraBets(allExtraBets.stream().map(this::toExtraBetToExport).toList());
        LOG.debug("exported extra bets");

        return jsonObjectConverter.toJson(importExportContainer);
    }

    private ExtraBetToExport toExtraBetToExport(ExtraBet extraBet) {
        ExtraBetToExport export = new ExtraBetToExport();
        export.setUserName(extraBet.getUserName());
        export.setFinalWinner(extraBet.getFinalWinner());
        export.setSemiFinalWinner(extraBet.getSemiFinalWinner());
        export.setThirdFinalWinner(extraBet.getThirdFinalWinner());
        export.setPointsOne(extraBet.getPointsOne());
        export.setPointsTwo(extraBet.getPointsTwo());
        export.setPointsThree(extraBet.getPointsThree());
        return export;
    }

    private BetToExport toBetToExport(Bet bet) {
        BetToExport export = new BetToExport();
        export.setJoker(bet.isJoker());
        export.setGoalsTeamOne(bet.getGoalsTeamOne());
        export.setGoalsTeamTwo(bet.getGoalsTeamTwo());
        if (bet.getMatch() != null) {
            export.setMatchBusinessKey(bet.getMatch().getBusinessKey());
        }
        export.setUsername(bet.getUserName());
        export.setPoints(bet.getPoints());
        export.setPenaltyWinnerOne(bet.isPenaltyWinnerOne());
        return export;
    }

    private MatchToExport toMatchToExport(Match match) {
        MatchToExport export = new MatchToExport();
        export.setTeamOne(match.getTeamOne());
        export.setTeamTwo(match.getTeamTwo());
        export.setGroup(match.getGroup());
        export.setStadium(match.getStadium());
        export.setKickOffDate(match.getKickOffDate());
        export.setPenaltyWinnerOne(match.isPenaltyWinnerOne());
        export.setMatchBusinessKey(match.getBusinessKey());
        return export;
    }

    private UserToExport toUserToExport(AppUser appUser) {
        UserToExport userToExport = new UserToExport();
        userToExport.setUsername(appUser.getUsername());
        userToExport.setPassword(appUser.getPassword());
        userToExport.setChild(appUser.isChild());
        userToExport.setRoles(appUser.getRoles());
        return userToExport;
    }

}
