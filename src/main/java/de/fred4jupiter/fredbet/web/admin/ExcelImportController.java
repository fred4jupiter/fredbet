package de.fred4jupiter.fredbet.web.admin;

import de.fred4jupiter.fredbet.security.FredBetPermission;
import de.fred4jupiter.fredbet.service.excel.ExcelImportService;
import de.fred4jupiter.fredbet.service.excel.ExcelReadingException;
import de.fred4jupiter.fredbet.web.WebMessageUtil;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Controller
@RequestMapping("/excelimport")
@PreAuthorize("hasAuthority('" + FredBetPermission.PERM_ADMINISTRATION + "')")
public class ExcelImportController {

    private static final String CONTENT_TYPE_EXCEL = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";

    private static final Logger LOG = LoggerFactory.getLogger(ExcelImportController.class);

    private static final String REDIRECT_SHOW_PAGE = "redirect:/excelimport/show";

    @Autowired
    private ExcelImportService excelImportService;

    @Autowired
    private WebMessageUtil messageUtil;

    @Value("classpath:/excelimport/ImportTemplate.xlsx")
    private Resource excelTemplateFile;

    @ModelAttribute("excelUploadCommand")
    public ExcelUploadCommand initExcelUploadCommand() {
        return new ExcelUploadCommand();
    }

    @GetMapping("/show")
    public String showUploadPage() {
        return "admin/excel_import";
    }

    @RequestMapping(value = "/download/template", method = RequestMethod.GET, produces = CONTENT_TYPE_EXCEL)
    public ResponseEntity<byte[]> downloadTemplate() {
        final String fileName = "ImportTemplate.xlsx";
        byte[] templateFile = downloadResource(excelTemplateFile);

        return createResponseEntityFor(templateFile, fileName);
    }

    private ResponseEntity<byte[]> createResponseEntityFor(byte[] fileContent, String fileName) {
        if (fileContent == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok().header("Content-Type", CONTENT_TYPE_EXCEL)
                .header("Content-Disposition", "inline; filename=\"" + fileName + "\"").body(fileContent);
    }

    @RequestMapping(value = "/upload", method = RequestMethod.POST)
    public String uploadExcelFile(ExcelUploadCommand excelUploadCommand, BindingResult result, RedirectAttributes redirect) {
        try {
            MultipartFile myFile = excelUploadCommand.getMyFile();
            if (myFile == null || myFile.getBytes() == null || myFile.getBytes().length == 0) {
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
