package de.fred4jupiter.fredbet.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import de.fred4jupiter.fredbet.domain.AppUser;

public interface AppUserRepository extends MongoRepository<AppUser, String>{

	AppUser findByUsername(String username);

}
