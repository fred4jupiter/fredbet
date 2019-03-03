package de.fred4jupiter.fredbet.web.admin;

import de.fred4jupiter.fredbet.security.FredBetPermission;
import de.fred4jupiter.fredbet.service.excel.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/excelexport")
@PreAuthorize("hasAuthority('" + FredBetPermission.PERM_ADMINISTRATION + "')")
public class ExcelExportController {

    private static final String CONTENT_TYPE_EXCEL = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";

    @Autowired
    private ReportService reportService;

    @GetMapping("/show")
    public String showCachePage() {
        return "admin/excel_export";
    }

    @GetMapping(value = "/allBets", produces = CONTENT_TYPE_EXCEL)
    public ResponseEntity<byte[]> exportAllBets() {
        final String fileName = "allBets.xlsx";
        byte[] fileContent = this.reportService.exportBetsToExcel(LocaleContextHolder.getLocale());
        if (fileContent == null) {
            return ResponseEntity.notFound().build();
        }

        return createResponseEntity(fileName, fileContent);
    }

    @GetMapping(value = "/extraBets", produces = CONTENT_TYPE_EXCEL)
    public ResponseEntity<byte[]> exportExtraBets() {
        final String fileName = "extraBets.xlsx";
        byte[] fileContent = this.reportService.exportExtraBetsToExcel(LocaleContextHolder.getLocale());
        if (fileContent == null) {
            return ResponseEntity.notFound().build();
        }

        return createResponseEntity(fileName, fileContent);
    }

    @GetMapping(value = "/pointsCount", produces = CONTENT_TYPE_EXCEL)
    public ResponseEntity<byte[]> exportPointsCountByUser() {
        final String fileName = "numberOfReachedPointsCount.xlsx";
        byte[] fileContent = this.reportService.exportNumberOfPointsInBets(LocaleContextHolder.getLocale());
        if (fileContent == null) {
            return ResponseEntity.notFound().build();
        }

        return createResponseEntity(fileName, fileContent);
    }

    @GetMapping(value = "/ranking", produces = CONTENT_TYPE_EXCEL)
    public ResponseEntity<byte[]> exportRanking() {
        final String fileName = "ranking.xlsx";
        byte[] fileContent = this.reportService.exportRankingToExcel(LocaleContextHolder.getLocale());
        if (fileContent == null) {
            return ResponseEntity.notFound().build();
        }

        return createResponseEntity(fileName, fileContent);
    }

    private ResponseEntity<byte[]> createResponseEntity(final String fileName, byte[] fileContent) {
        return ResponseEntity.ok().header("Content-Type", CONTENT_TYPE_EXCEL)
                .header("Content-Disposition", "inline; filename=\"" + fileName + "\"").body(fileContent);
    }
}
