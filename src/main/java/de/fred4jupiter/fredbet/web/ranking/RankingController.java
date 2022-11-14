package de.fred4jupiter.fredbet.web.ranking;

import de.fred4jupiter.fredbet.domain.RankingSelection;
import de.fred4jupiter.fredbet.repository.UsernamePoints;
import de.fred4jupiter.fredbet.service.ranking.RankingService;
import de.fred4jupiter.fredbet.util.ResponseEntityUtil;
import de.fred4jupiter.fredbet.util.Validator;
import de.fred4jupiter.fredbet.web.WebMessageUtil;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Controller
@RequestMapping("/ranking")
public class RankingController {

    private static final String CONTENT_TYPE_PDF = "application/pdf";

    private static final String PAGE_RANKING = "ranking/list";

    private final RankingService rankingService;

    private final WebMessageUtil messageUtil;

    public RankingController(RankingService rankingService, WebMessageUtil messageUtil) {
        this.rankingService = rankingService;
        this.messageUtil = messageUtil;
    }

    @GetMapping
    public String list(Model model) {
        return queryRanking(model, RankingSelection.MIXED);
    }

    @GetMapping("/{mode}")
    public String list(Model model, @PathVariable("mode") String mode) {
        return queryRanking(model, RankingSelection.fromMode(mode));
    }

    @GetMapping(value = "/pdf", produces = CONTENT_TYPE_PDF)
    public ResponseEntity<byte[]> exportAllBets() {
        final String fileName = createFilename();
        byte[] fileContent = this.rankingService.exportBetsToPdf(LocaleContextHolder.getLocale());
        if (fileContent == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntityUtil.createResponseEntity(fileName, fileContent, CONTENT_TYPE_PDF, ResponseEntityUtil.DownloadType.INLINE);
    }

    private String createFilename() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMyy_HHmmss")) + "_FredBet-Ranking.pdf";
    }

    private String queryRanking(Model model, RankingSelection rankingSelection) {
        List<UsernamePoints> rankings = rankingService.calculateCurrentRanking(rankingSelection);
        if (Validator.isEmpty(rankings) && RankingSelection.MIXED.equals(rankingSelection)) {
            messageUtil.addInfoMsg(model, "ranking.noRankings");
            model.addAttribute("rankings", rankings);
            model.addAttribute("rankingSelection", rankingSelection);
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
        model.addAttribute("rankingSelection", rankingSelection);
        return PAGE_RANKING;
    }
}
