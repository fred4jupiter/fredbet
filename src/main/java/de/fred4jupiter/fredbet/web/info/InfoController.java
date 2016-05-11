package de.fred4jupiter.fredbet.web.info;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/info")
public class InfoController {

	private static final Logger LOG = LoggerFactory.getLogger(InfoController.class);

	@RequestMapping("/rules")
	public ModelAndView showRules() {
		return new ModelAndView("info/rules");
	}

	@RequestMapping("/prices")
	public ModelAndView showPrices() {
		return new ModelAndView("info/prices");
	}

	@RequestMapping("/editInfo")
	public ModelAndView editInfo() {
		ModelAndView modelAndView = new ModelAndView("/info/edit_info");
		InfoCommand infoCommand = new InfoCommand();
		infoCommand.setTextContent("<p>Hello</p>");
		modelAndView.addObject("infoCommand", infoCommand);
		return modelAndView;
	}

	@RequestMapping(value = "/editInfo", method = RequestMethod.POST)
	public ModelAndView saveEditedInfo(InfoCommand infoCommand) {
		LOG.debug("textContent: {}", infoCommand.getTextContent());

		ModelAndView modelAndView = new ModelAndView("/info/edit_info");
		modelAndView.addObject("infoCommand", infoCommand);
		
		return modelAndView;
	}

}
