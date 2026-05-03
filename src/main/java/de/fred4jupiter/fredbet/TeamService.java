package de.fred4jupiter.fredbet;

import de.fred4jupiter.fredbet.crests.CrestsCountryResolver;
import de.fred4jupiter.fredbet.domain.Country;
import de.fred4jupiter.fredbet.domain.entity.Team;
import de.fred4jupiter.fredbet.match.TeamRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
        Team teamByCountry = teamRepository.findByCountry(country);
        if (teamByCountry != null) {
            return teamByCountry;
        }

        Team teamByName = teamRepository.findByName(teamName);
        if (teamByName != null) {
            return teamByName;
        }

        Team team = new Team();
        team.setCountry(country);
        team.setSvgContent(crestsCountryResolver.loadCrestsImageFor(country));
        team.setName(teamName);

        return teamRepository.save(team);
    }
}
