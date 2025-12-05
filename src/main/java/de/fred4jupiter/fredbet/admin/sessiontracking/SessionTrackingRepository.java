package de.fred4jupiter.fredbet.admin.sessiontracking;

import de.fred4jupiter.fredbet.domain.entity.SessionTracking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SessionTrackingRepository extends JpaRepository<SessionTracking, String> {

    List<SessionTracking> findAllByOrderByLastLoginDesc();

    @Modifying
    @Query("update SessionTracking t set t.userName = :newUsername where t.userName = :oldUsername")
    void renameUser(@Param("oldUsername") String oldUsername, @Param("newUsername") String newUsername);

    @Modifying
    @Query("delete SessionTracking t where t.sessionId = :sessionId")
    int deleteBySessionId(@Param("sessionId") String sessionId);
}
