package de.fred4jupiter.fredbet.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import de.fred4jupiter.fredbet.domain.RememberMeToken;

public interface RememberMeTokenRepository extends JpaRepository<RememberMeToken, Long> {

	RememberMeToken findBySeriesAndLastUsed(String series, Date lastUsed);

	List<RememberMeToken> findByUsername(String username);

	List<RememberMeToken> findBySeries(String series);

}
