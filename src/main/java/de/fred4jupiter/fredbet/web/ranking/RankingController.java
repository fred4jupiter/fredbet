package de.fred4jupiter.fredbet.web.ranking;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import de.fred4jupiter.fredbet.domain.RankingSelection;
import de.fred4jupiter.fredbet.repository.UsernamePoints;
import de.fred4jupiter.fredbet.service.ranking.RankingService;
import de.fred4jupiter.fredbet.util.Validator;
import de.fred4jupiter.fredbet.web.WebMessageUtil;

@Controller
@RequestMapping("/ranking")
public class RankingController {

	private static final String PAGE_RANKING = "ranking/list";

	@Autowired
	private RankingService rankingService;

	@Autowired
	private WebMessageUtil messageUtil;

	@GetMapping
	public String list(Model model) {
		return queryRanking(model, RankingSelection.MIXED);
	}

	@GetMapping("/{mode}")
	public String list(Model model, @PathVariable("mode") String mode) {
		return queryRanking(model, RankingSelection.fromMode(mode));
	}

	private String queryRanking(Model model, RankingSelection rankingSelection) {
		List<UsernamePoints> rankings = rankingService.calculateCurrentRanking(rankingSelection);
		if (Validator.isEmpty(rankings) && RankingSelection.MIXED.equals(rankingSelection)) {
			messageUtil.addInfoMsg(model, "ranking.noRankings");
			model.addAttribute("rankings", rankings);
			return PAGE_RANKING;
		}

		for (int i = 0; i < rankings.size(); i++) {
			UsernamePoints usernamePoints = rankings.get(i);
			if (i == 0) {
				usernamePoints.setCssRankClass("label-success");
			} else if (i == 1) {
				usernamePoints.setCssRankClass("label-primary");
			} else if (i == 2) {
				usernamePoints.setCssRankClass("label-warning");
			} else if (i == 3) {
				usernamePoints.setCssRankClass("label-rank4");
			} else if (i == 4) {
				usernamePoints.setCssRankClass("label-rank5");
			} else if (i == 5) {
				usernamePoints.setCssRankClass("label-rank6");
			} else {
				usernamePoints.setCssRankClass("label-default");
			}
		}

		model.addAttribute("rankings", rankings);
		return PAGE_RANKING;
	}
}
