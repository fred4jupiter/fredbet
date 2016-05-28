package de.fred4jupiter.fredbet.web.admin;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import de.fred4jupiter.fredbet.data.DataBasePopulator;
import de.fred4jupiter.fredbet.domain.AppUser;
import de.fred4jupiter.fredbet.security.FredBetPermission;
import de.fred4jupiter.fredbet.web.MessageUtil;

@Controller
@RequestMapping("/administration")
public class AdminController {

	@Autowired
	private DataBasePopulator dataBasePopulator;

	@Autowired
	private MessageUtil messageUtil;

	@Autowired
	private SessionRegistry sessionRegistry;

	@RequestMapping
	public String list() {
		return "admin/administration";
	}

	@PreAuthorize("hasAuthority('" + FredBetPermission.PERM_ADMINISTRATION + "')")
	@RequestMapping(path = "/createDemoMatches", method = RequestMethod.GET)
	public ModelAndView createDemoMatches(ModelMap modelMap) {
		dataBasePopulator.createEM2016Matches();

		ModelAndView modelAndView = new ModelAndView("admin/administration");

		messageUtil.addInfoMsg(modelMap, "administration.msg.info.demoMatchesCreated");
		return modelAndView;
	}

	@PreAuthorize("hasAuthority('" + FredBetPermission.PERM_ADMINISTRATION + "')")
	@RequestMapping(path = "/createDemoBets", method = RequestMethod.GET)
	public ModelAndView createDemoBets(ModelMap modelMap) {
		dataBasePopulator.createDemoBetsForAllUsers();

		ModelAndView modelAndView = new ModelAndView("admin/administration");

		messageUtil.addInfoMsg(modelMap, "administration.msg.info.demoBetsCreated");
		return modelAndView;
	}

	@PreAuthorize("hasAuthority('" + FredBetPermission.PERM_ADMINISTRATION + "')")
	@RequestMapping(path = "/createDemoResults", method = RequestMethod.GET)
	public ModelAndView createDemoResults(ModelMap modelMap) {
		dataBasePopulator.createDemoResultsForAllMatches();

		ModelAndView modelAndView = new ModelAndView("admin/administration");

		messageUtil.addInfoMsg(modelMap, "administration.msg.info.demoResultsCreated");
		return modelAndView;
	}

	@PreAuthorize("hasAuthority('" + FredBetPermission.PERM_SHOW_ACTIVE_USERS + "')")
	@RequestMapping(path = "/active/users", method = RequestMethod.GET)
	public ModelAndView showActiveUsers(ModelMap modelMap) {
		final List<Object> allPrincipals = sessionRegistry.getAllPrincipals();

		List<String> userNameList = allPrincipals.stream().filter(principal -> principal instanceof AppUser)
				.map(principal -> ((AppUser) principal).getUsername()).collect(Collectors.toList());

		ModelAndView modelAndView = new ModelAndView("admin/active_users");
		modelAndView.addObject("userList", userNameList);
		return modelAndView;
	}
}
