package de.fred4jupiter.fredbet.service;

import de.fred4jupiter.fredbet.data.RandomValueGenerator;
import de.fred4jupiter.fredbet.domain.*;
import de.fred4jupiter.fredbet.props.FredbetProperties;
import de.fred4jupiter.fredbet.repository.BetRepository;
import de.fred4jupiter.fredbet.repository.ExtraBetRepository;
import de.fred4jupiter.fredbet.repository.MatchRepository;
import de.fred4jupiter.fredbet.security.SecurityService;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.ImmutableTriple;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

@Service
@Transactional
public class BettingService {

    private final MatchRepository matchRepository;

    private final BetRepository betRepository;

    private final ExtraBetRepository extraBetRepository;

    private final SecurityService securityService;

    private final JokerService jokerService;

    private final RandomValueGenerator randomValueGenerator;

    private final MatchService matchService;

    private final FredbetProperties fredbetProperties;

    public BettingService(MatchRepository matchRepository, BetRepository betRepository, ExtraBetRepository extraBetRepository,
                          SecurityService securityService, JokerService jokerService, RandomValueGenerator randomValueGenerator,
                          MatchService matchService, FredbetProperties fredbetProperties) {
        this.matchRepository = matchRepository;
        this.betRepository = betRepository;
        this.extraBetRepository = extraBetRepository;
        this.securityService = securityService;
        this.jokerService = jokerService;
        this.randomValueGenerator = randomValueGenerator;
        this.matchService = matchService;
        this.fredbetProperties = fredbetProperties;
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

        List<Match> allMatches = matchRepository.findAllByOrderByKickOffDateAsc();
        return allMatches.stream().filter(match -> !matchIds.contains(match.getId()) && match.isBettable()).toList();
    }

    public Long save(Bet bet) {
        Match match = matchRepository.getReferenceById(bet.getMatch().getId());
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
        final Optional<Match> matchOpt = matchRepository.findById(matchId);
        if (matchOpt.isEmpty()) {
            return null;
        }
        final String currentUserName = securityService.getCurrentUserName();
        Match match = matchOpt.get();
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

    public void saveExtraBet(Country finalWinner, Country semiFinalWinner, Country thirdFinalWinner, String username) {
        ExtraBet found = extraBetRepository.findByUserName(username);
        if (Country.NONE.equals(finalWinner) && Country.NONE.equals(semiFinalWinner) && found != null) {
            // reset/delete existing extra bet
            extraBetRepository.delete(found);
            return;
        }

        if (found == null) {
            found = new ExtraBet();
        }

        found.setFinalWinner(finalWinner);
        found.setSemiFinalWinner(semiFinalWinner);
        if (matchService.isGameForThirdAvailable()) {
            found.setThirdFinalWinner(thirdFinalWinner);
        }
        found.setUserName(username);

        extraBetRepository.save(found);
    }

    public ExtraBet loadExtraBetForUser(String username) {
        ExtraBet extraBet = extraBetRepository.findByUserName(username);
        if (extraBet == null) {
            extraBet = new ExtraBet();
            extraBet.setUserName(username);
        }

        return extraBet;
    }

    public boolean hasFirstMatchStarted() {
        LocalDateTime dateTimeNow = LocalDateTime.now();
        LocalDateTime firstMatchKickOffDate = matchRepository.findStartDateOfFirstMatch();
        if (firstMatchKickOffDate == null) {
            return false;
        }
        return dateTimeNow.isAfter(firstMatchKickOffDate);
    }

    public Optional<Match> findFinalMatch() {
        List<Match> matches = matchRepository.findByGroup(Group.FINAL);
        return matches.stream().findFirst();
    }

    public boolean hasOpenExtraBet(String currentUserName) {
        ExtraBet extraBet = extraBetRepository.findByUserName(currentUserName);
        return extraBet == null;
    }

    public List<ExtraBet> loadExtraBetDataOthers() {
        List<ExtraBet> allExtraBets = extraBetRepository.findAll(Sort.by(Direction.ASC, "userName"));
        return allExtraBets.stream()
                .filter(extraBet -> !extraBet.getUserName().equals(fredbetProperties.adminUsername()))
                .sorted(Comparator.comparing(ExtraBet::getUserName, String.CASE_INSENSITIVE_ORDER))
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
                builder.withUserName(username).withMatch(match)
                        .withGoals(randomFromTo(), randomFromTo()).withJoker(isJokerAllowed(username, match));
            });
        });


        if (hasFirstMatchStarted()) {
            // Its too late for betting the extra bets. The first match has already started.
            return;
        }

        ExtraBet extraBet = loadExtraBetForUser(username);
        if (extraBet.noExtraBetsSet()) {
            ImmutableTriple<Country, Country, Country> teamTriple = randomValueGenerator.generateTeamTriple();
            if (!extraBet.isFinalWinnerSet()) {
                extraBet.setFinalWinner(teamTriple.getLeft());
            }
            if (!extraBet.isSemiFinalWinnerSet()) {
                extraBet.setSemiFinalWinner(teamTriple.getMiddle());
            }
            if (matchService.isGameForThirdAvailable() && (!extraBet.isThirdFinalWinnerSet())) {
                extraBet.setThirdFinalWinner(teamTriple.getRight());
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

    private Integer randomFromTo() {
        return randomValueGenerator.generateRandomValueInRange(fredbetProperties.diceMinRange(), fredbetProperties.diceMaxRange());
    }

    public void createExtraBetForUser(String username) {
        ImmutableTriple<Country, Country, Country> triple = randomValueGenerator.generateTeamTriple();
        if (triple != null) {
            Country extraBetCountryFinalWinner = triple.getLeft();
            Country extraBetCountrySemiFinalWinner = triple.getMiddle();
            Country extraBetCountryThirdFinalWinner = triple.getRight();
            saveExtraBet(extraBetCountryFinalWinner, extraBetCountrySemiFinalWinner, extraBetCountryThirdFinalWinner,
                    username);
        }
    }

    public List<Bet> findAll() {
        return this.betRepository.findAll();
    }

    public List<ExtraBet> findAllExtraBets() {
        return extraBetRepository.findAll();
    }

    public void createExtraBetForUser(String userName, Country finalWinner, Country semiFinalWinner, Country thirdFinalWinner,
                                      Integer pointsOne, Integer pointsTwo, Integer pointsThree) {
        ExtraBet extraBet = new ExtraBet();
        extraBet.setUserName(userName);
        extraBet.setFinalWinner(finalWinner);
        extraBet.setSemiFinalWinner(semiFinalWinner);
        extraBet.setThirdFinalWinner(thirdFinalWinner);
        extraBet.setPointsOne(pointsOne);
        extraBet.setPointsTwo(pointsTwo);
        extraBet.setPointsThree(pointsThree);
        extraBetRepository.save(extraBet);
    }

    public boolean hasUserBetsWithJoker() {
        String currentUserName = securityService.getCurrentUserName();
        return betRepository.hasBetsWithJoker(currentUserName);
    }
}
