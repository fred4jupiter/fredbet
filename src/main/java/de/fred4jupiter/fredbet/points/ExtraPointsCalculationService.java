package de.fred4jupiter.fredbet.points;

import de.fred4jupiter.fredbet.domain.ExtraBet;
import de.fred4jupiter.fredbet.domain.Group;
import de.fred4jupiter.fredbet.domain.Match;
import de.fred4jupiter.fredbet.repository.ExtraBetRepository;
import de.fred4jupiter.fredbet.service.MatchGoalsChangedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Calculates the extra betting points.
 *
 * @author michael
 */
@Service
public class ExtraPointsCalculationService implements ApplicationListener<MatchGoalsChangedEvent> {

    private static final Logger LOG = LoggerFactory.getLogger(ExtraPointsCalculationService.class);

    private final ExtraBetRepository extraBetRepository;

    private final PointsConfigService pointsConfigService;

    public ExtraPointsCalculationService(ExtraBetRepository extraBetRepository, PointsConfigService pointsConfigService) {
        this.extraBetRepository = extraBetRepository;
        this.pointsConfigService = pointsConfigService;
    }

    @Override
    public void onApplicationEvent(MatchGoalsChangedEvent event) {
        final Match match = event.getMatch();

        if (!match.isFinal() && !match.isGroup(Group.GAME_FOR_THIRD)) {
            LOG.debug("Match is not final or game of third. So no extra points to calculate for...");
            return;
        }

        LOG.debug("Calculate extra betting points...");
        List<ExtraBet> extraBets = extraBetRepository.findAll();

        extraBets.forEach(extraBet -> {
            calculatePointsFor(match, extraBet);
            LOG.debug("User {} has {} points", extraBet.getUserName(), extraBet.getPoints());
        });
    }

    private void calculatePointsFor(Match match, ExtraBet extraBet) {
        if (match.isFinal()) {
            Integer pointsFinal = calculatePointsFinal(match, extraBet);
            Integer pointsSemiFinal = calculatePointsSemiFinal(match, extraBet);
            LOG.debug("User {} reached extra bets: pointsFinal={}, pointsSemiFinal={}", extraBet.getUserName(), pointsFinal,
                pointsSemiFinal);
            extraBet.setPointsOne(pointsFinal);
            extraBet.setPointsTwo(pointsSemiFinal);
            extraBetRepository.save(extraBet);
            return;
        }

        if (match.isGroup(Group.GAME_FOR_THIRD)) {
            Integer pointsGameOfThird = calculatePointsGameOfThird(match, extraBet);
            LOG.debug("User {} reached extra bets: pointsGameOfThird={}", extraBet.getUserName(), pointsGameOfThird);

            extraBet.setPointsThree(pointsGameOfThird);
            extraBetRepository.save(extraBet);
        }
    }

    private Integer calculatePointsFinal(Match match, ExtraBet extraBet) {
        if (!match.isFinal()) {
            return 0;
        }

        if (!match.hasResultSet()) {
            return 0;
        }

        if (extraBet.getFinalWinner() != null && extraBet.getFinalWinner().equals(match.getWinner())) {
            return pointsConfigService.loadPointsConfig().getExtraPointsConfig().getPointsFinalWinner();
        }

        return 0;
    }

    private Integer calculatePointsSemiFinal(Match match, ExtraBet extraBet) {
        if (!match.isFinal()) {
            return 0;
        }

        if (!match.hasResultSet()) {
            return 0;
        }

        if (extraBet.getSemiFinalWinner() != null && extraBet.getSemiFinalWinner().equals(match.getLooser())) {
            return pointsConfigService.loadPointsConfig().getExtraPointsConfig().getPointsSemiFinalWinner();
        }

        return 0;
    }

    private Integer calculatePointsGameOfThird(Match match, ExtraBet extraBet) {
        if (!match.isGroup(Group.GAME_FOR_THIRD)) {
            return 0;
        }

        if (!match.hasResultSet()) {
            return 0;
        }

        if (extraBet.getThirdFinalWinner() != null && extraBet.getThirdFinalWinner().equals(match.getWinner())) {
            return pointsConfigService.loadPointsConfig().getExtraPointsConfig().getPointsThirdFinalWinner();
        }

        return 0;
    }
}
