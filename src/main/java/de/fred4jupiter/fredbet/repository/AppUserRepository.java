package de.fred4jupiter.fredbet.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import de.fred4jupiter.fredbet.domain.AppUser;

public interface AppUserRepository extends JpaRepository<AppUser, Long> {

	AppUser findByUsername(String username);

	@Query("Select a from AppUser a where a.lastLogin is not null ORDER BY a.lastLogin DESC")
	List<AppUser> fetchLastLoginUsers();

}
