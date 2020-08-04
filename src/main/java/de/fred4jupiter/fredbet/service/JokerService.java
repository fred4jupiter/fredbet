package de.fred4jupiter.fredbet.service;

import de.fred4jupiter.fredbet.domain.Bet;
import de.fred4jupiter.fredbet.domain.Joker;
import de.fred4jupiter.fredbet.domain.RuntimeSettings;
import de.fred4jupiter.fredbet.repository.BetRepository;
import de.fred4jupiter.fredbet.service.config.RuntimeSettingsService;
import org.springframework.stereotype.Service;

@Service
public class JokerService {

    private final BetRepository betRepository;

    private final RuntimeSettingsService runtimeSettingsService;

    public JokerService(BetRepository betRepository, RuntimeSettingsService runtimeSettingsService) {
        this.betRepository = betRepository;
        this.runtimeSettingsService = runtimeSettingsService;
    }

    public Joker getJokerForUser(String userName) {
        Integer numberOfJokersUsed = betRepository.countNumberOfJokerUsed(userName);
        RuntimeSettings runtimeSettings = runtimeSettingsService.loadRuntimeSettings();
        return new Joker(numberOfJokersUsed, runtimeSettings.getJokerMaxCount());
    }

    public boolean isSettingJokerAllowed(String userName, Long matchId) {
        Joker joker = getJokerForUser(userName);
        if (joker.getNumberOfJokersUsed() < joker.getMax()) {
            return true;
        }

        Bet bet = betRepository.findBetsOfGivenMatchWithJokerSet(userName, matchId);
        if (bet != null) {
            // This bet is one of the bets with the previous set joker. So you
            // can edit this
            return true;
        }

        return false;
    }

}
