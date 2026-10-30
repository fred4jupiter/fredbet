package de.fred4jupiter.fredbet.web.matches;

import de.fred4jupiter.fredbet.betting.BetsPdfExportService;
import de.fred4jupiter.fredbet.domain.Group;
import de.fred4jupiter.fredbet.match.MatchService;
import de.fred4jupiter.fredbet.security.SecurityService;
import de.fred4jupiter.fredbet.util.ResponseEntityUtil;
import de.fred4jupiter.fredbet.web.WebMessageUtil;
import de.fred4jupiter.fredbet.web.bet.RedirectViewName;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Controller
@RequestMapping("/matches")
public class MatchController {

    private static final String VIEW_LIST_MATCHES = "matches/list";

    private static final String CONTENT_TYPE_PDF = "application/pdf";

    private final WebMessageUtil messageUtil;

    private final MatchCommandMapper matchCommandMapper;

    private final BetsPdfExportService betsPdfExportService;

    private final SecurityService securityService;

    private final DateTimeFormatter fileNameDateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm");

    public MatchController(WebMessageUtil messageUtil, MatchCommandMapper matchCommandMapper, BetsPdfExportService betsPdfExportService,
                           SecurityService securityService) {
        this.messageUtil = messageUtil;
        this.matchCommandMapper = matchCommandMapper;
        this.betsPdfExportService = betsPdfExportService;
        this.securityService = securityService;
    }

    /**
     * All matches with the bets of the current user as PDF (DIN A4).
     */
    @GetMapping(value = "/pdf", produces = CONTENT_TYPE_PDF)
    public ResponseEntity<byte[]> exportMyBetsAsPdf() {
        final String username = securityService.getCurrentUserName();
        byte[] fileContent = betsPdfExportService.exportBetsOfUser(username, LocaleContextHolder.getLocale());
        if (fileContent == null) {
            return ResponseEntity.notFound().build();
        }

        final String fileName = "%s_fredbet_bets_%s.pdf".formatted(fileNameDateFormatter.format(LocalDateTime.now()), username);
        return ResponseEntityUtil.createResponseEntity(fileName, fileContent, CONTENT_TYPE_PDF);
    }

    @GetMapping
    public String listAllMatches(Model model) {
        List<MatchCommand> matches = matchCommandMapper.findMatches(MatchService::findAllMatches);
        model.addAttribute("allMatches", matches);
        model.addAttribute("heading", messageUtil.getMessageFor("all.matches"));
        model.addAttribute("redirectViewName", RedirectViewName.MATCHES);
        return VIEW_LIST_MATCHES;
    }

    @GetMapping("/past")
    public String pastMatches(Model model) {
        List<MatchCommand> matches = matchCommandMapper.findMatches(MatchService::findAllPastMatches);
        model.addAttribute("allMatches", matches);
        model.addAttribute("heading", messageUtil.getMessageFor("all.past.matches"));
        model.addAttribute("redirectViewName", RedirectViewName.MATCHES);
        return VIEW_LIST_MATCHES;
    }

    @GetMapping("/upcoming")
    public String upcomingMatches(Model model) {
        List<MatchCommand> matches = matchCommandMapper.findMatches(MatchService::findUpcomingMatches);
        model.addAttribute("allMatches", matches);
        model.addAttribute("heading", messageUtil.getMessageFor("upcoming.matches"));
        model.addAttribute("redirectViewName", RedirectViewName.MATCHES_UPCOMING);
        return VIEW_LIST_MATCHES;
    }

    @GetMapping("/group/{groupName}")
    public String listByGroup(@PathVariable String groupName, Model model) {
        final Group group = Group.valueOf(groupName);
        List<MatchCommand> matches = matchCommandMapper.findMatches(matchService -> matchService.findMatchesByGroup(group));
        model.addAttribute("allMatches", matches);
        model.addAttribute("heading", messageUtil.getMessageFor(group.getTitleMsgKey()));
        model.addAttribute("redirectViewName", RedirectViewName.createRedirectForGroup(group));
        return VIEW_LIST_MATCHES;
    }

    @GetMapping("/joker")
    public String jokerMatches(Model model) {
        List<MatchCommand> matches = matchCommandMapper.findMatches((username, matchService) -> matchService.findJokerMatches(username));
        model.addAttribute("allMatches", matches);
        model.addAttribute("heading", messageUtil.getMessageFor("joker.matches"));
        model.addAttribute("redirectViewName", RedirectViewName.MATCHES_JOKER);
        return VIEW_LIST_MATCHES;
    }

    @GetMapping("/today")
    public String matchesOfToday(Model model) {
        List<MatchCommand> matches = matchCommandMapper.findMatches(MatchService::findMatchesOfToday);
        model.addAttribute("allMatches", matches);
        model.addAttribute("heading", messageUtil.getMessageFor("today.matches"));
        model.addAttribute("redirectViewName", RedirectViewName.MATCHES_TODAY);
        return VIEW_LIST_MATCHES;
    }

    @GetMapping("/yesterday")
    public String matchesOfYesterday(Model model) {
        List<MatchCommand> matches = matchCommandMapper.findMatches(MatchService::findMatchesOfYesterday);
        model.addAttribute("allMatches", matches);
        model.addAttribute("heading", messageUtil.getMessageFor("yesterday.matches"));
        model.addAttribute("redirectViewName", RedirectViewName.MATCHES_TODAY);
        return VIEW_LIST_MATCHES;
    }

    @GetMapping("/finishednoresult")
    public String finishedMatchesWithoutResult(Model model) {
        List<MatchCommand> matches = matchCommandMapper.findMatches(MatchService::findFinishedMatchesWithoutResult);
        model.addAttribute("allMatches", matches);
        model.addAttribute("heading", messageUtil.getMessageFor("finishednoresult.matches"));
        model.addAttribute("redirectViewName", RedirectViewName.MATCHES_TODAY);
        return VIEW_LIST_MATCHES;
    }
}
