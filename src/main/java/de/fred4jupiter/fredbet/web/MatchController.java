package de.fred4jupiter.fredbet.web;

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

import de.fred4jupiter.fredbet.domain.Match;
import de.fred4jupiter.fredbet.repository.MatchRepository;

@Controller
@RequestMapping("/matches")
public class MatchController {

	@Autowired
	private MatchRepository matchRepository;

	@RequestMapping
	public ModelAndView list() {
		List<Match> matches = matchRepository.findAll();
		return new ModelAndView("matches/list", "allMatches", matches);
	}

	@RequestMapping("{id}")
	public ModelAndView view(@PathVariable("id") Match match) {
		return new ModelAndView("matches/view", "match", match);
	}

	@RequestMapping(params = "form", method = RequestMethod.GET)
	public String createForm(@ModelAttribute Match match) {
		return "matches/form";
	}

	@RequestMapping(method = RequestMethod.POST)
	public ModelAndView create(@Valid Match match, BindingResult result, RedirectAttributes redirect) {
		if (result.hasErrors()) {
			return new ModelAndView("matches/form", "formErrors", result.getAllErrors());
		}
		match = this.matchRepository.save(match);
		redirect.addFlashAttribute("globalMessage", "Successfully created a new message");
		return new ModelAndView("redirect:/matches/{match.id}", "match.id", match.getId());
	}
}
