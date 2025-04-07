package de.fred4jupiter.fredbet.imexport;

import de.fred4jupiter.fredbet.betting.BettingService;
import de.fred4jupiter.fredbet.betting.ExtraBettingService;
import de.fred4jupiter.fredbet.domain.builder.MatchBuilder;
import de.fred4jupiter.fredbet.domain.entity.Match;
import de.fred4jupiter.fredbet.match.MatchService;
import de.fred4jupiter.fredbet.user.UserService;
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

    private final UserImportExportHelper userImportExportHelper;

    private final ExtraBettingService extraBettingService;

    JsonImportService(JsonObjectConverter jsonObjectConverter, MatchService matchService, BettingService bettingService,
                      UserService userService, UserImportExportHelper userImportExportHelper, ExtraBettingService extraBettingService) {
        this.jsonObjectConverter = jsonObjectConverter;
        this.matchService = matchService;
        this.bettingService = bettingService;
        this.userService = userService;
        this.userImportExportHelper = userImportExportHelper;
        this.extraBettingService = extraBettingService;
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

        importUsers(importExportContainer);
        importMatches(importExportContainer);
        importBets(importExportContainer);
        importExtraBets(importExportContainer);
    }

    private void importExtraBets(ImportExportContainer importExportContainer) {
        final List<ExtraBetToExport> extraBets = importExportContainer.extraBets();
        extraBets.forEach(extraBetToExport -> extraBettingService.createExtraBetForUser(extraBetToExport.userName(), extraBetToExport.finalWinner(),
            extraBetToExport.semiFinalWinner(), extraBetToExport.thirdFinalWinner(),
            extraBetToExport.pointsOne(), extraBetToExport.pointsTwo(), extraBetToExport.pointsThree()));
        LOG.debug("imported extrabets");
    }

    private void importBets(ImportExportContainer importExportContainer) {
        final List<BetToExport> bets = importExportContainer.bets();
        final Map<String, Match> savedMatchesByBusinessKey = matchService.findAllMatches().stream().collect(Collectors.toMap(Match::getBusinessKey, e -> e));
        bets.forEach(betToExport -> {
            Match match = savedMatchesByBusinessKey.get(betToExport.matchBusinessKey());
            if (match == null) {
                LOG.warn("Could not find match with business key={}", betToExport.matchBusinessKey());
            }
            bettingService.createAndSaveBetting(builder -> {
                builder.withMatch(match)
                    .withGoals(betToExport.goalsTeamOne(), betToExport.goalsTeamTwo())
                    .withUserName(betToExport.username())
                    .withJoker(betToExport.joker())
                    .withPenaltyWinnerOne(betToExport.penaltyWinnerOne())
                    .withPoints(betToExport.points());
            });
        });
        LOG.debug("imported bets");
    }

    private void importMatches(ImportExportContainer importExportContainer) {
        final List<MatchToExport> matchesToExportList = importExportContainer.matches();
        matchesToExportList.forEach(matchToExport -> {
            Match match = mapToMatch(matchToExport);
            matchService.save(match);
        });
        LOG.debug("imported allMatches");
    }

    private void importUsers(ImportExportContainer importExportContainer) {
        final List<UserToExport> users = importExportContainer.users();
        long count = userImportExportHelper.importUsers(users);
        LOG.debug("imported {} users", count);
    }

    private Match mapToMatch(MatchToExport matchToExport) {
        return MatchBuilder.create().withTeams(matchToExport.getTeamOne(), matchToExport.getTeamTwo())
            .withGroup(matchToExport.getGroup())
            .withKickOffDate(matchToExport.getKickOffDate())
            .withStadium(matchToExport.getStadium())
            .build();
    }
}
