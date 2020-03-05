package de.fred4jupiter.fredbet.web.info;

import de.fred4jupiter.fredbet.service.grouppoints.GroupPointsContainer;
import de.fred4jupiter.fredbet.service.grouppoints.GroupPointsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/grouptable")
public class GroupTableController {

    private static final String PAGE_GROUP_TABLE = "info/group_table";


    @Autowired
    private GroupPointsService groupPointsService;

    @GetMapping
    public String show(Model model) {
        GroupPointsContainer groupPointsContainer = groupPointsService.calculateGroupTablePoints(LocaleContextHolder.getLocale());
        model.addAttribute("groupPointsContainer", groupPointsContainer);
        return PAGE_GROUP_TABLE;
    }
}
