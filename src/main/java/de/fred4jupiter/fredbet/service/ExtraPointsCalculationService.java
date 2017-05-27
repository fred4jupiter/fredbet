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
				if (match.isFinal() || match.isGroup(Group.GAME_FOR_THIRD)) {
					calculatePointsFor(match, extraBet);
					LOG.debug("User {} has {} points", extraBet.getUserName(), extraBet.getPoints());
					return;
				}
			});
		}
	}

	private void calculatePointsFor(Match match, ExtraBet extraBet) {
		if (match.isFinal()) {
			if (!match.hasResultSet()) {
				extraBet.setPointsOne(0);
				extraBet.setPointsTwo(0);
				extraBetRepository.save(extraBet);
				return;
			}

			if (extraBet.getFinalWinner().equals(match.getWinner())) {
				extraBet.setPointsOne(pointsFinalWinner);
			}

			if (extraBet.getSemiFinalWinner().equals(match.getLooser())) {
				extraBet.setPointsTwo(pointsSemiFinalWinner);
			}

			extraBetRepository.save(extraBet);
			return;
		} else if (match.isGroup(Group.GAME_FOR_THIRD)) {
			if (!match.hasResultSet()) {
				extraBet.setPointsThree(0);
				extraBetRepository.save(extraBet);
				return;
			}

			if (extraBet.getThirdFinalWinner().equals(match.getWinner())) {
				extraBet.setPointsThree(pointsThirdFinalWinner);
				extraBetRepository.save(extraBet);
				return;
			}
		}
	}

}
