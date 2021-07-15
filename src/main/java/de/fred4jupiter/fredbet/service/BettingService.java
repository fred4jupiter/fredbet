package de.fred4jupiter.fredbet.service;

import de.fred4jupiter.fredbet.data.RandomValueGenerator;
import de.fred4jupiter.fredbet.domain.*;
import de.fred4jupiter.fredbet.props.FredbetConstants;
import de.fred4jupiter.fredbet.props.FredbetProperties;
import de.fred4jupiter.fredbet.repository.BetRepository;
import de.fred4jupiter.fredbet.repository.ExtraBetRepository;
import de.fred4jupiter.fredbet.repository.MatchRepository;
import de.fred4jupiter.fredbet.security.SecurityService;
import de.fred4jupiter.fredbet.util.Validator;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.ImmutableTriple;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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

    public Bet createAndSaveBetting(String username, Match match, Integer goalsTeamOne, Integer goalsTeamTwo, boolean withJoker) {
        Bet bet = new Bet();
        bet.setGoalsTeamOne(goalsTeamOne);
        bet.setGoalsTeamTwo(goalsTeamTwo);
        bet.setMatch(match);
        bet.setUserName(username);
        bet.setJoker(withJoker);
        return betRepository.save(bet);
    }

    public List<Bet> findAllByUsername(String username) {
        return betRepository.findByUserName(username);
    }

    public List<Match> findMatchesToBet(String username) {
        List<Bet> userBets = betRepository.findByUserName(username);
        List<Long> matchIds = userBets.stream().map(bet -> bet.getMatch().getId()).collect(Collectors.toList());

        List<Match> matchesToBet = new ArrayList<>();
        List<Match> allMatches = matchRepository.findAllByOrderByKickOffDateAsc();
        for (Match match : allMatches) {
            if (!matchIds.contains(match.getId()) && match.isBettable()) {
                matchesToBet.add(match);
            }
        }

        return matchesToBet;
    }

    public Long save(Bet bet) {
        Match match = matchRepository.getOne(bet.getMatch().getId());
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
        return allBets.stream().filter(bet -> !bet.getUserName().equals(FredbetConstants.TECHNICAL_USERNAME)).collect(Collectors.toList());
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

    public Match findFinalMatch() {
        List<Match> matches = matchRepository.findByGroup(Group.FINAL);
        if (Validator.isEmpty(matches)) {
            return null;
        }

        if (matches.size() > 1) {
            throw new IllegalStateException("Found more than one final match!");
        }

        return matches.get(0);
    }

    public boolean hasOpenExtraBet(String currentUserName) {
        ExtraBet extraBet = extraBetRepository.findByUserName(currentUserName);
        return extraBet == null;
    }

    public List<ExtraBet> loadExtraBetDataOthers() {
        List<ExtraBet> allExtraBets = extraBetRepository.findAll(Sort.by(Direction.ASC, "userName"));
        return allExtraBets.stream().filter(extraBet -> !extraBet.getUserName().equals(FredbetConstants.TECHNICAL_USERNAME))
                .collect(Collectors.toList());
    }

    public Bet findBetById(Long betId) {
        return betRepository.getOne(betId);
    }

    public Long countByMatch(Match match) {
        return betRepository.countByMatch(match);
    }

    public void diceAllMatchesForUser(String username) {
        List<Match> allMatches = findMatchesToBet(username);
        allMatches.forEach(match -> {
            boolean jokerAllowed = false;
            if (randomValueGenerator.generateRandomBoolean()) {
                jokerAllowed = jokerService.isSettingJokerAllowed(username, match.getId());
            }
            Pair<Integer, Integer> goals = Pair.of(randomFromTo(), randomFromTo());
            createAndSaveBetting(username, match, goals.getLeft(), goals.getRight(), jokerAllowed);
        });

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

    private Integer randomFromTo() {
        return randomValueGenerator.generateRandomValueInRange(fredbetProperties.getDiceMinRange(), fredbetProperties.getDiceMaxRange());
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
}
