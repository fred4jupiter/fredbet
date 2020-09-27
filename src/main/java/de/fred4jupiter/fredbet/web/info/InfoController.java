package de.fred4jupiter.fredbet.web.info;

import de.fred4jupiter.fredbet.domain.Info;
import de.fred4jupiter.fredbet.domain.Statistic;
import de.fred4jupiter.fredbet.security.FredBetPermission;
import de.fred4jupiter.fredbet.security.SecurityService;
import de.fred4jupiter.fredbet.service.InfoService;
import de.fred4jupiter.fredbet.service.StatisticService;
import de.fred4jupiter.fredbet.web.WebMessageUtil;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;
import java.util.Locale;

@Controller
@RequestMapping("/info")
public class InfoController {

    private static final String TEXT_CONTENT = "textContent";

    private static final String PAGE_EDIT_INFO = "info/edit_info";
    public static final String PAGE_INFO_STATISTIC = "info/statistic";

    private final WebMessageUtil webMessageUtil;

    private final InfoService infoService;

    private final StatisticService statisticService;

    private final SecurityService securityService;

    public InfoController(WebMessageUtil webMessageUtil, InfoService infoService, StatisticService statisticService,
                          SecurityService securityService) {
        this.webMessageUtil = webMessageUtil;
        this.infoService = infoService;
        this.statisticService = statisticService;
        this.securityService = securityService;
    }

    @GetMapping("/rules")
    public ModelAndView showRules() {
        return prepareModelAndViewFor(InfoType.RULES);
    }

    @GetMapping("/prices")
    public ModelAndView showPrices() {
        return prepareModelAndViewFor(InfoType.PRICES);
    }

    @GetMapping("/misc")
    public ModelAndView showMiscellaneous() {
        return prepareModelAndViewFor(InfoType.MISC);
    }

    private ModelAndView prepareModelAndViewFor(InfoType infoType) {
        final String content = loadContentFor(infoType);
        ModelAndView modelAndView = new ModelAndView(infoType.getPageName());
        modelAndView.addObject(TEXT_CONTENT, content);
        return modelAndView;
    }

    @PreAuthorize("hasAuthority('" + FredBetPermission.PERM_EDIT_INFOS_RULES + "')")
    @GetMapping("/editinfo/rules")
    public ModelAndView editInfoRules() {
        return prepareShowEditInfoFor(InfoType.RULES);
    }

    @PreAuthorize("hasAuthority('" + FredBetPermission.PERM_EDIT_INFOS_PRICES + "')")
    @GetMapping("/editinfo/prices")
    public ModelAndView editInfoPrices() {
        return prepareShowEditInfoFor(InfoType.PRICES);
    }

    @GetMapping("/editinfo/misc")
    public ModelAndView editInfoMisc() {
        return prepareShowEditInfoFor(InfoType.MISC);
    }

    private ModelAndView prepareShowEditInfoFor(InfoType infoType) {
        Info info = infoService.findBy(infoType, LocaleContextHolder.getLocale());

        ModelAndView modelAndView = new ModelAndView(PAGE_EDIT_INFO);
        InfoCommand infoCommand = new InfoCommand();
        infoCommand.setName(info.getPk().getName());
        infoCommand.setTextContent(info.getContent());
        modelAndView.addObject("infoCommand", infoCommand);
        return modelAndView;
    }

    @PostMapping(value = "/editinfo")
    public String saveEditedInfoMisc(InfoCommand infoCommand) {
        final InfoType infoType = InfoType.valueOf(infoCommand.getName().toUpperCase());
        if (InfoType.MISC.equals(infoType)) {
            infoService.saveInfoContent(infoType, infoCommand.getTextContent(), LocaleContextHolder.getLocale());
            return "redirect:/" + infoType.getPageName();
        }

        if (InfoType.RULES.equals(infoType) && !securityService.isCurrentUserHavingPermission(FredBetPermission.PERM_EDIT_INFOS_RULES)) {
            throw new AccessDeniedException("No enough privileges!");
        } else if (InfoType.PRICES.equals(infoType)
                && !securityService.isCurrentUserHavingPermission(FredBetPermission.PERM_EDIT_INFOS_PRICES)) {
            throw new AccessDeniedException("No enough privileges!");
        }

        infoService.saveInfoContent(infoType, infoCommand.getTextContent(), LocaleContextHolder.getLocale());
        return "redirect:/" + infoType.getPageName();
    }

    @GetMapping("/statistic")
    public String showStatistics(Model model) {
        final List<Statistic> statisticList = statisticService.createStatistic();

        model.addAttribute("favouriteCountry", statisticService.getFavouriteCountry());
        if (statisticList.isEmpty()) {
            webMessageUtil.addInfoMsg(model, "msg.warn.no.statistics");
            return PAGE_INFO_STATISTIC;
        }

        model.addAttribute("statisticList", statisticList);

        return PAGE_INFO_STATISTIC;
    }

    private String loadContentFor(InfoType infoType) {
        Locale locale = LocaleContextHolder.getLocale();
        Info info = infoService.findBy(infoType, locale);
        return info.getContent();
    }
}
