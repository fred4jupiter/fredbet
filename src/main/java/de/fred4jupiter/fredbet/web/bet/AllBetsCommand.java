package de.fred4jupiter.fredbet.web.bet;

import de.fred4jupiter.fredbet.domain.Bet;
import de.fred4jupiter.fredbet.domain.Match;
import de.fred4jupiter.fredbet.util.Validator;
import de.fred4jupiter.fredbet.web.AbstractMatchHeaderCommand;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

public class AllBetsCommand extends AbstractMatchHeaderCommand {

	private List<Bet> allBetsForMatch;

	private Match match;

	private String redirectViewName;

	public String getBackUrl() {
        String view = RedirectViewName.resolveBackUrl(redirectViewName);
        return view + "#" + getMatchId();
    }

	public String getRedirectViewName() {
		return redirectViewName;
	}

	public void setRedirectViewName(String redirectViewName) {
		this.redirectViewName = redirectViewName;
	}

	public List<Bet> getAllBetsForMatch() {
		return allBetsForMatch;
	}

	public Long getMatchId() {
		return this.match.getId();
	}

	public Match getMatch() {
		return match;
	}

	public void setAllBetsForMatch(List<Bet> allBetsForMatch) {
		this.allBetsForMatch = allBetsForMatch;
	}

	public void setMatch(Match match) {
		this.match = match;
	}

	public GroupAvg getAvgGroupBet() {
		double teamOne = 0.0;
		double teamTwo = 0.0;
		if (Validator.isEmpty(allBetsForMatch)) {
			return new GroupAvg(teamOne, teamTwo);
		}

		for (Bet bet : allBetsForMatch) {
			teamOne = teamOne + bet.getGoalsTeamOne();
			teamTwo = teamTwo + bet.getGoalsTeamTwo();
		}
		return new GroupAvg(teamOne / allBetsForMatch.size(), teamTwo / allBetsForMatch.size());
	}
}
