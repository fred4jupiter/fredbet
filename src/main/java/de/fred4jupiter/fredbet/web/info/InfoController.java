package de.fred4jupiter.fredbet.web.info;

import java.util.List;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import de.fred4jupiter.fredbet.domain.Info;
import de.fred4jupiter.fredbet.domain.Statistic;
import de.fred4jupiter.fredbet.security.FredBetPermission;
import de.fred4jupiter.fredbet.service.InfoService;
import de.fred4jupiter.fredbet.service.StatisticService;
import de.fred4jupiter.fredbet.web.MessageUtil;

@Controller
@RequestMapping("/info")
public class InfoController {

	private static final String TEXT_CONTENT = "textContent";

	private static final String PAGE_EDIT_INFO = "info/edit_info";

	@Autowired
	private MessageUtil messageUtil;

	@Autowired
	private InfoService infoService;

	@Autowired
	private StatisticService statisticService;

	@RequestMapping("/rules")
	public ModelAndView showRules() {
		final String content = loadContentFor(InfoType.RULES);

		ModelAndView modelAndView = new ModelAndView("info/rules");
		modelAndView.addObject(TEXT_CONTENT, content);
		return modelAndView;
	}

	private String loadContentFor(InfoType infoType) {
		Locale locale = LocaleContextHolder.getLocale();
		Info info = infoService.findBy(infoType, locale);
		return info.getContent();
	}

	@RequestMapping("/prices")
	public ModelAndView showPrices() {
		final String content = loadContentFor(InfoType.PRICES);

		ModelAndView modelAndView = new ModelAndView("info/prices");
		modelAndView.addObject(TEXT_CONTENT, content);
		return modelAndView;
	}

	@RequestMapping("/misc")
	public ModelAndView showMiscellaneous() {
		final String content = loadContentFor(InfoType.MISC);

		ModelAndView modelAndView = new ModelAndView("info/misc");
		modelAndView.addObject(TEXT_CONTENT, content);
		return modelAndView;
	}

	@PreAuthorize("hasAuthority('" + FredBetPermission.PERM_EDIT_INFOS + "')")
	@RequestMapping("/editinfo/{name}")
	public ModelAndView editInfo(@PathVariable("name") String name) {
		Info info = infoService.findBy(InfoType.valueOf(name.toUpperCase()), LocaleContextHolder.getLocale());

		ModelAndView modelAndView = new ModelAndView(PAGE_EDIT_INFO);
		InfoCommand infoCommand = new InfoCommand();
		infoCommand.setName(info.getPk().getName());
		infoCommand.setTextContent(info.getContent());
		modelAndView.addObject("infoCommand", infoCommand);
		return modelAndView;
	}

	@PreAuthorize("hasAuthority('" + FredBetPermission.PERM_EDIT_INFOS + "')")
	@RequestMapping(value = "/editinfo", method = RequestMethod.POST)
	public ModelAndView saveEditedInfo(InfoCommand infoCommand, ModelMap modelMap) {
		infoService.saveInfoContent(InfoType.valueOf(infoCommand.getName().toUpperCase()), infoCommand.getTextContent(), LocaleContextHolder.getLocale());

		ModelAndView modelAndView = new ModelAndView(PAGE_EDIT_INFO);
		modelAndView.addObject("infoCommand", infoCommand);
		messageUtil.addInfoMsg(modelMap, "info.msg.edit.saved");

		return modelAndView;
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
}
