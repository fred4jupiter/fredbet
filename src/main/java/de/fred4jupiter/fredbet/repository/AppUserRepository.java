package de.fred4jupiter.fredbet.repository;

import de.fred4jupiter.fredbet.domain.entity.AppUser;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface AppUserRepository extends JpaRepository<AppUser, Long> {

    AppUser findByUsername(String username);

    @Query("Select a from AppUser a where a.lastLogin is not null ORDER BY a.lastLogin DESC")
    List<AppUser> fetchLastLoginUsers();

    List<AppUser> findByUsernameNotLike(String username, Sort sort);

    default AppUser findByUserId(Long userId) {
        Optional<AppUser> appUser = findById(userId);
        if (appUser.isPresent()) {
            return appUser.get();
        }

        throw new IllegalArgumentException("Given user with userId=" + userId + " does not exists.");
    }
}
