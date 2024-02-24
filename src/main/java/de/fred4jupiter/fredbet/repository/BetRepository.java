package de.fred4jupiter.fredbet.repository;

import de.fred4jupiter.fredbet.domain.Bet;
import de.fred4jupiter.fredbet.domain.Match;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BetRepository extends JpaRepository<Bet, Long>, BetRepositoryCustom {

    Bet findByUserNameAndMatch(String currentUsername, Match match);

    List<Bet> findByUserName(String currentUsername);

    List<Bet> findByMatch(Match match);

    List<Bet> findByMatchIdOrderByUserNameAsc(Long id);

    Long countByMatch(Match match);

    @Query("""
            select new de.fred4jupiter.fredbet.repository.PointCountResult(b.userName, b.points, count(b))
            from Bet b
            where b.userName != :adminUsername
            group by b.userName, b.points
            order by b.points desc, count(b) desc, b.userName asc
            """)
    List<PointCountResult> countNumberOfPointsByUser(@Param("adminUsername") String adminUsername);

    @Modifying
    @Query("update Bet b set b.userName = :newUsername where b.userName = :oldUsername")
    void renameUser(@Param("oldUsername") String oldUsername, @Param("newUsername") String newUsername);

    @Query("select count(joker) from Bet where joker = true and userName = :userName")
    Integer countNumberOfJokerUsed(@Param("userName") String userName);

    @Query("select b from Bet b where b.joker = true and b.userName = :userName and b.match.id = :matchId")
    Bet findBetsOfGivenMatchWithJokerSet(@Param("userName") String username, @Param("matchId") Long matchId);

    @Query("""
            Select b
            from Bet b
            LEFT JOIN FETCH b.match
            where b.goalsTeamOne is not null
            and b.goalsTeamTwo is not null
            and b.match.teamOne.goals is not null
            and b.match.teamTwo.goals is not null
            """)
    List<Bet> findAllBetsWithMatches();

    @Query("""
            select new de.fred4jupiter.fredbet.repository.PointCourseResult(b.userName, b.points, b.match)
            from Bet b
            where b.userName in :usernames
            order by b.match.kickOffDate asc
            """)
    List<PointCourseResult> queryPointsCourse(@Param("usernames") List<String> usernames);

    @Query("select new de.fred4jupiter.fredbet.repository.PointsPerUser(b.userName, sum(b.points)) from Bet b group by b.userName")
    List<PointsPerUser> queryPointsPerUser();
}
