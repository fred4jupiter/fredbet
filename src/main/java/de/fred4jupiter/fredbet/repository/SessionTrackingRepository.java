package de.fred4jupiter.fredbet.repository;

import de.fred4jupiter.fredbet.domain.SessionTracking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SessionTrackingRepository extends JpaRepository<SessionTracking, String> {

    List<SessionTracking> findAllByOrderByLastLoginDesc();

    SessionTracking findBySessionId(String sessionId);

    @Modifying
    @Query("update SessionTracking t set t.userName = :newUsername where t.userName = :oldUsername")
    void renameUser(@Param("oldUsername") String oldUsername, @Param("newUsername") String newUsername);
}
