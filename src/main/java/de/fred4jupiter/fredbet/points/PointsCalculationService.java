package de.fred4jupiter.fredbet.points;

import de.fred4jupiter.fredbet.betting.repository.BetRepository;
import de.fred4jupiter.fredbet.domain.entity.Bet;
import de.fred4jupiter.fredbet.domain.entity.Match;
import de.fred4jupiter.fredbet.match.MatchGoalsChangedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Calculates the general betting points (but without extra bets points).
 *
 * @author michael
 */
@Service
public class PointsCalculationService {

    private static final Logger LOG = LoggerFactory.getLogger(PointsCalculationService.class);

    private final BetRepository betRepository;

    private final PointsCalculationUtil pointsCalculationUtil;

    PointsCalculationService(BetRepository betRepository, PointsCalculationUtil pointsCalculationUtil) {
        this.betRepository = betRepository;
        this.pointsCalculationUtil = pointsCalculationUtil;
    }

    @EventListener
    public void onApplicationEvent(MatchGoalsChangedEvent event) {
        final Match match = event.getMatch();

        LOG.debug("match={} has finished. Calculating points for bets...", match);
        calculatePointsFor(match);
    }

    @EventListener
    public void onPointsConfigurationChangedEvent(PointsConfigurationChangedEvent event) {
        // TODO: implement me
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
