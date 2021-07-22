package de.fred4jupiter.fredbet.web.imexport;

import de.fred4jupiter.fredbet.imexport.ImportExportService;
import de.fred4jupiter.fredbet.security.FredBetPermission;
import de.fred4jupiter.fredbet.service.excel.ExcelReadingException;
import de.fred4jupiter.fredbet.util.ResponseEntityUtil;
import de.fred4jupiter.fredbet.web.WebMessageUtil;
import de.fred4jupiter.fredbet.web.user.UserImportExportCommand;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
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

    private final ImportExportService importExportService;

    private final WebMessageUtil messageUtil;

    private static final String REDIRECT_SHOW_PAGE = "redirect:/importexport";

    public ImportExportController(ImportExportService importExportService, WebMessageUtil messageUtil) {
        this.importExportService = importExportService;
        this.messageUtil = messageUtil;
    }

    @ModelAttribute("importExportCommand")
    public ImportExportCommand initCommand() {
        return new ImportExportCommand();
    }

    @GetMapping
    public String showUploadPage() {
        return "imexport/import_export";
    }

    @GetMapping(value = "/export", produces = CONTENT_TYPE_JSON)
    public ResponseEntity<byte[]> exportUsers() {
        final String fileName = "fredbet_all_data.json";
        String json = this.importExportService.exportAllToJson();
        if (StringUtils.isBlank(json)) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntityUtil.createResponseEntity(fileName, json.getBytes(StandardCharsets.UTF_8), CONTENT_TYPE_JSON);
    }

    @RequestMapping(value = "/import", method = RequestMethod.POST)
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

            importExportService.importAllFromJson(new String(myFile.getBytes(), StandardCharsets.UTF_8));

            messageUtil.addInfoMsg(redirect, "importexport.upload.msg.saved");
        } catch (IOException | ExcelReadingException e) {
            LOG.error(e.getMessage(), e);
            messageUtil.addErrorMsg(redirect, "importexport.upload.msg.failed", e.getMessage());
        }

        return REDIRECT_SHOW_PAGE;
    }
}
