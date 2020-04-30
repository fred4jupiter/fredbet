package de.fred4jupiter.fredbet.web.bet;

import de.fred4jupiter.fredbet.domain.ExtraBet;
import de.fred4jupiter.fredbet.domain.Match;
import de.fred4jupiter.fredbet.service.BettingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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
        extraBetCommand.setPointsOne(extraBet.getPointsOne());
        extraBetCommand.setPointsTwo(extraBet.getPointsTwo());
        extraBetCommand.setPointsThree(extraBet.getPointsThree());

        boolean firstMatchStarted = bettingService.hasFirstMatchStarted();
        if (firstMatchStarted) {
            extraBetCommand.setBettable(false);
        } else {
            extraBetCommand.setBettable(true);
        }

        return extraBetCommand;
    }

}
