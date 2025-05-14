package de.fred4jupiter.fredbet.web.info;

import de.fred4jupiter.fredbet.domain.entity.Info;
import de.fred4jupiter.fredbet.info.InfoService;
import de.fred4jupiter.fredbet.pdf.PdfExportService;
import de.fred4jupiter.fredbet.security.FredBetPermission;
import de.fred4jupiter.fredbet.security.SecurityService;
import de.fred4jupiter.fredbet.statistic.Statistic;
import de.fred4jupiter.fredbet.statistic.StatisticService;
import de.fred4jupiter.fredbet.util.MessageSourceUtil;
import de.fred4jupiter.fredbet.util.ResponseEntityUtil;
import de.fred4jupiter.fredbet.web.WebMessageUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

@Controller
@RequestMapping("/info")
public class InfoController {

    private static final String TEXT_CONTENT = "textContent";

    private static final String PAGE_EDIT_INFO = "info/edit_info";

    public static final String PAGE_INFO_STATISTIC = "info/statistic";

    private static final String CONTENT_TYPE_PDF = "application/pdf";

    private final WebMessageUtil webMessageUtil;

    private final InfoService infoService;

    private final StatisticService statisticService;

    private final SecurityService securityService;

    private final PdfExportService pdfExportService;

    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");

    private final MessageSourceUtil messageSourceUtil;

    public InfoController(WebMessageUtil webMessageUtil, InfoService infoService, StatisticService statisticService,
                          SecurityService securityService, PdfExportService pdfExportService, MessageSourceUtil messageSourceUtil) {
        this.webMessageUtil = webMessageUtil;
        this.infoService = infoService;
        this.statisticService = statisticService;
        this.securityService = securityService;
        this.pdfExportService = pdfExportService;
        this.messageSourceUtil = messageSourceUtil;
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

    @GetMapping(value = "/pdf", produces = CONTENT_TYPE_PDF)
    public ResponseEntity<byte[]> exportToPdf(@RequestParam("type") String infoTypeString) {
        final InfoType infoType = InfoType.valueOf(infoTypeString.toUpperCase());
        final String content = loadContentFor(infoType);

        if (StringUtils.isBlank(content)) {
            return ResponseEntity.notFound().build();
        }

        final String finalContent = createPdfContent(infoType, content);
        byte[] fileContent = this.pdfExportService.createPdfFromHtml(finalContent);
        if (fileContent == null) {
            return ResponseEntity.notFound().build();
        }

        final String fileName = createFileNameFor(infoType);
        return ResponseEntityUtil.createResponseEntity(fileName, fileContent, CONTENT_TYPE_PDF, ResponseEntityUtil.DownloadType.ATTACHMENT);
    }

    private String createPdfContent(InfoType infoType, String content) {
        String title = messageSourceUtil.getMessageFor(infoType.getMsgKey());
        return "<h1>%s</h1>".formatted(title) + content;
    }

    private String createFileNameFor(InfoType infoType) {
        return "%s_export".formatted(infoType.name().toLowerCase());
    }

    private ModelAndView prepareModelAndViewFor(InfoType infoType) {
        final String content = loadContentFor(infoType);
        ModelAndView modelAndView = new ModelAndView(infoType.getPageName());
        modelAndView.addObject(TEXT_CONTENT, prepareForImages(content));
        modelAndView.addObject("showPdfExportButton", !isEmpty(content));
        return modelAndView;
    }

    private boolean isEmpty(String content) {
        if (StringUtils.isBlank(content)) {
            return true;
        }

        return "<p><br></p>".equals(content);
    }

    private String prepareForImages(String content) {
        if (StringUtils.isNotBlank(content) && content.contains("<img ")) {
            return StringUtils.replace(content, "<img ", "<img class=\"img-responsive\" ");
        }
        return content;
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
