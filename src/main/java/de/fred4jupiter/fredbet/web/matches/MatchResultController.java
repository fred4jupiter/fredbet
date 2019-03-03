package de.fred4jupiter.fredbet.web.matches;

import de.fred4jupiter.fredbet.domain.Match;
import de.fred4jupiter.fredbet.security.FredBetPermission;
import de.fred4jupiter.fredbet.service.MatchService;
import de.fred4jupiter.fredbet.web.WebMessageUtil;
import de.fred4jupiter.fredbet.web.bet.RedirectViewName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Controller
@RequestMapping("/matchresult")
public class MatchResultController {

    private static final String VIEW_EDIT_MATCHRESULT = "matches/matchresult";

    @Autowired
    private MatchService matchService;

    @Autowired
    private WebMessageUtil webMessageUtil;

    @PreAuthorize("hasAuthority('" + FredBetPermission.PERM_EDIT_MATCH_RESULT + "')")
    @GetMapping("/{id}")
    public String edit(@PathVariable("id") Long matchId, @RequestParam(required = false) String redirectViewName, Model model) {
        Match match = matchService.findMatchById(matchId);
        MatchResultCommand matchResultCommand = toMatchResultCommand(match);
        matchResultCommand.setRedirectViewName(redirectViewName);
        model.addAttribute("matchResultCommand", matchResultCommand);
        return VIEW_EDIT_MATCHRESULT;
    }

    @PreAuthorize("hasAuthority('" + FredBetPermission.PERM_EDIT_MATCH_RESULT + "')")
    @PostMapping
    public String save(@Valid MatchResultCommand matchResultCommand, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("matchResultCommand", matchResultCommand);
            return VIEW_EDIT_MATCHRESULT;
        }

        Match match = matchService.findMatchById(matchResultCommand.getMatchId());
        match.setGoalsTeamOne(matchResultCommand.getTeamResultOne());
        match.setGoalsTeamTwo(matchResultCommand.getTeamResultTwo());
        match.setPenaltyWinnerOne(matchResultCommand.isPenaltyWinnerOne());

        matchService.save(match);

        String view = RedirectViewName.resolveRedirect(matchResultCommand.getRedirectViewName());
        return view + "#" + matchResultCommand.getMatchId();
    }

    private MatchResultCommand toMatchResultCommand(Match match) {
        MatchResultCommand matchResultCommand = new MatchResultCommand();
        matchResultCommand.setMatchId(match.getId());
        matchResultCommand.setGroupMatch(match.isGroupMatch());

        if (match.hasContriesSet()) {
            matchResultCommand.setTeamNameOne(webMessageUtil.getCountryName(match.getCountryOne()));
            matchResultCommand.setIconPathTeamOne(match.getCountryOne().getIconPathBig());

            matchResultCommand.setTeamNameTwo(webMessageUtil.getCountryName(match.getCountryTwo()));
            matchResultCommand.setIconPathTeamTwo(match.getCountryTwo().getIconPathBig());
            matchResultCommand.setShowCountryIcons(true);
        } else {
            matchResultCommand.setTeamNameOne(match.getTeamNameOne());
            matchResultCommand.setTeamNameTwo(match.getTeamNameTwo());
        }
        matchResultCommand.setTeamResultOne(match.getGoalsTeamOne());
        matchResultCommand.setTeamResultTwo(match.getGoalsTeamTwo());
        matchResultCommand.setPenaltyWinnerOne(match.isPenaltyWinnerOne());
        return matchResultCommand;
    }
}
