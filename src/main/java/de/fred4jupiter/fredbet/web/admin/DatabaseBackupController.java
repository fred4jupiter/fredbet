package de.fred4jupiter.fredbet.web.admin;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import de.fred4jupiter.fredbet.security.FredBetPermission;
import de.fred4jupiter.fredbet.web.WebMessageUtil;

@Controller
@RequestMapping("/backup")
@PreAuthorize("hasAuthority('" + FredBetPermission.PERM_ADMINISTRATION + "')")
public class DatabaseBackupController {

	@Autowired
	private WebMessageUtil webMessageUtil;
	
	@ModelAttribute("backupCommand")
	public BackupCommand initBackupCommand() {
		return new BackupCommand();
	}
	
	@RequestMapping(value = "/show", method = RequestMethod.GET)
	public ModelAndView showPage(BackupCommand backupCommand) {
		return new ModelAndView("admin/backup", "backupCommand", backupCommand);
	}
	
	@RequestMapping(value = "/save", method = RequestMethod.POST)
	public ModelAndView saveBackupSettings(@Valid BackupCommand backupCommand, RedirectAttributes redirect, ModelMap modelMap) {

		return new ModelAndView("redirect:/backup/show");
	}
}
