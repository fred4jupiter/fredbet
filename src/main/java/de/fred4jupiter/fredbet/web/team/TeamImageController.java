package de.fred4jupiter.fredbet.web.team;

import de.fred4jupiter.fredbet.TeamService;
import de.fred4jupiter.fredbet.domain.SvgImage;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.request.WebRequest;

import java.util.concurrent.TimeUnit;

@Controller
@RequestMapping("/team")
public class TeamImageController {

    private final TeamService teamService;

    public TeamImageController(TeamService teamService) {
        this.teamService = teamService;
    }

    @GetMapping(value = "/{teamId}.svg", produces = "image/svg+xml")
    public ResponseEntity<String> getTeamImage(@PathVariable Long teamId, WebRequest webRequest) {
        final SvgImage svgImage = teamService.loadCrestImage(teamId);
        if (svgImage == null) {
            return ResponseEntity.notFound().build();
        }

        final String etag = "\"" + svgImage.version() + "\"";
        final CacheControl cacheControl = CacheControl.maxAge(30, TimeUnit.DAYS);
        if (webRequest.checkNotModified(etag)) {
            return ResponseEntity.status(HttpStatus.NOT_MODIFIED)
                .cacheControl(cacheControl)
                .eTag(etag)
                .build();
        }

        return ResponseEntity.ok()
            .cacheControl(cacheControl)
            .eTag(etag)
            .body(svgImage.svgContent());
    }
}
