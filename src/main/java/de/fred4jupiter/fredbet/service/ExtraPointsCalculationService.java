package de.fred4jupiter.fredbet.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Service;

import de.fred4jupiter.fredbet.domain.ExtraBet;
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

	private final ExtraBetRepository extraBetRepository;

	@Autowired
	public ExtraPointsCalculationService(FredbetProperties fredbetProperties, ExtraBetRepository extraBetRepository) {
		this.extraBetRepository = extraBetRepository;
		this.pointsFinalWinner = fredbetProperties.getPointsFinalWinner();
		this.pointsSemiFinalWinner = fredbetProperties.getPointsSemiFinalWinner();
	}
	
	public int getPointsFinalWinner() {
		return pointsFinalWinner;
	}
	
	public int getPointsSemiFinalWinner() {
		return pointsSemiFinalWinner;
	}

	@Override
	public void onApplicationEvent(MatchGoalsChangedEvent event) {
		if (!event.getMatch().isFinal()) {
			return;
		}

		LOG.debug("Calculate extra betting points for final match...");
		List<ExtraBet> extraBets = extraBetRepository.findAll();

		extraBets.forEach(extraBet -> {
			Integer points = calculatePointsFor(event.getMatch(), extraBet);
			extraBet.setPoints(points);
			LOG.debug("User {} gets {} points", extraBet.getUserName(), points);
			extraBetRepository.save(extraBet);
		});
	}

	private Integer calculatePointsFor(Match finalMatch, ExtraBet extraBet) {
		if (!finalMatch.hasResultSet()) {
			return null;
		}

		int points = 0;

		if (extraBet.getFinalWinner().equals(finalMatch.getWinner())) {
			points = points + pointsFinalWinner;
		}

		if (extraBet.getSemiFinalWinner().equals(finalMatch.getLooser())) {
			points = points + pointsSemiFinalWinner;
		}

		return points;
	}

}
