package de.fred4jupiter.fredbet.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import de.fred4jupiter.fredbet.domain.SessionTracking;

public interface SessionTrackingRepository extends JpaRepository<SessionTracking, String>{

}
