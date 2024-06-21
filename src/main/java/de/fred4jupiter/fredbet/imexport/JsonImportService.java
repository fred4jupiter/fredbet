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
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional
public class JsonImportService {

    private static final Logger LOG = LoggerFactory.getLogger(JsonImportService.class);

    private final JsonObjectConverter jsonObjectConverter;

    private final MatchService matchService;

    private final BettingService bettingService;

    private final UserService userService;

    public JsonImportService(JsonObjectConverter jsonObjectConverter, MatchService matchService, BettingService bettingService, UserService userService) {
        this.jsonObjectConverter = jsonObjectConverter;
        this.matchService = matchService;
        this.bettingService = bettingService;
        this.userService = userService;
    }

    public void importAllFromJson(String json) {
        LOG.debug("importing all from json...");
        userService.deleteAllUsers();
        LOG.debug("deleted all users");
        matchService.deleteAllMatches();
        LOG.debug("deleted all allMatches");
        bettingService.deleteAllBets();
        LOG.debug("deleted all bets");

        final ImportExportContainer importExportContainer = jsonObjectConverter.fromJson(json, ImportExportContainer.class);

        final List<UserToExport> users = importExportContainer.getUsers();
        users.forEach(userToExport -> userService.createUserIfNotExists(userToExport.getUsername(), userToExport.getPassword(), userToExport.isChild(), userToExport.getRoles()));
        LOG.debug("imported users");

        final List<MatchToExport> matchesToExportList = importExportContainer.getMatches();
        matchesToExportList.forEach(matchToExport -> {
            Match match = mapToMatch(matchToExport);
            matchService.save(match);
        });
        LOG.debug("imported allMatches");

        final List<BetToExport> bets = importExportContainer.getBets();
        final Map<String, Match> savedMatchesByBusinessKey = matchService.findAllMatches().stream().collect(Collectors.toMap(Match::getBusinessHashcode, e -> e));
        bets.forEach(betToExport -> {
            Match match = savedMatchesByBusinessKey.get(betToExport.getMatchBusinessKey());
            if (match == null) {
                LOG.warn("Could not find match with business key={}", betToExport.getMatchBusinessKey());
            }
            bettingService.createAndSaveBetting(builder -> {
                builder.withMatch(match)
                    .withGoals(betToExport.getGoalsTeamOne(), betToExport.getGoalsTeamTwo())
                    .withUserName(betToExport.getUsername())
                    .withJoker(betToExport.isJoker())
                    .withPenaltyWinnerOne(betToExport.isPenaltyWinnerOne())
                    .withPoints(betToExport.getPoints());
            });
        });
        LOG.debug("imported bets");

        final List<ExtraBetToExport> extraBets = importExportContainer.getExtraBets();
        extraBets.forEach(extraBetToExport -> bettingService.createExtraBetForUser(extraBetToExport.getUserName(), extraBetToExport.getFinalWinner(),
            extraBetToExport.getSemiFinalWinner(), extraBetToExport.getThirdFinalWinner(),
            extraBetToExport.getPointsOne(), extraBetToExport.getPointsTwo(), extraBetToExport.getPointsThree()));
        LOG.debug("imported extrabets");
    }

    private Match mapToMatch(MatchToExport matchToExport) {
        return MatchBuilder.create().withTeams(matchToExport.getTeamOne(), matchToExport.getTeamTwo())
            .withGroup(matchToExport.getGroup())
            .withKickOffDate(matchToExport.getKickOffDate())
            .withStadium(matchToExport.getStadium())
            .build();
    }
}
