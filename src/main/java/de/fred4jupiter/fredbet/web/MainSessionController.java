package de.fred4jupiter.fredbet.web;

import java.util.List;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;

import de.fred4jupiter.fredbet.domain.Group;

@Controller
@Scope("session")
public class MainSessionController {

	@ModelAttribute("mainGroups")
	public List<Group> availableGroups() {
		return Group.getMainGroups();
	}
}
