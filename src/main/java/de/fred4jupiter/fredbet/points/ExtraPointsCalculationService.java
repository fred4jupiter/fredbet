package de.fred4jupiter.fredbet.points;

import de.fred4jupiter.fredbet.betting.repository.ExtraBetRepository;
import de.fred4jupiter.fredbet.domain.Group;
import de.fred4jupiter.fredbet.domain.entity.ExtraBet;
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
 * Calculates the extra betting points.
 *
 * @author michael
 */
@Service
@Transactional
public class ExtraPointsCalculationService {

    private static final Logger LOG = LoggerFactory.getLogger(ExtraPointsCalculationService.class);

    private final ExtraBetRepository extraBetRepository;

    private final PointsConfigService pointsConfigService;

    private final MatchRepository matchRepository;

    public ExtraPointsCalculationService(ExtraBetRepository extraBetRepository, PointsConfigService pointsConfigService,
                                         MatchRepository matchRepository) {
        this.extraBetRepository = extraBetRepository;
        this.pointsConfigService = pointsConfigService;
        this.matchRepository = matchRepository;
    }

    @EventListener
    public void onApplicationEvent(MatchGoalsChangedEvent event) {
        final Match match = event.getMatch();

        calculateExtraPointsOnMatch(match);
    }

    @EventListener
    public void onApplicationEvent(PointsConfigurationChangedEvent event) {
        LOG.info("recalculating extra points...");
        List<Match> lastMatchesRelevantForExtraPoints = matchRepository.findByGroupIn(List.of(Group.FINAL, Group.GAME_FOR_THIRD));
        lastMatchesRelevantForExtraPoints.forEach(this::calculateExtraPointsOnMatch);
    }

    private void calculateExtraPointsOnMatch(Match match) {
        if (!match.isFinal() && !match.isGroup(Group.GAME_FOR_THIRD)) {
            LOG.debug("Match is not final or game of third. So no extra points to calculate for...");
            return;
        }

        LOG.debug("calculating extra points for match: {}", match);
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
