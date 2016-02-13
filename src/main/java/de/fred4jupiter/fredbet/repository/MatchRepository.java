package de.fred4jupiter.fredbet.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import de.fred4jupiter.fredbet.domain.Country;
import de.fred4jupiter.fredbet.domain.Group;
import de.fred4jupiter.fredbet.domain.Match;

public interface MatchRepository extends JpaRepository<Match, Long> {

	List<Match> findAllByOrderByKickOffDateAsc();

	List<Match> findByKickOffDateGreaterThanOrderByKickOffDateAsc(Date someDate);

	List<Match> findByGroupOrderByKickOffDateAsc(Group group);

	List<Match> findByCountryOne(Country country);

}
