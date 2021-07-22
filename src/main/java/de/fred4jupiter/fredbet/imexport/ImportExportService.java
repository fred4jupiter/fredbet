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
public class ImportExportService {

    private static final Logger LOG = LoggerFactory.getLogger(ImportExportService.class);

    private final JsonObjectConverter jsonObjectConverter;

    private final MatchService matchService;

    private final BettingService bettingService;

    private final UserService userService;

    public ImportExportService(JsonObjectConverter jsonObjectConverter, MatchService matchService, BettingService bettingService, UserService userService) {
        this.jsonObjectConverter = jsonObjectConverter;
        this.matchService = matchService;
        this.bettingService = bettingService;
        this.userService = userService;
    }

    public String exportAllToJson() {
        final ImportExportContainer importExportContainer = new ImportExportContainer();
        List<AppUser> allUsers = userService.findAll();
        importExportContainer.setUsers(convertToUsers(allUsers));

        List<Match> matches = matchService.findAll();
        importExportContainer.setMatches(convertToMatches(matches));

        List<Bet> bettings = bettingService.findAll();
        importExportContainer.setBets(convertToBettings(bettings));

        List<ExtraBet> allExtraBets = bettingService.findAllExtraBets();
        importExportContainer.setExtraBets(convertToExtraBets(allExtraBets));

        return jsonObjectConverter.toJson(importExportContainer);
    }

    private List<ExtraBetToExport> convertToExtraBets(List<ExtraBet> allExtraBets) {
        return allExtraBets.stream().map(extraBet -> {
            ExtraBetToExport export = new ExtraBetToExport();
            export.setUserName(extraBet.getUserName());
            export.setFinalWinner(extraBet.getFinalWinner());
            export.setSemiFinalWinner(extraBet.getSemiFinalWinner());
            export.setThirdFinalWinner(extraBet.getThirdFinalWinner());
            export.setPointsOne(extraBet.getPointsOne());
            export.setPointsTwo(extraBet.getPointsTwo());
            export.setPointsThree(extraBet.getPointsThree());
            return export;
        }).collect(Collectors.toList());
    }

    private List<BetToExport> convertToBettings(List<Bet> bettings) {
        return bettings.stream().map(bet -> {
            BetToExport export = new BetToExport();
            export.setJoker(bet.isJoker());
            export.setGoalsTeamOne(bet.getGoalsTeamOne());
            export.setGoalsTeamTwo(bet.getGoalsTeamTwo());
            if (bet.getMatch() != null) {
                export.setMatchBusinessKey(bet.getMatch().getMatchBusinessKey());
            }
            export.setUsername(bet.getUserName());
            export.setPoints(bet.getPoints());
            export.setPenaltyWinnerOne(bet.isPenaltyWinnerOne());
            return export;
        }).collect(Collectors.toList());
    }

    private List<MatchToExport> convertToMatches(List<Match> matches) {
        return matches.stream().map(match -> {
            MatchToExport export = new MatchToExport();
            export.setTeamOne(match.getTeamOne());
            export.setTeamTwo(match.getTeamTwo());
            export.setGroup(match.getGroup());
            export.setStadium(match.getStadium());
            export.setKickOffDate(match.getKickOffDate());
            export.setPenaltyWinnerOne(match.isPenaltyWinnerOne());
            return export;
        }).collect(Collectors.toList());
    }

    private List<UserToExport> convertToUsers(List<AppUser> allUsers) {
        return allUsers.stream().filter(AppUser::isDeletable).map(appUser -> {
            UserToExport userToExport = new UserToExport();
            userToExport.setUsername(appUser.getUsername());
            userToExport.setPassword(appUser.getPassword());
            userToExport.setChild(appUser.isChild());
            userToExport.setRoles(appUser.getRoles());
            return userToExport;
        }).collect(Collectors.toList());
    }

    public void importAllFromJson(String json) {
        LOG.debug("importing all from json...");
        userService.deleteAllUsers();
        LOG.debug("deleted all users");
        matchService.deleteAllMatches();
        LOG.debug("deleted all matches");

        final ImportExportContainer importExportContainer = jsonObjectConverter.fromJson(json, ImportExportContainer.class);

        final List<UserToExport> users = importExportContainer.getUsers();
        users.forEach(userToExport -> {
            userService.createUserIfNotExists(userToExport.getUsername(), userToExport.getPassword(), userToExport.isChild(), userToExport.getRoles());
        });
        LOG.debug("imported users");

        final List<MatchToExport> matchesToExportList = importExportContainer.getMatches();
        matchesToExportList.forEach(matchToExport -> {
            Match match = mapToMatch(matchToExport);
            matchService.save(match);
        });
        LOG.debug("imported matches");

        final List<BetToExport> bets = importExportContainer.getBets();
        final Map<Integer, Match> savedMatchesByBusinessKey = loadAllMatches();
        bets.forEach(betToExport -> {
            bettingService.createAndSaveBetting(betToExport.getUsername(), savedMatchesByBusinessKey.get(betToExport.getMatchBusinessKey()),
                    betToExport.getGoalsTeamOne(), betToExport.getGoalsTeamTwo(), betToExport.isJoker(), betToExport.isPenaltyWinnerOne());
        });
        LOG.debug("imported bets");

        final List<ExtraBetToExport> extraBets = importExportContainer.getExtraBets();
        extraBets.forEach(extraBetToExport -> {
            bettingService.createExtraBetForUser(extraBetToExport.getUserName(), extraBetToExport.getFinalWinner(),
                    extraBetToExport.getSemiFinalWinner(), extraBetToExport.getThirdFinalWinner(),
                    extraBetToExport.getPointsOne(), extraBetToExport.getPointsTwo(), extraBetToExport.getPointsThree());
        });
        LOG.debug("imported extrabets");
    }

    private Map<Integer, Match> loadAllMatches() {
        return matchService.findAllMatches().stream().collect(Collectors.toMap(Match::getMatchBusinessKey, e -> e));
    }

    private Match mapToMatch(MatchToExport matchToExport) {
        return MatchBuilder.create().withTeams(matchToExport.getTeamOne(), matchToExport.getTeamTwo())
                .withGroup(matchToExport.getGroup())
                .withKickOffDate(matchToExport.getKickOffDate())
                .withStadium(matchToExport.getStadium())
                .build();
    }
}
