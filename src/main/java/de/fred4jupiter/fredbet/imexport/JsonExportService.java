package de.fred4jupiter.fredbet.imexport;

import de.fred4jupiter.fredbet.betting.BettingService;
import de.fred4jupiter.fredbet.betting.ExtraBettingService;
import de.fred4jupiter.fredbet.domain.entity.AppUser;
import de.fred4jupiter.fredbet.domain.entity.Bet;
import de.fred4jupiter.fredbet.domain.entity.ExtraBet;
import de.fred4jupiter.fredbet.domain.entity.Match;
import de.fred4jupiter.fredbet.match.MatchService;
import de.fred4jupiter.fredbet.user.UserService;
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

    private final ExtraBettingService extraBettingService;

    private final UserService userService;

    private final UserImportExportHelper userImportExportHelper;

    JsonExportService(JsonObjectConverter jsonObjectConverter, MatchService matchService,
                      BettingService bettingService, ExtraBettingService extraBettingService, UserService userService,
                      UserImportExportHelper userImportExportHelper) {
        this.jsonObjectConverter = jsonObjectConverter;
        this.matchService = matchService;
        this.bettingService = bettingService;
        this.extraBettingService = extraBettingService;
        this.userService = userService;
        this.userImportExportHelper = userImportExportHelper;
    }

    public String exportAllToJson() {
        LOG.debug("start export to JSON...");
        List<AppUser> allUsers = userService.findAll();
        List<UserToExport> exportUsers = allUsers.stream().filter(AppUser::isDeletable).map(userImportExportHelper::mapToUserToExport).toList();
        LOG.debug("exported users");

        List<Match> allMatches = matchService.findAll();
        List<MatchToExport> exportMatches = allMatches.stream().map(this::toMatchToExport).toList();
        LOG.debug("exported matches");

        List<Bet> allBetting = bettingService.findAll();
        List<BetToExport> exportBets = allBetting.stream().map(this::toBetToExport).toList();
        LOG.debug("exported bets");

        List<ExtraBet> allExtraBets = extraBettingService.findAllExtraBets();
        List<ExtraBetToExport> exportExtraBets = allExtraBets.stream().map(this::toExtraBetToExport).toList();
        LOG.debug("exported extra bets");

        ImportExportContainer importExportContainer = new ImportExportContainer(exportUsers, exportMatches, exportBets, exportExtraBets);
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
}
