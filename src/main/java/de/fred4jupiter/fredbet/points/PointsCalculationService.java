package de.fred4jupiter.fredbet.points;

import de.fred4jupiter.fredbet.domain.Bet;
import de.fred4jupiter.fredbet.domain.Match;
import de.fred4jupiter.fredbet.repository.BetRepository;
import de.fred4jupiter.fredbet.service.MatchGoalsChangedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Calculates the general betting points (but without extra bets points).
 *
 * @author michael
 */
@Service
public class PointsCalculationService implements ApplicationListener<MatchGoalsChangedEvent> {

    private static final Logger LOG = LoggerFactory.getLogger(PointsCalculationService.class);

    private final BetRepository betRepository;

    private final PointsCalculationUtil pointsCalculationUtil;

    PointsCalculationService(BetRepository betRepository, PointsCalculationUtil pointsCalculationUtil) {
        this.betRepository = betRepository;
        this.pointsCalculationUtil = pointsCalculationUtil;
    }

    @Override
    public void onApplicationEvent(MatchGoalsChangedEvent event) {
        final Match match = event.getMatch();

        LOG.debug("match={} has finished. Calculating points for bets...", match);
        calculatePointsFor(match);
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
