package de.fred4jupiter.fredbet.repository;

import java.util.Date;
import java.util.List;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import de.fred4jupiter.fredbet.domain.Country;
import de.fred4jupiter.fredbet.domain.Group;
import de.fred4jupiter.fredbet.domain.Match;

public interface MatchRepository extends JpaRepository<Match, Long> {

	List<Match> findAllByOrderByKickOffDateAsc();

	@Query("select m from Match m where (m.group like 'GROUP%' and m.kickOffDate > :groupKickOffDate) or (m.group not like 'GROUP%' and m.kickOffDate > :koKickOffDate) or (m.goalsTeamOne is null and m.goalsTeamTwo is null) order by m.kickOffDate asc")
	List<Match> findUpcomingMatches(@Param("groupKickOffDate") Date groupKickOffDate, @Param("koKickOffDate") Date koKickOffDate);

	List<Match> findByGroupOrderByKickOffDateAsc(Group group);

	List<Match> findByCountryOne(Country country);

	List<Match> findByGroup(Group group);

	@Query("select min(a.kickOffDate) from Match a")
	Date findStartDateOfFirstMatch();
	
	@Query("select a.group from Match a ")
	Set<Group> fetchGroupsOfAllMatches();

}
