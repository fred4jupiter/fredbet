package de.fred4jupiter.fredbet.web.bet;

import de.fred4jupiter.fredbet.domain.entity.ExtraBet;
import de.fred4jupiter.fredbet.match.MatchService;
import org.springframework.stereotype.Component;

@Component
public class ExtraBetCommandMapper {

    private final MatchService matchService;

    public ExtraBetCommandMapper(MatchService matchService) {
        this.matchService = matchService;
    }

    public ExtraBetCommand toExtraBetCommand(ExtraBet extraBet) {
        ExtraBetCommand extraBetCommand = new ExtraBetCommand();
        matchService.findFinalMatch().ifPresent(extraBetCommand::setFinalMatch);

        extraBetCommand.setExtraBetId(extraBet.getId());
        extraBetCommand.setFinalWinner(extraBet.getFinalWinner());
        extraBetCommand.setSemiFinalWinner(extraBet.getSemiFinalWinner());
        extraBetCommand.setThirdFinalWinner(extraBet.getThirdFinalWinner());
        extraBetCommand.setPointsOne(extraBet.getPointsOne());
        extraBetCommand.setPointsTwo(extraBet.getPointsTwo());
        extraBetCommand.setPointsThree(extraBet.getPointsThree());
        return extraBetCommand;
    }

}
