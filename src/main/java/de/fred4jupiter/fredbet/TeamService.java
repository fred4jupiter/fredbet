package de.fred4jupiter.fredbet;

import de.fred4jupiter.fredbet.crests.CrestsCountryResolver;
import de.fred4jupiter.fredbet.domain.Country;
import de.fred4jupiter.fredbet.domain.entity.Team;
import de.fred4jupiter.fredbet.match.TeamRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.function.Consumer;

@Service
@Transactional
public class TeamService {

    private final TeamRepository teamRepository;

    private final CrestsCountryResolver crestsCountryResolver;

    public TeamService(TeamRepository teamRepository, CrestsCountryResolver crestsCountryResolver) {
        this.teamRepository = teamRepository;
        this.crestsCountryResolver = crestsCountryResolver;
    }

    public Team findOrCreateTeam(Country country, String teamName) {
        return findOrCreateTeam(country, teamName, _ -> {
        });
    }

    public Team findOrCreateTeam(Country country, String teamName, Consumer<Team> newTeamCallback) {
        Team team = teamRepository.findByCountryOrName(country, teamName);
        if (team != null) {
            return team;
        }

        Team newTeam = new Team();
        if (country != null) {
            newTeam.setCountry(country);
            newTeam.setSvgContent(crestsCountryResolver.loadCrestsImageFor(country));
            newTeam.setName(null);
        } else {
            newTeam.setName(StringUtils.isNotBlank(teamName) ? teamName : "Not yet defined");
        }

        newTeamCallback.accept(newTeam);

        return teamRepository.save(newTeam);
    }
}
