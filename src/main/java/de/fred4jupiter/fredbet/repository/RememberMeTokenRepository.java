package de.fred4jupiter.fredbet.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import de.fred4jupiter.fredbet.domain.RememberMeToken;

public interface RememberMeTokenRepository extends MongoRepository<RememberMeToken, String> {

	RememberMeToken findBySeries(String series);

	List<RememberMeToken> findByUsername(String username);

}
