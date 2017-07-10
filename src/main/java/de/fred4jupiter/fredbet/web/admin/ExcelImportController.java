package de.fred4jupiter.fredbet.web.admin;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

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
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import de.fred4jupiter.fredbet.security.FredBetPermission;
import de.fred4jupiter.fredbet.service.excel.ExcelImportService;
import de.fred4jupiter.fredbet.service.excel.ExcelReadingException;
import de.fred4jupiter.fredbet.web.MessageUtil;

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
	private MessageUtil messageUtil;

	@Value("classpath:/excelimport/ImportTemplate.xlsx")
	private Resource excelTemplateFile;

	@Value("classpath:/excelimport/ConfederationsCup2017.xlsx")
	private Resource confCup2017File;

	@ModelAttribute("excelUploadCommand")
	public ExcelUploadCommand initExcelUploadCommand() {
		return new ExcelUploadCommand();
	}

	@RequestMapping(value = "/show", method = RequestMethod.GET)
	public ModelAndView showUploadPage() {
		return new ModelAndView("admin/excel_import");
	}

	@RequestMapping(value = "/download/template", method = RequestMethod.GET, produces = CONTENT_TYPE_EXCEL)
	public ResponseEntity<byte[]> downloadTemplate(HttpServletResponse response) {
		final String fileName = "ImportTemplate.xlsx";
		byte[] templateFile = downloadTemplate();

		return createResponseEntityFor(templateFile, fileName);
	}

	@RequestMapping(value = "/download/confcup2017", method = RequestMethod.GET, produces = CONTENT_TYPE_EXCEL)
	public ResponseEntity<byte[]> downloadConferderationsCup2017(HttpServletResponse response) {
		final String fileName = "ConfederationsCup2017.xlsx";
		byte[] fileContent = downloadConfCup2017();

		return createResponseEntityFor(fileContent, fileName);
	}

	private ResponseEntity<byte[]> createResponseEntityFor(byte[] fileContent, String fileName) {
		if (fileContent == null) {
			return new ResponseEntity<byte[]>(HttpStatus.NOT_FOUND);
		}

		return ResponseEntity.ok().header("Content-Type", CONTENT_TYPE_EXCEL)
				.header("Content-Disposition", "inline; filename=\"" + fileName + "\"").body(fileContent);
	}

	@RequestMapping(value = "/upload", method = RequestMethod.POST)
	public ModelAndView uploadExcelFile(ExcelUploadCommand excelUploadCommand, BindingResult result, RedirectAttributes redirect) {
		try {
			MultipartFile myFile = excelUploadCommand.getMyFile();
			if (myFile == null || myFile.getBytes() == null || myFile.getBytes().length == 0) {
				messageUtil.addErrorMsg(redirect, "excel.upload.msg.noFileGiven");
				return new ModelAndView(REDIRECT_SHOW_PAGE);
			}

			if (!myFile.getContentType().equals(CONTENT_TYPE_EXCEL)) {
				messageUtil.addErrorMsg(redirect, "excel.upload.msg.noExcelFile");
				return new ModelAndView(REDIRECT_SHOW_PAGE);
			}

			excelImportService.importMatchesFromExcelAndSave(myFile.getBytes());

			messageUtil.addInfoMsg(redirect, "excel.upload.msg.saved");
		} catch (IOException | ExcelReadingException e) {
			LOG.error(e.getMessage(), e);
			messageUtil.addErrorMsg(redirect, "excel.upload.msg.failed", e.getMessage());
		}

		return new ModelAndView(REDIRECT_SHOW_PAGE);
	}

	private byte[] downloadTemplate() {
		try {
			return IOUtils.toByteArray(excelTemplateFile.getInputStream());
		} catch (IOException e) {
			throw new IllegalStateException(e.getMessage(), e);
		}
	}

	private byte[] downloadConfCup2017() {
		try {
			return IOUtils.toByteArray(confCup2017File.getInputStream());
		} catch (IOException e) {
			throw new IllegalStateException(e.getMessage(), e);
		}
	}
}
