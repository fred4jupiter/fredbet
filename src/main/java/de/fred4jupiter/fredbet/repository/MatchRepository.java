package de.fred4jupiter.fredbet.repository;

import de.fred4jupiter.fredbet.domain.Country;
import de.fred4jupiter.fredbet.domain.Group;
import de.fred4jupiter.fredbet.domain.Match;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

public interface MatchRepository extends JpaRepository<Match, Long> {

    List<Match> findAllByOrderByKickOffDateAsc();

    @Query("""
                select m
                from Match m
                where (m.group in :listOfGroup and m.kickOffDate > :groupKickOffDate)
                or (m.group not in :listOfGroup and m.kickOffDate > :koKickOffDate)
                or (m.teamOne.goals is null and m.teamTwo.goals is null)
                order by m.kickOffDate asc
            """)
    List<Match> findUpcomingMatches(@Param("groupKickOffDate") LocalDateTime groupKickOffDate,
                                    @Param("koKickOffDate") LocalDateTime koKickOffDate,
                                    @Param("listOfGroup") List<Group> listOfGroup);

    default List<Match> findUpcomingMatches(LocalDateTime groupKickOffDate, LocalDateTime koKickOffDate) {
        return findUpcomingMatches(groupKickOffDate, koKickOffDate, Group.getMainGroups());
    }

    default List<Match> findUpcomingMatches(int hoursShowUpcomingGroupMatches, int hoursShowUpcomingOtherMatches) {
        LocalDateTime groupKickOffBeginSelectionDate = LocalDateTime.now().minusHours(hoursShowUpcomingGroupMatches);
        LocalDateTime koKickOffBeginSelectionDate = LocalDateTime.now().minusHours(hoursShowUpcomingOtherMatches);
        return findUpcomingMatches(groupKickOffBeginSelectionDate, koKickOffBeginSelectionDate, Group.getMainGroups());
    }

    List<Match> findByGroupOrderByKickOffDateAsc(Group group);

    List<Match> findByTeamOneCountry(Country country);

    List<Match> findByGroup(Group group);

    @Query("select min(a.kickOffDate) from Match a")
    LocalDateTime findStartDateOfFirstMatch();

    @Query("select a.group from Match a ")
    Set<Group> fetchGroupsOfAllMatches();

    @Query("Select b.match from Bet b where b.joker = TRUE and b.userName = :userName order by b.match.kickOffDate asc")
    List<Match> findMatchesOfJokerBetsForUser(@Param("userName") String userName);

    List<Match> findByKickOffDateBetweenOrderByKickOffDateAsc(LocalDateTime startDate, LocalDateTime endDate);

    @Query("select m from Match m where m.kickOffDate < :date and m.teamOne.goals is null and m.teamTwo.goals is null order by m.kickOffDate asc")
    List<Match> findFinishedMatchesWithMissingResult(@Param("date") LocalDateTime date);

    @Query("select m from Match m where m.teamOne.goals is not null and m.teamTwo.goals is not null order by m.kickOffDate asc")
    List<Match> findFinishedMatches();

    @Query("Select m.teamOne.country, m.teamTwo.country from Match m")
    List<Country[]> findAllCountries();

    @Query("""
            select case when (count(m) > 0) then true else false end
            from Match m
            where m.group in (:groups)
            """)
    boolean hasMatchesOfGroups(@Param("groups") List<Group> groups);
}
