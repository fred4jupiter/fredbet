package de.fred4jupiter.fredbet.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import de.fred4jupiter.fredbet.domain.Bet;

public interface BetRepository extends MongoRepository<Bet, String>{

}
