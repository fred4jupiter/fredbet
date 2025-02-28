package de.fred4jupiter.fredbet.betting.repository;

import de.fred4jupiter.fredbet.repository.UsernamePoints;

import java.util.List;

interface BetRepositoryCustom {

    List<UsernamePoints> calculateRanging();
}
