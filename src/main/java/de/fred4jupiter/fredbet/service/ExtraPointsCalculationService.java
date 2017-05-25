package de.fred4jupiter.fredbet.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Service;

import de.fred4jupiter.fredbet.domain.ExtraBet;
import de.fred4jupiter.fredbet.domain.Group;
import de.fred4jupiter.fredbet.domain.Match;
import de.fred4jupiter.fredbet.props.FredbetProperties;
import de.fred4jupiter.fredbet.repository.ExtraBetRepository;

/**
 * Calculates the extra betting points.
 * 
 * @author michael
 *
 */
@Service
public class ExtraPointsCalculationService implements ApplicationListener<MatchGoalsChangedEvent> {

	private static final Logger LOG = LoggerFactory.getLogger(ExtraPointsCalculationService.class);

	private final int pointsFinalWinner;

	private final int pointsSemiFinalWinner;

	private final int pointsThirdFinalWinner;

	private final ExtraBetRepository extraBetRepository;

	@Autowired
	public ExtraPointsCalculationService(FredbetProperties fredbetProperties, ExtraBetRepository extraBetRepository) {
		this.extraBetRepository = extraBetRepository;
		this.pointsFinalWinner = fredbetProperties.getPointsFinalWinner();
		this.pointsSemiFinalWinner = fredbetProperties.getPointsSemiFinalWinner();
		this.pointsThirdFinalWinner = fredbetProperties.getPointsThirdFinalWinner();
	}

	public int getPointsFinalWinner() {
		return pointsFinalWinner;
	}

	public int getPointsSemiFinalWinner() {
		return pointsSemiFinalWinner;
	}

	public int getPointsThirdFinalWinner() {
		return pointsThirdFinalWinner;
	}

	@Override
	public void onApplicationEvent(MatchGoalsChangedEvent event) {
		final Match match = event.getMatch();
		if (match.isFinal() || match.isGroup(Group.GAME_FOR_THIRD)) {
			LOG.debug("Calculate extra betting points for final match...");
			List<ExtraBet> extraBets = extraBetRepository.findAll();

			extraBets.forEach(extraBet -> {
				boolean save = calculatePointsFor(match, extraBet);
				if (save) {
					LOG.debug("User {} has {} points", extraBet.getUserName(), extraBet.getPoints());
					extraBetRepository.save(extraBet);
				}
			});
		}
	}

	private boolean calculatePointsFor(Match match, ExtraBet extraBet) {
		if (!match.hasResultSet()) {
			return false;
		}

		if (match.isFinal()) {
			if (extraBet.getFinalWinner().equals(match.getWinner())) {
				extraBet.addPoints(pointsFinalWinner);
			}

			if (extraBet.getSemiFinalWinner().equals(match.getLooser())) {
				extraBet.addPoints(pointsSemiFinalWinner);
			}
			return true;
		}
		if (match.isGroup(Group.GAME_FOR_THIRD)) {
			if (extraBet.getThirdFinalWinner().equals(match.getWinner())) {
				extraBet.addPoints(pointsThirdFinalWinner);
				return true;
			}
		}

		return false;
	}

}
