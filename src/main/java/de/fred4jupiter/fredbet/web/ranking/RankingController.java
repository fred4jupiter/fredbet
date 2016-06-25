package de.fred4jupiter.fredbet.web.ranking;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import de.fred4jupiter.fredbet.repository.UsernamePoints;
import de.fred4jupiter.fredbet.service.RankingService;
import de.fred4jupiter.fredbet.web.MessageUtil;

@Controller
@RequestMapping("/ranking")
public class RankingController {

	@Autowired
	private RankingService rankingService;

	@Autowired
    private MessageUtil messageUtil;
	
	@RequestMapping
	public ModelAndView list(ModelMap modelMap) {
		List<UsernamePoints> rankings = rankingService.calculateCurrentRanking();
		if (CollectionUtils.isEmpty(rankings)) {
		    messageUtil.addInfoMsg(modelMap, "ranking.noRankings");
            return new ModelAndView("ranking/list", "rankings", rankings);
		}
		
		for (int i = 0; i < rankings.size(); i++) {
			UsernamePoints usernamePoints = rankings.get(i);
			if (i == 0) {
				usernamePoints.setCssRankClass("label-success");	
			}
			else if (i == 1) {
				usernamePoints.setCssRankClass("label-primary");	
			}
			else if (i == 2) {
				usernamePoints.setCssRankClass("label-warning");	
			}
			else if (i == 3) {
				usernamePoints.setCssRankClass("label-rank4");	
			}
			else if (i == 4) {
				usernamePoints.setCssRankClass("label-rank5");	
			}
			else if (i == 5) {
				usernamePoints.setCssRankClass("label-rank6");	
			}
			else {
				usernamePoints.setCssRankClass("label-default");
			}
		}
		return new ModelAndView("ranking/list", "rankings", rankings);
	}
}
