package de.fred4jupiter.fredbet.web.matches;

import de.fred4jupiter.fredbet.domain.Group;
import de.fred4jupiter.fredbet.service.MatchService;
import de.fred4jupiter.fredbet.web.WebMessageUtil;
import de.fred4jupiter.fredbet.web.bet.RedirectViewName;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/matches")
public class MatchController {

    private static final String VIEW_LIST_MATCHES = "matches/list";

    private final WebMessageUtil messageUtil;

    private final MatchCommandMapper matchCommandMapper;

    public MatchController(WebMessageUtil messageUtil, MatchCommandMapper matchCommandMapper) {
        this.messageUtil = messageUtil;
        this.matchCommandMapper = matchCommandMapper;
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
