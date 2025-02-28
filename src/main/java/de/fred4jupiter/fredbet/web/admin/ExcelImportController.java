package de.fred4jupiter.fredbet.web.admin;

import de.fred4jupiter.fredbet.security.FredBetPermission;
import de.fred4jupiter.fredbet.excel.ExcelImportService;
import de.fred4jupiter.fredbet.excel.ExcelReadingException;
import de.fred4jupiter.fredbet.util.ResponseEntityUtil;
import de.fred4jupiter.fredbet.web.WebMessageUtil;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
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

@Controller
@RequestMapping("/excelimport")
@PreAuthorize("hasAuthority('" + FredBetPermission.PERM_ADMINISTRATION + "')")
public class ExcelImportController {

    private static final String CONTENT_TYPE_EXCEL = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";

    private static final Logger LOG = LoggerFactory.getLogger(ExcelImportController.class);

    private static final String REDIRECT_SHOW_PAGE = "redirect:/excelimport/show";

    private final ExcelImportService excelImportService;

    private final WebMessageUtil messageUtil;

    private final Resource excelTemplateFile;

    public ExcelImportController(ExcelImportService excelImportService, WebMessageUtil messageUtil,
                                 @Value("classpath:/excelimport/ImportTemplate.xlsx") Resource excelTemplateFile) {
        this.excelImportService = excelImportService;
        this.messageUtil = messageUtil;
        this.excelTemplateFile = excelTemplateFile;
    }

    @ModelAttribute("excelUploadCommand")
    public ExcelUploadCommand initExcelUploadCommand() {
        return new ExcelUploadCommand();
    }

    @GetMapping("/show")
    public String showUploadPage() {
        return "admin/excel_import";
    }

    @GetMapping(value = "/download/template", produces = CONTENT_TYPE_EXCEL)
    public ResponseEntity<byte[]> downloadTemplate() {
        final String fileName = "ImportTemplate.xlsx";
        byte[] templateFile = downloadResource(excelTemplateFile);

        return createResponseEntityFor(templateFile, fileName);
    }

    private ResponseEntity<byte[]> createResponseEntityFor(byte[] fileContent, String fileName) {
        if (fileContent == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntityUtil.createResponseEntity(fileName, fileContent, CONTENT_TYPE_EXCEL);
    }

    @PostMapping("/upload")
    public String uploadExcelFile(ExcelUploadCommand excelUploadCommand, RedirectAttributes redirect) {
        try {
            MultipartFile myFile = excelUploadCommand.getMyFile();
            if (myFile == null || myFile.getBytes().length == 0) {
                messageUtil.addErrorMsg(redirect, "excel.upload.msg.noFileGiven");
                return REDIRECT_SHOW_PAGE;
            }

            if (!CONTENT_TYPE_EXCEL.equals(myFile.getContentType())) {
                messageUtil.addErrorMsg(redirect, "excel.upload.msg.noExcelFile");
                return REDIRECT_SHOW_PAGE;
            }

            excelImportService.importMatchesFromExcelAndSave(myFile.getBytes());

            messageUtil.addInfoMsg(redirect, "excel.upload.msg.saved");
        } catch (IOException | ExcelReadingException e) {
            LOG.error(e.getMessage(), e);
            messageUtil.addErrorMsg(redirect, "excel.upload.msg.failed", e.getMessage());
        }

        return REDIRECT_SHOW_PAGE;
    }

    private byte[] downloadResource(Resource resource) {
        try {
            return IOUtils.toByteArray(resource.getInputStream());
        } catch (IOException e) {
            throw new IllegalStateException(e.getMessage(), e);
        }
    }
}
