package de.fred4jupiter.fredbet.web.bet;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import de.fred4jupiter.fredbet.domain.ExtraBet;
import de.fred4jupiter.fredbet.domain.Match;
import de.fred4jupiter.fredbet.service.BettingService;

@Component
public class ExtraBetCommandMapper {

	@Autowired
	private BettingService bettingService;

	public ExtraBetCommand toExtraBetCommand(ExtraBet extraBet) {
		ExtraBetCommand extraBetCommand = new ExtraBetCommand();
		Match finalMatch = bettingService.findFinalMatch();
		if (finalMatch != null) {
			extraBetCommand.setFinalMatch(finalMatch);
		}

		extraBetCommand.setExtraBetId(extraBet.getId());
		extraBetCommand.setFinalWinner(extraBet.getFinalWinner());
		extraBetCommand.setSemiFinalWinner(extraBet.getSemiFinalWinner());
		extraBetCommand.setThirdFinalWinner(extraBet.getThirdFinalWinner());
		extraBetCommand.setPoints(extraBet.getPoints());

		boolean firstMatchStarted = bettingService.hasFirstMatchStarted();
		if (firstMatchStarted) {
			extraBetCommand.setBettable(false);
		} else {
			extraBetCommand.setBettable(true);
		}

		return extraBetCommand;
	}

}
