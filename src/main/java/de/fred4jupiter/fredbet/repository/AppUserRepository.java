package de.fred4jupiter.fredbet.repository;

import de.fred4jupiter.fredbet.domain.AppUser;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface AppUserRepository extends JpaRepository<AppUser, Long> {

    AppUser findByUsername(String username);

    @Query("Select a from AppUser a where a.lastLogin is not null ORDER BY a.lastLogin DESC")
    List<AppUser> fetchLastLoginUsers();

    List<AppUser> findByUsernameNotLike(String username, Sort sort);
}
