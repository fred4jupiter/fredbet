package de.fred4jupiter.fredbet.web.matches;

import de.fred4jupiter.fredbet.domain.Group;
import de.fred4jupiter.fredbet.security.SecurityService;
import de.fred4jupiter.fredbet.web.WebMessageUtil;
import de.fred4jupiter.fredbet.web.bet.RedirectViewName;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    private SecurityService securityBean;

    @Autowired
    private WebMessageUtil messageUtil;

    @Autowired
    private MatchCommandMapper matchCommandMapper;

    @GetMapping
    public String listAllMatches(Model model) {
        List<MatchCommand> matches = matchCommandMapper.findAllMatches(securityBean.getCurrentUserName());
        model.addAttribute("allMatches", matches);
        model.addAttribute("heading", messageUtil.getMessageFor("all.matches"));
        model.addAttribute("redirectViewName", RedirectViewName.MATCHES);
        return VIEW_LIST_MATCHES;
    }

    @GetMapping("upcoming")
    public String upcomingMatches(Model model) {
        List<MatchCommand> matches = matchCommandMapper.findAllUpcomingMatches(securityBean.getCurrentUserName());
        model.addAttribute("allMatches", matches);
        model.addAttribute("heading", messageUtil.getMessageFor("upcoming.matches"));
        model.addAttribute("redirectViewName", RedirectViewName.MATCHES_UPCOMING);
        return VIEW_LIST_MATCHES;
    }

    @GetMapping("/group/{groupName}")
    public String listByGroup(@PathVariable("groupName") String groupName, Model model) {
        final Group group = Group.valueOf(groupName);
        List<MatchCommand> matches = matchCommandMapper.findMatchesByGroup(securityBean.getCurrentUserName(), group);
        model.addAttribute("allMatches", matches);
        model.addAttribute("heading", messageUtil.getMessageFor("group.entry." + groupName));
        model.addAttribute("redirectViewName", RedirectViewName.createRedirectForGroup(group));
        return VIEW_LIST_MATCHES;
    }

    @GetMapping("/joker")
    public String jokerMatches(Model model) {
        List<MatchCommand> matches = matchCommandMapper.findJokerMatches(securityBean.getCurrentUserName());
        model.addAttribute("allMatches", matches);
        model.addAttribute("heading", messageUtil.getMessageFor("joker.matches"));
        model.addAttribute("redirectViewName", RedirectViewName.MATCHES_JOKER);
        return VIEW_LIST_MATCHES;
    }

    @GetMapping("/today")
    public String matchesOfToday(Model model) {
        List<MatchCommand> matches = matchCommandMapper.findMatchesOfToday(securityBean.getCurrentUserName());
        model.addAttribute("allMatches", matches);
        model.addAttribute("heading", messageUtil.getMessageFor("today.matches"));
        model.addAttribute("redirectViewName", RedirectViewName.MATCHES_TODAY);
        return VIEW_LIST_MATCHES;
    }
}
