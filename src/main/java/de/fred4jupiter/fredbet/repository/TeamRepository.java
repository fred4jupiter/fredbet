package de.fred4jupiter.fredbet.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import de.fred4jupiter.fredbet.domain.Team;

public interface TeamRepository extends MongoRepository<Team, String>{

	Team findByName(String name);

}
