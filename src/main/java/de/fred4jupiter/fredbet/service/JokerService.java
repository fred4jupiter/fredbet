package de.fred4jupiter.fredbet.service;

import de.fred4jupiter.fredbet.domain.Bet;
import de.fred4jupiter.fredbet.domain.Joker;
import de.fred4jupiter.fredbet.domain.RuntimeConfig;
import de.fred4jupiter.fredbet.repository.BetRepository;
import de.fred4jupiter.fredbet.service.config.RuntimeConfigurationService;
import org.springframework.stereotype.Service;

@Service
public class JokerService {

    private final BetRepository betRepository;

    private final RuntimeConfigurationService runtimeConfigurationService;

    public JokerService(BetRepository betRepository, RuntimeConfigurationService runtimeConfigurationService) {
        this.betRepository = betRepository;
        this.runtimeConfigurationService = runtimeConfigurationService;
    }

    public Joker getJokerForUser(String userName) {
        Integer numberOfJokersUsed = betRepository.countNumberOfJokerUsed(userName);
        RuntimeConfig runtimeConfig = runtimeConfigurationService.loadRuntimeConfig();
        return new Joker(numberOfJokersUsed, runtimeConfig.getJokerMaxCount());
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
