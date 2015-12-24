package de.fred4jupiter.fredbet.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import de.fred4jupiter.fredbet.data.DataBasePopulator;

@Controller
@RequestMapping("/administration")
public class AdminController {

	@Autowired
	private DataBasePopulator dataBasePopulator;
	
	@Autowired
	private MessageUtil messageUtil;

	@RequestMapping
	public String list() {
		return "admin/administration";
	}

	@RequestMapping(path = "/createDemoData", method = RequestMethod.GET)
	public ModelAndView createDemoData(ModelMap modelMap) {
		dataBasePopulator.createDemoData();
		
		ModelAndView modelAndView = new ModelAndView("admin/administration");
		
		messageUtil.addInfoMsg(modelMap, "administration.msg.info.demoDataCreated");
		return modelAndView;
	}
}
