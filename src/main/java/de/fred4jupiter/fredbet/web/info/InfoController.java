package de.fred4jupiter.fredbet.web.info;

import java.util.List;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import de.fred4jupiter.fredbet.domain.Info;
import de.fred4jupiter.fredbet.domain.Statistic;
import de.fred4jupiter.fredbet.security.FredBetPermission;
import de.fred4jupiter.fredbet.security.SecurityService;
import de.fred4jupiter.fredbet.service.InfoService;
import de.fred4jupiter.fredbet.service.StatisticService;
import de.fred4jupiter.fredbet.web.WebMessageUtil;

@Controller
@RequestMapping("/info")
public class InfoController {

	private static final String TEXT_CONTENT = "textContent";

	private static final String PAGE_EDIT_INFO = "info/edit_info";

	@Autowired
	private WebMessageUtil messageUtil;

	@Autowired
	private InfoService infoService;

	@Autowired
	private StatisticService statisticService;
	
	@Autowired
	private SecurityService securityService;

	@RequestMapping("/rules")
	public ModelAndView showRules() {
		return prepareModelAndViewFor(InfoType.RULES);
	}

	@RequestMapping("/prices")
	public ModelAndView showPrices() {
		return prepareModelAndViewFor(InfoType.PRICES);
	}

	@RequestMapping("/misc")
	public ModelAndView showMiscellaneous() {
		return prepareModelAndViewFor(InfoType.MISC);
	}

	private ModelAndView prepareModelAndViewFor(InfoType infoType) {
		final String content = loadContentFor(infoType);
		ModelAndView modelAndView = new ModelAndView(infoType.getPageName());
		modelAndView.addObject(TEXT_CONTENT, content);
		return modelAndView;
	}

	@PreAuthorize("hasAuthority('" + FredBetPermission.PERM_EDIT_INFOS_RULES + "')")
	@RequestMapping("/editinfo/rules")
	public ModelAndView editInfoRules() {
		return prepareShowEditInfoFor(InfoType.RULES);
	}

	@PreAuthorize("hasAuthority('" + FredBetPermission.PERM_EDIT_INFOS_PRICES + "')")
	@RequestMapping("/editinfo/prices")
	public ModelAndView editInfoPrices() {
		return prepareShowEditInfoFor(InfoType.PRICES);
	}

	@RequestMapping("/editinfo/misc")
	public ModelAndView editInfoMisc() {
		return prepareShowEditInfoFor(InfoType.MISC);
	}

	private ModelAndView prepareShowEditInfoFor(InfoType infoType) {
		Info info = infoService.findBy(infoType, LocaleContextHolder.getLocale());

		ModelAndView modelAndView = new ModelAndView(PAGE_EDIT_INFO);
		InfoCommand infoCommand = new InfoCommand();
		infoCommand.setName(info.getPk().getName());
		infoCommand.setTextContent(info.getContent());
		modelAndView.addObject("infoCommand", infoCommand);
		return modelAndView;
	}

	@RequestMapping(value = "/editinfo", method = RequestMethod.POST)
	public ModelAndView saveEditedInfoMisc(InfoCommand infoCommand, ModelMap modelMap) {
		final InfoType infoType = InfoType.valueOf(infoCommand.getName().toUpperCase());
		if (InfoType.MISC.equals(infoType)) {
			infoService.saveInfoContent(infoType, infoCommand.getTextContent(), LocaleContextHolder.getLocale());
			return new ModelAndView("redirect:/" + infoType.getPageName());
		}
		
		if (InfoType.RULES.equals(infoType) && !securityService.isCurrentUserHavingPermission(FredBetPermission.PERM_EDIT_INFOS_RULES)) {
			throw new AccessDeniedException("No enough privileges!");
		}
		else if (InfoType.PRICES.equals(infoType) && !securityService.isCurrentUserHavingPermission(FredBetPermission.PERM_EDIT_INFOS_PRICES)) {
			throw new AccessDeniedException("No enough privileges!");
		}

		infoService.saveInfoContent(infoType, infoCommand.getTextContent(), LocaleContextHolder.getLocale());
		return new ModelAndView("redirect:/" + infoType.getPageName());
	}

	@RequestMapping("/statistic")
	public ModelAndView showStatistics(ModelMap modelMap) {
		List<Statistic> statisticList = statisticService.createStatistic();

		ModelAndView modelAndView = new ModelAndView("info/statistic");
		modelAndView.addObject("statisticList", statisticList);
		if (statisticList.isEmpty()) {
			messageUtil.addInfoMsg(modelMap, "msg.warn.no.statistics");
		}

		return modelAndView;
	}

	private String loadContentFor(InfoType infoType) {
		Locale locale = LocaleContextHolder.getLocale();
		Info info = infoService.findBy(infoType, locale);
		return info.getContent();
	}
}
