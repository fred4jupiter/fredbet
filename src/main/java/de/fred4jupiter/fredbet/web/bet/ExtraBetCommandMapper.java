package de.fred4jupiter.fredbet.web.bet;

import de.fred4jupiter.fredbet.domain.ExtraBet;
import de.fred4jupiter.fredbet.service.BettingService;
import org.springframework.stereotype.Component;

@Component
public class ExtraBetCommandMapper {

    private final BettingService bettingService;

    public ExtraBetCommandMapper(BettingService bettingService) {
        this.bettingService = bettingService;
    }

    public ExtraBetCommand toExtraBetCommand(ExtraBet extraBet) {
        ExtraBetCommand extraBetCommand = new ExtraBetCommand();
        bettingService.findFinalMatch().ifPresent(extraBetCommand::setFinalMatch);

        extraBetCommand.setExtraBetId(extraBet.getId());
        extraBetCommand.setFinalWinner(extraBet.getFinalWinner());
        extraBetCommand.setSemiFinalWinner(extraBet.getSemiFinalWinner());
        extraBetCommand.setThirdFinalWinner(extraBet.getThirdFinalWinner());
        extraBetCommand.setPointsOne(extraBet.getPointsOne());
        extraBetCommand.setPointsTwo(extraBet.getPointsTwo());
        extraBetCommand.setPointsThree(extraBet.getPointsThree());

        boolean firstMatchStarted = bettingService.hasFirstMatchStarted();
        extraBetCommand.setBettable(!firstMatchStarted);

        return extraBetCommand;
    }

}
