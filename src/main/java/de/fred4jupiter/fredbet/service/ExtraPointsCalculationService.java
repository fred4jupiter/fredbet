package de.fred4jupiter.fredbet.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Service;

import de.fred4jupiter.fredbet.domain.ExtraBet;
import de.fred4jupiter.fredbet.domain.Match;
import de.fred4jupiter.fredbet.repository.ExtraBetRepository;

@Service
public class ExtraPointsCalculationService implements ApplicationListener<MatchFinishedEvent> {

	private static final int POINTS_SEMI_FINAL_WINNER = 10;

	private static final int POINTS_FINAL_WINNER = 5;

	private static final Logger LOG = LoggerFactory.getLogger(ExtraPointsCalculationService.class);

	@Autowired
	private ExtraBetRepository extraBetRepository;

	@Override
	public void onApplicationEvent(MatchFinishedEvent event) {
		if (!event.getMatch().isFinal()) {
			return;
		}

		List<ExtraBet> extraBets = extraBetRepository.findAll();

		extraBets.forEach(extraBet -> {
			Integer points = calculatePointsFor(event.getMatch(), extraBet);
			extraBet.setPoints(points);
			LOG.debug("User {} gets {} points", extraBet.getUserName(), points);
			extraBetRepository.save(extraBet);
		});
	}

	private Integer calculatePointsFor(Match finalMatch, ExtraBet extraBet) {
		int points = 0;

		if (extraBet.getFinalWinner().equals(finalMatch.getWinner())) {
			points = points + POINTS_FINAL_WINNER;
		}

		if (extraBet.getSemiFinalWinner().equals(finalMatch.getLooser())) {
			points = points + POINTS_SEMI_FINAL_WINNER;
		}

		return points;
	}

}
