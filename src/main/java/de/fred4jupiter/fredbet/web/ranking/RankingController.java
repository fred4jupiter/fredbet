package de.fred4jupiter.fredbet.web.ranking;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import de.fred4jupiter.fredbet.service.RankingService;

@Controller
@RequestMapping("/ranking")
public class RankingController {

	@Autowired
	private RankingService rankingService;

	@RequestMapping
	public ModelAndView list() {
		List<RankingCommand> rankings = rankingService.calculateCurrentRanking();
		return new ModelAndView("ranking/list", "rankings", rankings);
	}
}
