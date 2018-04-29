package de.fred4jupiter.fredbet.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import de.fred4jupiter.fredbet.domain.SessionTracking;

public interface SessionTrackingRepository extends JpaRepository<SessionTracking, String>{

	List<SessionTracking> findAllByOrderByUserNameAsc();

	List<SessionTracking> findByLastLoginLessThan(LocalDateTime lastLogin);

	SessionTracking findBySessionId(String sessionId);

	@Modifying
	@Query("update SessionTracking t set t.userName = :newUsername where t.userName = :oldUsername")
	void renameUser(@Param("oldUsername") String oldUsername, @Param("newUsername") String newUsername);
}
