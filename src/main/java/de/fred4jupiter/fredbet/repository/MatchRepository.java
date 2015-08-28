package de.fred4jupiter.fredbet.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import de.fred4jupiter.fredbet.domain.Group;
import de.fred4jupiter.fredbet.domain.Match;

public interface MatchRepository extends MongoRepository<Match, String> {

	List<Match> findAllByOrderByKickOffDateAsc();

	List<Match> findByGroupOrderByKickOffDateAsc(Group group);

}
