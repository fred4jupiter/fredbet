package de.fred4jupiter.fredbet.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/")
public class HomeController {

	@RequestMapping
	public ModelAndView createOrUpdate(RedirectAttributes redirect) {
		return new ModelAndView("redirect:/matches");
	}

}
