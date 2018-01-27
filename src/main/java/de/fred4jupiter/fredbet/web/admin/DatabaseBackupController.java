package de.fred4jupiter.fredbet.web.admin;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import de.fred4jupiter.fredbet.domain.DatabaseBackup;
import de.fred4jupiter.fredbet.security.FredBetPermission;
import de.fred4jupiter.fredbet.service.admin.DatabaseBackupCreationException;
import de.fred4jupiter.fredbet.service.admin.DatabaseBackupCreationException.ErrorCode;
import de.fred4jupiter.fredbet.service.admin.DatabaseBackupService;
import de.fred4jupiter.fredbet.web.WebMessageUtil;

@Controller
@RequestMapping("/backup")
@PreAuthorize("hasAuthority('" + FredBetPermission.PERM_ADMINISTRATION + "')")
public class DatabaseBackupController {

	private static final Logger LOG = LoggerFactory.getLogger(DatabaseBackupController.class);

	@Autowired
	private WebMessageUtil webMessageUtil;

	@Autowired
	private DatabaseBackupService databaseBackupService;

	@ModelAttribute("databaseBackupCommand")
	public DatabaseBackupCommand initBackupCommand() {
		return new DatabaseBackupCommand();
	}

	@RequestMapping(value = "/show", method = RequestMethod.GET)
	public ModelAndView showPage(DatabaseBackupCommand databaseBackupCommand) {
		DatabaseBackup databaseBackup = databaseBackupService.loadDatabaseBackup();
		databaseBackupCommand.setBackupFolder(databaseBackup.getDatabaseBackupFolder());
		return new ModelAndView("admin/backup", "databaseBackupCommand", databaseBackupCommand);
	}

	@RequestMapping(value = "/execute", method = RequestMethod.GET)
	public ModelAndView executeBackup(RedirectAttributes redirect) {
		try {
			String pathFilename = databaseBackupService.executeBackup();
			webMessageUtil.addInfoMsg(redirect, "administration.msg.info.databaseBackupCreated", pathFilename);
		} catch (DatabaseBackupCreationException e) {
			LOG.error(e.getMessage());
			ErrorCode errorCode = e.getErrorCode();
			if (ErrorCode.UNSUPPORTED_DATABASE_TYPE.equals(errorCode)) {
				webMessageUtil.addErrorMsg(redirect, "administration.msg.backup.error.unsupportedDatabase");	
			}
			else if (ErrorCode.IN_MEMORY_NOT_SUPPORTED.equals(errorCode)) {
				webMessageUtil.addErrorMsg(redirect, "administration.msg.backup.error.noInMemory");
			}
			else {
				webMessageUtil.addErrorMsg(redirect, "administration.msg.backup.error.unknown", e.getMessage());
			}
		}

		return new ModelAndView("redirect:/backup/show");
	}

	@RequestMapping(value = "/save", method = RequestMethod.POST)
	public ModelAndView saveBackupSettings(@Valid DatabaseBackupCommand databaseBackupCommand, RedirectAttributes redirect) {
		databaseBackupService.saveBackupFolder(databaseBackupCommand.getBackupFolder());
		webMessageUtil.addInfoMsg(redirect, "administration.msg.info.databaseBackupFolderSaved", databaseBackupCommand.getBackupFolder());
		return new ModelAndView("redirect:/backup/show");
	}
}
