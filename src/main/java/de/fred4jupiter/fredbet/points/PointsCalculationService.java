package de.fred4jupiter.fredbet.points;

import de.fred4jupiter.fredbet.betting.repository.BetRepository;
import de.fred4jupiter.fredbet.domain.entity.Bet;
import de.fred4jupiter.fredbet.domain.entity.Match;
import de.fred4jupiter.fredbet.match.MatchGoalsChangedEvent;
import de.fred4jupiter.fredbet.match.MatchRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Calculates the general betting points (but without extra bets points).
 *
 * @author michael
 */
@Service
@Transactional
public class PointsCalculationService {

    private static final Logger LOG = LoggerFactory.getLogger(PointsCalculationService.class);

    private final BetRepository betRepository;

    private final PointsCalculationUtil pointsCalculationUtil;

    private final MatchRepository matchRepository;

    PointsCalculationService(BetRepository betRepository, PointsCalculationUtil pointsCalculationUtil, MatchRepository matchRepository) {
        this.betRepository = betRepository;
        this.pointsCalculationUtil = pointsCalculationUtil;
        this.matchRepository = matchRepository;
    }

    @EventListener
    public void onApplicationEvent(MatchGoalsChangedEvent event) {
        final Match match = event.getMatch();

        LOG.debug("match={} has finished. Calculating points for bets...", match);
        calculatePointsFor(match);
    }

    @EventListener
    public void onPointsConfigurationChangedEvent(PointsConfigurationChangedEvent event) {
        LOG.info("recalculating points...");
        List<Match> matchesWithResult = matchRepository.findAllWithMatchResult();
        matchesWithResult.forEach(this::calculatePointsFor);
    }

    void calculatePointsFor(final Match match) {
        List<Bet> allBetsForThisMatch = betRepository.findByMatch(match);

        allBetsForThisMatch.forEach(bet -> {
            if (match.hasResultSet()) {
                bet.setPoints(pointsCalculationUtil.calculatePointsFor(match, bet));
            } else {
                bet.setPoints(null);
            }

            LOG.debug("User {} gets {} points", bet.getUserName(), bet.getPoints());
        });

        betRepository.saveAll(allBetsForThisMatch);
    }
}
