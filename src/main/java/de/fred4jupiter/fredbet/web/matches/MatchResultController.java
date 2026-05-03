package de.fred4jupiter.fredbet.web.matches;

import de.fred4jupiter.fredbet.domain.entity.Match;
import de.fred4jupiter.fredbet.match.MatchService;
import de.fred4jupiter.fredbet.security.FredBetPermission;
import de.fred4jupiter.fredbet.web.bet.RedirectViewName;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/matchresult")
public class MatchResultController {

    private static final String VIEW_EDIT_MATCHRESULT = "matches/matchresult";

    private final MatchService matchService;

    public MatchResultController(MatchService matchService) {
        this.matchService = matchService;
    }

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
    public String save(@Valid MatchResultCommand command, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return VIEW_EDIT_MATCHRESULT;
        }

        matchService.enterMatchResult(command.getMatchId(), match -> {
            match.setGoalsTeamOne(command.getTeamResultOne());
            match.setGoalsTeamTwo(command.getTeamResultTwo());
            match.setPenaltyWinnerOne(command.isPenaltyWinnerOne());
        });

        String view = RedirectViewName.resolveRedirect(command.getRedirectViewName());
        return view + "#" + command.getMatchId();
    }

    private MatchResultCommand toMatchResultCommand(Match match) {
        MatchResultCommand matchResultCommand = new MatchResultCommand();
        matchResultCommand.setMatchId(match.getId());
        matchResultCommand.setMatch(match);

        matchResultCommand.setKnockoutMatch(match.isKnockoutMatch());

        matchResultCommand.setTeamResultOne(match.getGoalsTeamOne());
        matchResultCommand.setTeamResultTwo(match.getGoalsTeamTwo());
        matchResultCommand.setPenaltyWinnerOne(match.isPenaltyWinnerOne());
        return matchResultCommand;
    }
}
