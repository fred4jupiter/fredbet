package de.fred4jupiter.fredbet.betting.repository;

import de.fred4jupiter.fredbet.ranking.UsernamePoints;

import java.util.List;

interface BetRepositoryCustom {

    List<UsernamePoints> calculateRanging();
}
