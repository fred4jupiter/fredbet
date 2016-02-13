package de.fred4jupiter.fredbet.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import de.fred4jupiter.fredbet.domain.AppUser;

public interface AppUserRepository extends JpaRepository<AppUser, Long>{

	AppUser findByUsername(String username);

}
