package de.fred4jupiter.fredbet.web.info;

import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import de.fred4jupiter.fredbet.domain.Info;
import de.fred4jupiter.fredbet.service.InfoService;

@Controller
@RequestMapping("/info")
public class InfoController {

	private static final String INFO_CONTEXT_RULES = "rules";

	private static final Logger LOG = LoggerFactory.getLogger(InfoController.class);

	@Autowired
	private InfoService infoService;

	@RequestMapping("/rules")
	public ModelAndView showRules() {
		Locale locale = LocaleContextHolder.getLocale();
		Info info = infoService.findBy(INFO_CONTEXT_RULES, locale);
		if (info == null) {
			info = infoService.saveInfoContent(INFO_CONTEXT_RULES, "", locale);
		}

		ModelAndView modelAndView = new ModelAndView("info/rules");
		modelAndView.addObject("textContent", info.getContent());
		return modelAndView;
	}

	@RequestMapping("/prices")
	public ModelAndView showPrices() {
		return new ModelAndView("info/prices");
	}

	@RequestMapping("/editInfo")
	public ModelAndView editInfo(@RequestParam("name") String name) {
		Locale locale = LocaleContextHolder.getLocale();
		Info info = infoService.findBy(name, locale);
		if (info == null) {
			info = infoService.saveInfoContent(name, "", locale);
		}

		ModelAndView modelAndView = new ModelAndView("/info/edit_info");
		InfoCommand infoCommand = new InfoCommand();
		infoCommand.setName(info.getPk().getName());
		infoCommand.setTextContent(info.getContent());
		modelAndView.addObject("infoCommand", infoCommand);
		return modelAndView;
	}

	@RequestMapping(value = "/editInfo", method = RequestMethod.POST)
	public ModelAndView saveEditedInfo(InfoCommand infoCommand) {
		LOG.debug("textContent: {}", infoCommand.getTextContent());

		Locale locale = LocaleContextHolder.getLocale();
		infoService.saveInfoContent(infoCommand.getName(), infoCommand.getTextContent(), locale);

		ModelAndView modelAndView = new ModelAndView("/info/edit_info");
		modelAndView.addObject("infoCommand", infoCommand);

		return modelAndView;
	}

}
