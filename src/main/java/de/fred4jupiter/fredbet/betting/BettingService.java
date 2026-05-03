package de.fred4jupiter.fredbet.betting;

import de.fred4jupiter.fredbet.betting.repository.BetRepository;
import de.fred4jupiter.fredbet.betting.repository.ExtraBetRepository;
import de.fred4jupiter.fredbet.data.GoalResult;
import de.fred4jupiter.fredbet.data.RandomValueGenerator;
import de.fred4jupiter.fredbet.data.TeamTriple;
import de.fred4jupiter.fredbet.domain.builder.BetBuilder;
import de.fred4jupiter.fredbet.domain.entity.AppUser;
import de.fred4jupiter.fredbet.domain.entity.Bet;
import de.fred4jupiter.fredbet.domain.entity.ExtraBet;
import de.fred4jupiter.fredbet.domain.entity.Match;
import de.fred4jupiter.fredbet.match.MatchService;
import de.fred4jupiter.fredbet.props.FredbetProperties;
import de.fred4jupiter.fredbet.security.SecurityService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.function.Consumer;

@Service
@Transactional
public class BettingService {

    private static final Logger LOG = LoggerFactory.getLogger(BettingService.class);

    private final BetRepository betRepository;

    private final ExtraBetRepository extraBetRepository;

    private final SecurityService securityService;

    private final JokerService jokerService;

    private final RandomValueGenerator randomValueGenerator;

    private final MatchService matchService;

    private final FredbetProperties fredbetProperties;

    private final ExtraBettingService extraBettingService;

    public BettingService(BetRepository betRepository, ExtraBetRepository extraBetRepository,
                          SecurityService securityService, JokerService jokerService, RandomValueGenerator randomValueGenerator,
                          MatchService matchService, FredbetProperties fredbetProperties, ExtraBettingService extraBettingService) {
        this.betRepository = betRepository;
        this.extraBetRepository = extraBetRepository;
        this.securityService = securityService;
        this.jokerService = jokerService;
        this.randomValueGenerator = randomValueGenerator;
        this.matchService = matchService;
        this.fredbetProperties = fredbetProperties;
        this.extraBettingService = extraBettingService;
    }

    public Bet createAndSaveBetting(Consumer<BetBuilder> consumer) {
        BetBuilder builder = BetBuilder.create();
        consumer.accept(builder);
        return betRepository.save(builder.build());
    }

    public List<Bet> findAllByUsername(String username) {
        return betRepository.findByUserName(username);
    }

    public List<Match> findMatchesToBet(String username) {
        List<Bet> userBets = betRepository.findByUserName(username);
        List<Long> matchIds = userBets.stream().map(bet -> bet.getMatch().getId()).toList();

        List<Match> allMatches = matchService.findAllByOrderByKickOffDateAsc();
        return allMatches.stream().filter(match -> !matchIds.contains(match.getId()) && match.isBettable()).toList();
    }

    public Long save(Bet bet) {
        Match match = matchService.findByMatchId(bet.getMatch().getId());
        if (match.hasStarted()) {
            throw new NoBettingAfterMatchStartedAllowedException("The match has already been started! You are to late!");
        }

        if (bet.isJoker() && !jokerService.isSettingJokerAllowed(bet.getUserName(), bet.getMatch().getId())) {
            throw new NumberOfJokersReachedException("Maximum number of jokes to use has already been reached!");
        }

        if (StringUtils.isBlank(bet.getUserName())) {
            bet.setUserName(securityService.getCurrentUserName());
        }

        Bet saved = betRepository.save(bet);
        return saved.getId();
    }

    public Bet findOrCreateBetForMatch(Long matchId) {
        Match match = matchService.findByMatchId(matchId);
        final String currentUserName = securityService.getCurrentUserName();
        Bet bet = betRepository.findByUserNameAndMatch(currentUserName, match);
        if (bet == null) {
            bet = new Bet();
            bet.setMatch(match);
            bet.setUserName(currentUserName);
        }

        return bet;
    }

    public void deleteAllBets() {
        betRepository.deleteAll();
        extraBetRepository.deleteAll();
    }

    public List<Bet> findAllBetsForMatchId(final Long matchId) {
        if (matchId == null) {
            return null;
        }
        List<Bet> allBets = betRepository.findByMatchIdOrderByUserNameAsc(matchId);
        return allBets.stream()
            .filter(bet -> !bet.getUserName().equals(fredbetProperties.adminUsername()))
            .sorted(Comparator.comparing(Bet::getUserName, String.CASE_INSENSITIVE_ORDER))
            .toList();
    }

    public Bet findBetById(Long betId) {
        return betRepository.getReferenceById(betId);
    }

    public Long countByMatch(Match match) {
        return betRepository.countByMatch(match);
    }

    public void diceAllMatchesForUser(String username) {
        List<Match> allMatches = findMatchesToBet(username);
        allMatches.forEach(match -> {
            createAndSaveBetting(builder -> {
                GoalResult goalResult = randomValueGenerator.generateGoalResult(fredbetProperties.diceMinRange(), fredbetProperties.diceMaxRange());
                builder.withUserName(username).withMatch(match)
                    .withGoals(goalResult.goalsTeamOne(), goalResult.goalsTeamTwo())
                    .withJoker(isJokerAllowed(username, match));
            });
        });


        if (matchService.hasFirstMatchStarted()) {
            // Its too late for betting the extra bets. The first match has already started.
            return;
        }

        ExtraBet extraBet = extraBettingService.loadExtraBetForUser(username);
        if (extraBet.noExtraBetsSet()) {
            TeamTriple teamTriple = randomValueGenerator.generateTeamTriple();
            if (!extraBet.isFinalWinnerSet()) {
                extraBet.setFinalWinner(teamTriple.finalWinner());
            }
            if (!extraBet.isSemiFinalWinnerSet()) {
                extraBet.setSemiFinalWinner(teamTriple.semiFinalWinner());
            }
            if (matchService.isGameForThirdAvailable() && (!extraBet.isThirdFinalWinnerSet())) {
                extraBet.setThirdFinalWinner(teamTriple.thirdFinalWinner());
            }
            extraBetRepository.save(extraBet);
        }
    }

    private boolean isJokerAllowed(String username, Match match) {
        if (randomValueGenerator.generateRandomBoolean()) {
            return jokerService.isSettingJokerAllowed(username, match.getId());
        }
        return false;
    }

    public List<Bet> findAll() {
        return this.betRepository.findAll();
    }

    public void deleteAllBetsOfUser(AppUser appUser) {
        int deletedExtraBets = extraBetRepository.deleteAllExtraBetsOfUser(appUser.getUsername());
        LOG.debug("deleted {} extraBets of user={}", deletedExtraBets, appUser.getUsername());

        int deletedBets = betRepository.deleteAllBetsOfUser(appUser.getUsername());
        LOG.debug("deleted {} bets of user={}", deletedBets, appUser.getUsername());
    }
}
