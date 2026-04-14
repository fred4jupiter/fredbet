package de.fred4jupiter.fredbet.match;

import de.fred4jupiter.fredbet.domain.Country;
import de.fred4jupiter.fredbet.domain.entity.Team;
import io.micrometer.common.util.StringUtils;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TeamRepository extends JpaRepository<Team, Long> {

    Team findByCountry(Country country);

    Team findByName(String name);

    default Team findOrCreate(Team team) {
        if (team.getCountry() != null) {
            Team foundByCountry = findByCountry(team.getCountry());
            if (foundByCountry != null) {
                return foundByCountry;
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
