package de.fred4jupiter.fredbet.web.admin;

import de.fred4jupiter.fredbet.betting.BetsPdfExportService;
import de.fred4jupiter.fredbet.excel.ReportService;
import de.fred4jupiter.fredbet.security.FredBetPermission;
import de.fred4jupiter.fredbet.util.ResponseEntityUtil;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/export")
@PreAuthorize("hasAuthority('" + FredBetPermission.PERM_ADMINISTRATION + "')")
public class ExportController {

    private static final String CONTENT_TYPE_EXCEL = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";

    private static final String CONTENT_TYPE_PDF = "application/pdf";

    private final ReportService reportService;

    private final BetsPdfExportService betsPdfExportService;

    public ExportController(ReportService reportService, BetsPdfExportService betsPdfExportService) {
        this.reportService = reportService;
        this.betsPdfExportService = betsPdfExportService;
    }

    @GetMapping("/show")
    public String showExportPage() {
        return "admin/export";
    }

    @GetMapping(value = "/allBetsPdf", produces = CONTENT_TYPE_PDF)
    public ResponseEntity<byte[]> exportAllBetsAsPdf() {
        final String fileName = "allBets.pdf";
        byte[] fileContent = this.betsPdfExportService.exportBetsOfAllUsers(LocaleContextHolder.getLocale());
        if (fileContent == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntityUtil.createResponseEntity(fileName, fileContent, CONTENT_TYPE_PDF);
    }

    @GetMapping(value = "/allBets", produces = CONTENT_TYPE_EXCEL)
    public ResponseEntity<byte[]> exportAllBets() {
        final String fileName = "allBets.xlsx";
        byte[] fileContent = this.reportService.exportBetsToExcel(LocaleContextHolder.getLocale());
        if (fileContent == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntityUtil.createResponseEntity(fileName, fileContent, CONTENT_TYPE_EXCEL);
    }

    @GetMapping(value = "/extraBets", produces = CONTENT_TYPE_EXCEL)
    public ResponseEntity<byte[]> exportExtraBets() {
        final String fileName = "extraBets.xlsx";
        byte[] fileContent = this.reportService.exportExtraBetsToExcel(LocaleContextHolder.getLocale());
        if (fileContent == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntityUtil.createResponseEntity(fileName, fileContent, CONTENT_TYPE_EXCEL);
    }

    @GetMapping(value = "/pointsCount", produces = CONTENT_TYPE_EXCEL)
    public ResponseEntity<byte[]> exportPointsCountByUser() {
        final String fileName = "numberOfReachedPointsCount.xlsx";
        byte[] fileContent = this.reportService.exportNumberOfPointsInBets(LocaleContextHolder.getLocale());
        if (fileContent == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntityUtil.createResponseEntity(fileName, fileContent, CONTENT_TYPE_EXCEL);
    }

    @GetMapping(value = "/ranking", produces = CONTENT_TYPE_EXCEL)
    public ResponseEntity<byte[]> exportRanking() {
        final String fileName = "ranking.xlsx";
        byte[] fileContent = this.reportService.exportRankingToExcel(LocaleContextHolder.getLocale());
        if (fileContent == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntityUtil.createResponseEntity(fileName, fileContent, CONTENT_TYPE_EXCEL);
    }
}
