package de.fred4jupiter.fredbet.match;

import de.fred4jupiter.fredbet.domain.entity.Team;
import de.fred4jupiter.fredbet.teambundle.TeamBundleEntry;
import de.fred4jupiter.fredbet.teambundle.TeamBundleEntryType;
import io.micrometer.common.util.StringUtils;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TeamRepository extends JpaRepository<Team, Long> {

    Team findByTeamTypeAndTeamKey(TeamBundleEntryType teamType, String teamKey);

    Team findByName(String name);

    int countByTeamTypeAndTeamKey(TeamBundleEntryType teamType, String teamKey);

    int countByName(String name);

    List<Team> findByFavouriteTrue();

    default Team findByTeamBundleEntry(TeamBundleEntry teamBundleEntry) {
        return findByTeamTypeAndTeamKey(teamBundleEntry.getType(), teamBundleEntry.name());
    }

    default int countByTeamBundleEntry(TeamBundleEntry teamBundleEntry) {
        return countByTeamTypeAndTeamKey(teamBundleEntry.getType(), teamBundleEntry.name());
    }

    default Team findByTeamBundleEntryOrName(TeamBundleEntry teamBundleEntry, String name) {
        if (teamBundleEntry == null && StringUtils.isEmpty(name)) {
            throw new IllegalArgumentException("TeamBundleEntry and Name cannot be empty!");
        }

        if (teamBundleEntry != null) {
            return findByTeamBundleEntry(teamBundleEntry);
        }

        return findByName(name);
    }

    default Team findOrCreate(Team team) {
        if (team.getTeamBundleEntry() != null) {
            Team foundByTeamBundleEntry = findByTeamBundleEntry(team.getTeamBundleEntry());
            if (foundByTeamBundleEntry != null) {
                return foundByTeamBundleEntry;
            }
        }

        if (StringUtils.isNotBlank(team.getName())) {
            Team foundByName = findByName(team.getName());
            if (foundByName != null) {
                return foundByName;
            }
        }

        return save(team);
    }
}
