package de.fred4jupiter.fredbet.web.bet;

import de.fred4jupiter.fredbet.service.BettableService;
import org.springframework.stereotype.Component;

@Component
public class BettingWebUtil {

    private final BettableService bettableService;

    public BettingWebUtil(BettableService bettableService) {
        this.bettableService = bettableService;
    }

    public boolean isBettable() {
        return bettableService.isBettable();
    }
}
