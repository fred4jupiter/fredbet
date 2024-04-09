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
        importExportContainer.setUsers(allUsers.stream().filter(AppUser::isDeletable).map(this::toUserToExport).toList());

        List<Match> allMatches = matchService.findAll();
        importExportContainer.setMatches(allMatches.stream().map(this::toMatchToExport).toList());

        List<Bet> allBetting = bettingService.findAll();
        importExportContainer.setBets(allBetting.stream().map(this::toBetToExport).toList());

        List<ExtraBet> allExtraBets = bettingService.findAllExtraBets();
        importExportContainer.setExtraBets(allExtraBets.stream().map(this::toExtraBetToExport).toList());

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
            export.setMatchBusinessKey(bet.getMatch().getBusinessHashcode());
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
