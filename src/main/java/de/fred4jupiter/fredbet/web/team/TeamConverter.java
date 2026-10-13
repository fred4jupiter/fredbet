package de.fred4jupiter.fredbet.web.team;

import de.fred4jupiter.fredbet.domain.entity.Team;
import de.fred4jupiter.fredbet.team.TeamService;
import org.apache.commons.lang3.StringUtils;
import org.jspecify.annotations.NonNull;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

/**
 * Converts the id of a saved team (e.g. from a team selection) to the team.
 */
@Component
public class TeamConverter implements Converter<String, Team> {

    private final TeamService teamService;

    public TeamConverter(TeamService teamService) {
        this.teamService = teamService;
    }

    @Override
    public Team convert(@NonNull String teamId) {
        if (StringUtils.isBlank(teamId)) {
            return teamService.createFallbackPlaceholder();
        }

        return teamService.findTeamById(Long.parseLong(teamId.trim()));
    }
}
