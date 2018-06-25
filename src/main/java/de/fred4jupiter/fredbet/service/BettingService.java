package de.fred4jupiter.fredbet.service;

import de.fred4jupiter.fredbet.domain.*;
import de.fred4jupiter.fredbet.props.FredbetConstants;
import de.fred4jupiter.fredbet.repository.BetRepository;
import de.fred4jupiter.fredbet.repository.ExtraBetRepository;
import de.fred4jupiter.fredbet.repository.MatchRepository;
import de.fred4jupiter.fredbet.security.SecurityService;
import de.fred4jupiter.fredbet.util.Validator;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    private MatchRepository matchRepository;

    @Autowired
    private BetRepository betRepository;

    @Autowired
    private ExtraBetRepository extraBetRepository;

    @Autowired
    private SecurityService securityService;

    @Autowired
    private JokerService jokerService;

    public Bet createAndSaveBetting(AppUser appUser, Match match, Integer goalsTeamOne, Integer goalsTeamTwo, boolean withJoker) {
        Bet bet = new Bet();
        bet.setGoalsTeamOne(goalsTeamOne);
        bet.setGoalsTeamTwo(goalsTeamTwo);
        bet.setMatch(match);
        bet.setUserName(appUser.getUsername());
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
        if (!matchOpt.isPresent()) {
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
        if (Country.NONE.equals(finalWinner) && Country.NONE.equals(semiFinalWinner) && Country.NONE.equals(thirdFinalWinner)
                && found != null) {
            // reset/delete existing extra bet
            extraBetRepository.delete(found);
            return;
        }

        if (found == null) {
            found = new ExtraBet();
        }

        found.setFinalWinner(finalWinner);
        found.setSemiFinalWinner(semiFinalWinner);
        found.setThirdFinalWinner(thirdFinalWinner);
        found.setUserName(username);

        extraBetRepository.save(found);
    }

    public ExtraBet loadExtraBetForUser(String username) {
        ExtraBet extraBet = extraBetRepository.findByUserName(username);
        if (extraBet == null) {
            extraBet = new ExtraBet();
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
        List<ExtraBet> allExtraBets = extraBetRepository.findAll(new Sort(Direction.ASC, "userName"));
        return allExtraBets.stream().filter(extraBet -> !extraBet.getUserName().equals(FredbetConstants.TECHNICAL_USERNAME))
                .collect(Collectors.toList());
    }

    public Bet findBetById(Long betId) {
        return betRepository.getOne(betId);
    }

    public Long countByMatch(Match match) {
        return betRepository.countByMatch(match);
    }
}
