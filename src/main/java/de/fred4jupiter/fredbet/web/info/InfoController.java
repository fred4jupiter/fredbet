package de.fred4jupiter.fredbet.web.info;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/info")
public class InfoController {

	@RequestMapping("/rules")
	public ModelAndView showRules() {
		return new ModelAndView("info/rules");
	}
}
