package de.fred4jupiter.fredbet.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import de.fred4jupiter.fredbet.domain.Match;

public interface MatchRepository extends MongoRepository<Match, String>{

}
