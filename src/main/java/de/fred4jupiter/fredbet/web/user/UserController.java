package de.fred4jupiter.fredbet.web.user;

import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import de.fred4jupiter.fredbet.domain.AppUser;
import de.fred4jupiter.fredbet.service.UserService;
import de.fred4jupiter.fredbet.web.MessageUtil;

@Controller
@RequestMapping("/user")
public class UserController {

	@Autowired
	private UserService userService;
	
	@Autowired
	private MessageUtil messageUtil;
	
	@RequestMapping
	public ModelAndView list() {
		List<AppUser> users = userService.findAll();
		return new ModelAndView("user/list", "allUsers", users);
	}

	@RequestMapping("{id}")
	public ModelAndView edit(@PathVariable("id") String userId) {
		UserCommand userCommand = userService.findByUserId(userId);
		return new ModelAndView("user/form", "userCommand", userCommand);
	}
	
	@RequestMapping("{id}/delete")
	public ModelAndView delete(@PathVariable("id") String userId, RedirectAttributes redirect) {
		UserCommand userCommand = userService.findByUserId(userId);
		userService.deleteUser(userId);
		String msg = "Benutzer " + userCommand.getUsername() + " wurde gelöscht!";
		messageUtil.addInfoMsg(redirect, msg);
		return new ModelAndView("redirect:/user");
	}

	@RequestMapping(value = "/create", method = RequestMethod.GET)
	public String createForm(@ModelAttribute UserCommand UserCommand) {
		return "user/form";
	}

	@RequestMapping(method = RequestMethod.POST)
	public ModelAndView createOrUpdate(@Valid UserCommand userCommand, BindingResult result, RedirectAttributes redirect) {
		if (result.hasErrors()) {
			return new ModelAndView("user/form", "formErrors", result.getAllErrors());
		}

		userService.save(userCommand);

		String msg = "Benutzer " + userCommand.getUsername() + " angelegt/aktualisiert!";
		messageUtil.addInfoMsg(redirect, msg);
		return new ModelAndView("redirect:/user");
	}
}
