package de.fred4jupiter.fredbet.web.imexport;

import de.fred4jupiter.fredbet.imexport.JsonExportService;
import de.fred4jupiter.fredbet.imexport.JsonImportService;
import de.fred4jupiter.fredbet.security.FredBetPermission;
import de.fred4jupiter.fredbet.excel.ExcelReadingException;
import de.fred4jupiter.fredbet.util.ResponseEntityUtil;
import de.fred4jupiter.fredbet.web.WebMessageUtil;
import de.fred4jupiter.fredbet.web.user.UserImportExportCommand;
import jakarta.validation.Valid;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Controller
@RequestMapping("/importexport")
@PreAuthorize("hasAuthority('" + FredBetPermission.PERM_ADMINISTRATION + "')")
public class ImportExportController {

    private static final Logger LOG = LoggerFactory.getLogger(ImportExportController.class);

    private static final String CONTENT_TYPE_JSON = "application/json";

    private final JsonExportService jsonExportService;

    private final JsonImportService jsonImportService;

    private final WebMessageUtil messageUtil;

    private static final String REDIRECT_SHOW_PAGE = "redirect:/importexport";

    public ImportExportController(JsonExportService jsonExportService, JsonImportService jsonImportService, WebMessageUtil messageUtil) {
        this.jsonExportService = jsonExportService;
        this.messageUtil = messageUtil;
        this.jsonImportService = jsonImportService;
    }

    @ModelAttribute("importCommand")
    public ImportCommand importCommand() {
        return new ImportCommand();
    }

    @ModelAttribute("exportCommand")
    public ExportCommand exportCommand() {
        return new ExportCommand();
    }

    @GetMapping
    public String showUploadPage() {
        return "imexport/import_export";
    }

    @PostMapping(value = "/export", produces = CONTENT_TYPE_JSON)
    public ResponseEntity<byte[]> exportUsers(@Valid ExportCommand exportCommand) {
        final String fileName = "fredbet_all_data.json";
        String json = this.jsonExportService.exportAllToJson(exportCommand.getUsersOnly());
        if (StringUtils.isBlank(json)) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntityUtil.createResponseEntity(fileName, json.getBytes(StandardCharsets.UTF_8), CONTENT_TYPE_JSON);
    }

    @PostMapping("/import")
    public String uploadJsonFile(UserImportExportCommand command, RedirectAttributes redirect) {
        try {
            MultipartFile myFile = command.getJsonFile();
            if (myFile == null || myFile.getBytes().length == 0) {
                messageUtil.addErrorMsg(redirect, "importexport.upload.msg.noFileGiven");
                return REDIRECT_SHOW_PAGE;
            }

            if (!CONTENT_TYPE_JSON.equals(myFile.getContentType())) {
                messageUtil.addErrorMsg(redirect, "importexport.upload.msg.noJsonFile");
                return REDIRECT_SHOW_PAGE;
            }

            jsonImportService.importAllFromJson(new String(myFile.getBytes(), StandardCharsets.UTF_8));

            messageUtil.addInfoMsg(redirect, "importexport.upload.msg.saved");
        } catch (IOException | ExcelReadingException e) {
            LOG.error(e.getMessage(), e);
            messageUtil.addErrorMsg(redirect, "importexport.upload.msg.failed", e.getMessage());
        }

        return REDIRECT_SHOW_PAGE;
    }
}
