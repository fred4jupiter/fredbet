package de.fred4jupiter.fredbet.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import de.fred4jupiter.fredbet.domain.Country;
import de.fred4jupiter.fredbet.domain.Team;

public interface TeamRepository extends MongoRepository<Team, String>{

	Team findByCountry(Country country);

	Team findByName(String name);

}
