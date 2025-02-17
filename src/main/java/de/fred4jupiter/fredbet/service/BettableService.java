package de.fred4jupiter.fredbet.service;

import org.springframework.stereotype.Service;

@Service
public class BettableService {

    private final BettingService bettingService;

    private final MatchService matchService;

    public BettableService(BettingService bettingService, MatchService matchService) {
        this.bettingService = bettingService;
        this.matchService = matchService;
    }

    public boolean isBettable() {
        return !isNotBettable();
    }

    public boolean isNotBettable() {
        // if first match has started (based on kick off date) or there is no match with a result
        return bettingService.hasFirstMatchStarted() || matchService.hasMatchWithResult();
    }
}
