package de.fred4jupiter.fredbet.web.info;

import de.fred4jupiter.fredbet.service.standings.StandingsContainer;
import de.fred4jupiter.fredbet.service.standings.StandingsService;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/standings")
public class StandingsController {

    private static final String PAGE_GROUP_TABLE = "info/standings";

    private final StandingsService standingsService;

    public StandingsController(StandingsService standingsService) {
        this.standingsService = standingsService;
    }

    @GetMapping
    public String show(Model model) {
        StandingsContainer standingsContainer = standingsService.calculateStandings(LocaleContextHolder.getLocale());
        model.addAttribute("standingsContainer", standingsContainer);
        return PAGE_GROUP_TABLE;
    }
}
