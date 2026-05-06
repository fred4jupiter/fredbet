package de.fred4jupiter.fredbet.web.team;

import de.fred4jupiter.fredbet.TeamService;
import de.fred4jupiter.fredbet.domain.SvgImage;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/team")
public class TeamImageController {

    private final TeamService teamService;

    public TeamImageController(TeamService teamService) {
        this.teamService = teamService;
    }

    @GetMapping(value = "/{teamId}", produces = "image/svg+xml")
    public ResponseEntity<String> getTeamImage(@PathVariable Long teamId) {
        SvgImage svgImage = teamService.loadCrestImage(teamId);
        if (svgImage == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(svgImage.svgContent());
    }
}
