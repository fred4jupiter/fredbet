package de.fred4jupiter.fredbet.service;

import de.fred4jupiter.fredbet.domain.ExtraBet;
import de.fred4jupiter.fredbet.domain.Group;
import de.fred4jupiter.fredbet.domain.Match;
import de.fred4jupiter.fredbet.domain.RuntimeSettings;
import de.fred4jupiter.fredbet.repository.ExtraBetRepository;
import de.fred4jupiter.fredbet.service.config.RuntimeSettingsService;
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

    private final RuntimeSettingsService runtimeSettingsService;

    public ExtraPointsCalculationService(ExtraBetRepository extraBetRepository, RuntimeSettingsService runtimeSettingsService) {
        this.extraBetRepository = extraBetRepository;
        this.runtimeSettingsService = runtimeSettingsService;
    }

    @Override
    public void onApplicationEvent(MatchGoalsChangedEvent event) {
        final Match match = event.getMatch();

        if (!match.isFinal() && !match.isGroup(Group.GAME_FOR_THIRD)) {
            LOG.debug("Match is not final or game of third. So no extra points to calculate for...");
            return;
        }

        final RuntimeSettings runtimeSettings = runtimeSettingsService.loadRuntimeSettings();

        LOG.debug("Calculate extra betting points...");
        List<ExtraBet> extraBets = extraBetRepository.findAll();

        extraBets.forEach(extraBet -> {
            calculatePointsFor(match, extraBet, runtimeSettings);
            LOG.debug("User {} has {} points", extraBet.getUserName(), extraBet.getPoints());
        });
    }

    private void calculatePointsFor(Match match, ExtraBet extraBet, RuntimeSettings runtimeSettings) {
        if (match.isFinal()) {
            Integer pointsFinal = calculatePointsFinal(match, extraBet, runtimeSettings);
            Integer pointsSemiFinal = calculatePointsSemiFinal(match, extraBet, runtimeSettings);
            LOG.debug("User {} reached extra bets: pointsFinal={}, pointsSemiFinal={}", extraBet.getUserName(), pointsFinal,
                    pointsSemiFinal);
            extraBet.setPointsOne(pointsFinal);
            extraBet.setPointsTwo(pointsSemiFinal);
            extraBetRepository.save(extraBet);
            return;
        }

        if (match.isGroup(Group.GAME_FOR_THIRD)) {
            Integer pointsGameOfThird = calculatePointsGameOfThird(match, extraBet, runtimeSettings);
            LOG.debug("User {} reached extra bets: pointsGameOfThird={}", extraBet.getUserName(), pointsGameOfThird);

            extraBet.setPointsThree(pointsGameOfThird);
            extraBetRepository.save(extraBet);
        }
    }

    private Integer calculatePointsFinal(Match match, ExtraBet extraBet, RuntimeSettings runtimeSettings) {
        if (!match.isFinal()) {
            return 0;
        }

        if (!match.hasResultSet()) {
            return 0;
        }

        if (extraBet.getFinalWinner() != null && extraBet.getFinalWinner().equals(match.getWinner())) {
            return runtimeSettings.getPointsFinalWinner();
        }

        return 0;
    }

    private Integer calculatePointsSemiFinal(Match match, ExtraBet extraBet, RuntimeSettings runtimeSettings) {
        if (!match.isFinal()) {
            return 0;
        }

        if (!match.hasResultSet()) {
            return 0;
        }

        if (extraBet.getSemiFinalWinner()!= null && extraBet.getSemiFinalWinner().equals(match.getLooser())) {
            return runtimeSettings.getPointsSemiFinalWinner();
        }

        return 0;
    }

    private Integer calculatePointsGameOfThird(Match match, ExtraBet extraBet, RuntimeSettings runtimeSettings) {
        if (!match.isGroup(Group.GAME_FOR_THIRD)) {
            return 0;
        }

        if (!match.hasResultSet()) {
            return 0;
        }

        if (extraBet.getThirdFinalWinner() != null && extraBet.getThirdFinalWinner().equals(match.getWinner())) {
            return runtimeSettings.getPointsThirdFinalWinner();
        }

        return 0;
    }
}
