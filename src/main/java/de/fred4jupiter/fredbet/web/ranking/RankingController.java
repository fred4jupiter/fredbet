package de.fred4jupiter.fredbet.web.ranking;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import de.fred4jupiter.fredbet.repository.UsernamePoints;
import de.fred4jupiter.fredbet.service.RankingService;

@Controller
@RequestMapping("/ranking")
public class RankingController {

	@Autowired
	private RankingService rankingService;

	@RequestMapping
	public ModelAndView list() {
		List<UsernamePoints> rankings = rankingService.calculateCurrentRanking();
		for (int i = 0; i < rankings.size(); i++) {
			UsernamePoints usernamePoints = rankings.get(i);
			if (i == 0) {
				usernamePoints.setCssRankClass("label-success");	
			}
			else if (i == 1) {
				usernamePoints.setCssRankClass("label-primary");	
			}
			else if (i == 1) {
				usernamePoints.setCssRankClass("label-warning");	
			}
			else {
				usernamePoints.setCssRankClass("label-default");
			}
		}
		return new ModelAndView("ranking/list", "rankings", rankings);
	}
}
