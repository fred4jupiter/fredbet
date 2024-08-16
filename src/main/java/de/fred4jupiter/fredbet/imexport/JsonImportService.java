package de.fred4jupiter.fredbet.imexport;

import de.fred4jupiter.fredbet.domain.AppUser;
import de.fred4jupiter.fredbet.domain.Match;
import de.fred4jupiter.fredbet.domain.MatchBuilder;
import de.fred4jupiter.fredbet.service.BettingService;
import de.fred4jupiter.fredbet.service.MatchService;
import de.fred4jupiter.fredbet.service.image.ImageAdministrationService;
import de.fred4jupiter.fredbet.service.user.UserService;
import de.fred4jupiter.fredbet.service.user.UserToExport;
import de.fred4jupiter.fredbet.util.JsonObjectConverter;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Base64;
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

    private final ImageAdministrationService imageAdministrationService;

    public JsonImportService(JsonObjectConverter jsonObjectConverter, MatchService matchService, BettingService bettingService,
                             UserService userService, ImageAdministrationService imageAdministrationService) {
        this.jsonObjectConverter = jsonObjectConverter;
        this.matchService = matchService;
        this.bettingService = bettingService;
        this.userService = userService;
        this.imageAdministrationService = imageAdministrationService;
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
        final List<ExtraBetToExport> extraBets = importExportContainer.getExtraBets();
        extraBets.forEach(extraBetToExport -> bettingService.createExtraBetForUser(extraBetToExport.getUserName(), extraBetToExport.getFinalWinner(),
            extraBetToExport.getSemiFinalWinner(), extraBetToExport.getThirdFinalWinner(),
            extraBetToExport.getPointsOne(), extraBetToExport.getPointsTwo(), extraBetToExport.getPointsThree()));
        LOG.debug("imported extrabets");
    }

    private void importBets(ImportExportContainer importExportContainer) {
        final List<BetToExport> bets = importExportContainer.getBets();
        final Map<String, Match> savedMatchesByBusinessKey = matchService.findAllMatches().stream().collect(Collectors.toMap(Match::getBusinessKey, e -> e));
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
    }

    private void importMatches(ImportExportContainer importExportContainer) {
        final List<MatchToExport> matchesToExportList = importExportContainer.getMatches();
        matchesToExportList.forEach(matchToExport -> {
            Match match = mapToMatch(matchToExport);
            matchService.save(match);
        });
        LOG.debug("imported allMatches");
    }

    private void importUsers(ImportExportContainer importExportContainer) {
        final List<UserToExport> users = importExportContainer.getUsers();
        users.forEach(userToExport -> {
            AppUser appUser = userService.createUserIfNotExists(userToExport.getUsername(), userToExport.getPassword(), userToExport.isChild(), userToExport.getRoles());
            if (StringUtils.isNotBlank(userToExport.getUserAvatarBase64())) {
                byte[] decoded = Base64.getDecoder().decode(userToExport.getUserAvatarBase64());
                imageAdministrationService.saveUserProfileImage(decoded, appUser);
            }
        });
        LOG.debug("imported users");
    }

    private Match mapToMatch(MatchToExport matchToExport) {
        return MatchBuilder.create().withTeams(matchToExport.getTeamOne(), matchToExport.getTeamTwo())
            .withGroup(matchToExport.getGroup())
            .withKickOffDate(matchToExport.getKickOffDate())
            .withStadium(matchToExport.getStadium())
            .build();
    }
}
