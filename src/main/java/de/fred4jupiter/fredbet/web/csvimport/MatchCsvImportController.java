package de.fred4jupiter.fredbet.web.csvimport;

import de.fred4jupiter.fredbet.csvimport.CsvImportError;
import de.fred4jupiter.fredbet.csvimport.CsvImportException;
import de.fred4jupiter.fredbet.csvimport.CsvImportField;
import de.fred4jupiter.fredbet.csvimport.CsvTable;
import de.fred4jupiter.fredbet.csvimport.MatchCsvImportResult;
import de.fred4jupiter.fredbet.csvimport.MatchCsvImportService;
import de.fred4jupiter.fredbet.security.FredBetPermission;
import de.fred4jupiter.fredbet.web.WebMessageUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.List;

/**
 * Import of the matches of a tournament from a CSV file in three steps: upload the file, map the columns to the
 * match fields and import the matches.
 */
@Controller
@RequestMapping("/csvimport")
@PreAuthorize("hasAuthority('" + FredBetPermission.PERM_ADMINISTRATION + "')")
@SessionAttributes(MatchCsvImportController.COMMAND_NAME)
public class MatchCsvImportController {

    static final String COMMAND_NAME = "matchCsvImportCommand";

    private static final Logger LOG = LoggerFactory.getLogger(MatchCsvImportController.class);

    private static final String PAGE = "csvimport/match_csv_import";

    private static final String REDIRECT_TO_PAGE = "redirect:/csvimport";

    private static final int PREVIEW_ROWS = 10;

    private final MatchCsvImportService matchCsvImportService;

    private final WebMessageUtil webMessageUtil;

    public MatchCsvImportController(MatchCsvImportService matchCsvImportService, WebMessageUtil webMessageUtil) {
        this.matchCsvImportService = matchCsvImportService;
        this.webMessageUtil = webMessageUtil;
    }

    /**
     * Only the column mapping and the date format can be changed by the user, the uploaded CSV file is kept in the session.
     */
    @InitBinder(COMMAND_NAME)
    public void initBinder(WebDataBinder binder) {
        binder.setAllowedFields("columnMappings*", "dateFormat");
    }

    @ModelAttribute(COMMAND_NAME)
    public MatchCsvImportCommand initCommand() {
        return new MatchCsvImportCommand();
    }

    @ModelAttribute("availableFields")
    public CsvImportField[] availableFields() {
        return CsvImportField.values();
    }

    @ModelAttribute("requiredFields")
    public List<CsvImportField> requiredFields() {
        return CsvImportField.requiredFields();
    }

    @GetMapping
    public String showPage(@ModelAttribute(COMMAND_NAME) MatchCsvImportCommand command, Model model) {
        addPreview(command, model);
        return PAGE;
    }

    @PostMapping("/upload")
    public String upload(@ModelAttribute(COMMAND_NAME) MatchCsvImportCommand command, @RequestParam(value = "csvFile", required = false) MultipartFile csvFile,
                         RedirectAttributes redirect) {
        if (csvFile == null || csvFile.isEmpty()) {
            webMessageUtil.addErrorMsg(redirect, "csvimport.msg.noFileGiven");
            return REDIRECT_TO_PAGE;
        }

        try {
            CsvTable csvTable = matchCsvImportService.parseCsv(csvFile.getBytes());
            command.setFileName(csvFile.getOriginalFilename());
            command.setCsvTable(csvTable);
            command.setColumnMappings(matchCsvImportService.suggestMapping(csvTable.headers()));
            command.setDateFormat(null);
            webMessageUtil.addInfoMsg(redirect, "csvimport.msg.uploaded", String.valueOf(csvTable.rowCount()));
        } catch (CsvImportException e) {
            LOG.info("Could not read CSV file {}: {}", csvFile.getOriginalFilename(), e.getMessage());
            webMessageUtil.addErrorMsg(redirect, e.getMsgKey());
        } catch (IOException e) {
            LOG.error("Could not read uploaded CSV file: {}", e.getMessage(), e);
            webMessageUtil.addErrorMsg(redirect, "csvimport.msg.uploadFailed", e.getMessage());
        }
        return REDIRECT_TO_PAGE;
    }

    @PostMapping("/import")
    public String importMatches(@ModelAttribute(COMMAND_NAME) MatchCsvImportCommand command, Model model, RedirectAttributes redirect,
                                SessionStatus sessionStatus) {
        if (!command.isUploaded()) {
            webMessageUtil.addErrorMsg(redirect, "csvimport.msg.noFileGiven");
            return REDIRECT_TO_PAGE;
        }

        MatchCsvImportResult result = matchCsvImportService.importMatches(command.getCsvTable(), command.getColumnMappings(), command.getDateFormat());
        if (!result.isSuccessful()) {
            webMessageUtil.addErrorMsg(model, "csvimport.msg.importFailed");
            model.addAttribute("importErrors", toMessages(result.errors()));
            addPreview(command, model);
            return PAGE;
        }

        sessionStatus.setComplete();
        webMessageUtil.addInfoMsg(redirect, "csvimport.msg.imported", result.importedMatches(), result.createdTeams());
        if (!result.warnings().isEmpty()) {
            redirect.addFlashAttribute("importWarnings", toMessages(result.warnings()));
            return REDIRECT_TO_PAGE;
        }
        return "redirect:/matches";
    }

    @GetMapping("/reset")
    public String reset(SessionStatus sessionStatus) {
        sessionStatus.setComplete();
        return REDIRECT_TO_PAGE;
    }

    private void addPreview(MatchCsvImportCommand command, Model model) {
        if (command.isUploaded()) {
            model.addAttribute("previewRows", command.getCsvTable().firstRows(PREVIEW_ROWS));
        }
    }

    private List<String> toMessages(List<CsvImportError> errors) {
        return errors.stream().map(this::toMessage).toList();
    }

    private String toMessage(CsvImportError error) {
        String fieldName = error.field() != null ? webMessageUtil.getMessageFor(error.field().getMsgKey()) : "";
        String message = webMessageUtil.getMessageFor(error.msgKey(), fieldName, error.value() != null ? error.value() : "");
        if (error.lineNumber() == null) {
            return message;
        }
        return webMessageUtil.getMessageFor("csvimport.error.line", String.valueOf(error.lineNumber()), message);
    }
}
