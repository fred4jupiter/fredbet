package de.fred4jupiter.fredbet.web.bet;

import de.fred4jupiter.fredbet.match.MatchService;
import org.springframework.stereotype.Component;

@Component
public class BettingWebUtil {

    private final MatchService matchService;

    public BettingWebUtil(MatchService matchService) {
        this.matchService = matchService;
    }

    public boolean isBettable() {
        return matchService.isBettable();
    }
}
