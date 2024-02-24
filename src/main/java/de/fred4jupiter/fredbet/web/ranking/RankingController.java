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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

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

    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMyy_HHmmss");

    public RankingController(RankingService rankingService, WebMessageUtil messageUtil) {
        this.rankingService = rankingService;
        this.messageUtil = messageUtil;
    }

    @GetMapping
    public String list(Model model, @RequestParam(required = false, defaultValue = "mixed") String mode) {
        return queryRanking(model, RankingSelection.fromMode(mode));
    }

    @GetMapping(value = "/pdf", produces = CONTENT_TYPE_PDF)
    public ResponseEntity<byte[]> exportAllBets(@RequestParam(required = false, defaultValue = "mixed") String mode) {
        final RankingSelection rankingSelection = RankingSelection.fromMode(mode);
        final String fileName = createFilename(mode);
        byte[] fileContent = this.rankingService.exportBetsToPdf(LocaleContextHolder.getLocale(), rankingSelection);
        if (fileContent == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntityUtil.createResponseEntity(fileName, fileContent, CONTENT_TYPE_PDF, ResponseEntityUtil.DownloadType.INLINE);
    }

    private String createFilename(String mode) {
        return "%s_%s_fredbet_ranking.pdf".formatted(dateTimeFormatter.format(LocalDateTime.now()), mode);
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
            rankings.get(i).setCssRankClass(getCssRankingClassForPosition(i));
        }

        model.addAttribute("rankings", rankings);
        model.addAttribute("rankingSelection", rankingSelection);
        return PAGE_RANKING;
    }

    private String getCssRankingClassForPosition(int position) {
        return switch (position) {
            case 0 -> "label-success";
            case 1 -> "label-primary";
            case 2 -> "label-warning";
            case 3 -> "label-rank4";
            case 4 -> "label-rank5";
            case 5 -> "label-rank6";
            default -> "label-default";
        };
    }
}
