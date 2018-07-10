package de.fred4jupiter.fredbet.web.ranking;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
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

	@Autowired
	private RankingService rankingService;

	@Autowired
	private WebMessageUtil messageUtil;

	@RequestMapping
	public ModelAndView list(ModelMap modelMap) {
		return queryRanking(modelMap, RankingSelection.MIXED);
	}

	@RequestMapping("/{mode}")
	public ModelAndView list(ModelMap modelMap, @PathVariable("mode") String mode) {
		return queryRanking(modelMap, RankingSelection.fromMode(mode));
	}

	private ModelAndView queryRanking(ModelMap modelMap, RankingSelection rankingSelection) {
		List<UsernamePoints> rankings = rankingService.calculateCurrentRanking(rankingSelection);
		if (Validator.isEmpty(rankings) && RankingSelection.MIXED.equals(rankingSelection)) {
			messageUtil.addInfoMsg(modelMap, "ranking.noRankings");
			return new ModelAndView("ranking/list", "rankings", rankings);
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
		return new ModelAndView("ranking/list", "rankings", rankings);
	}
}
