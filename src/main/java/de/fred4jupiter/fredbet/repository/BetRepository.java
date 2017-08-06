package de.fred4jupiter.fredbet.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import de.fred4jupiter.fredbet.domain.Bet;
import de.fred4jupiter.fredbet.domain.Match;

public interface BetRepository extends JpaRepository<Bet, Long>, BetRepositoryCustom {

	Bet findByUserNameAndMatch(String currentUsername, Match match);

	List<Bet> findByUserName(String currentUsername);

	List<Bet> findByMatch(Match match);

	List<Bet> findByMatchIdOrderByUserNameAsc(Long id);

	Long countByMatch(Match match);

	@Query("select new de.fred4jupiter.fredbet.repository.PointCountResult(b.userName, b.points, count(b)) from Bet b group by b.userName, b.points order by b.points desc, count(b) desc, b.userName asc")
	List<PointCountResult> countNumberOfPointsByUser();

	@Modifying
	@Query("update Bet b set b.userName = :newUsername where b.userName = :oldUsername")
	void renameUser(@Param("oldUsername") String oldUsername, @Param("newUsername") String newUsername);
}
