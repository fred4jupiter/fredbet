package de.fred4jupiter.fredbet.web.info.pointcourse;

import de.fred4jupiter.fredbet.pointcourse.ChartData;
import de.fred4jupiter.fredbet.pointcourse.PointCourseService;
import de.fred4jupiter.fredbet.security.SecurityService;
import de.fred4jupiter.fredbet.web.WebMessageUtil;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

// Punkteverlauf
@Controller
@RequestMapping("/pointcourse")
public class PointCourseController {

    private final PointCourseService pointCourseService;

    private final WebMessageUtil webMessageUtil;

    private final SecurityService securityService;

    public PointCourseController(PointCourseService pointCourseService, WebMessageUtil webMessageUtil, SecurityService securityService) {
        this.pointCourseService = pointCourseService;
        this.webMessageUtil = webMessageUtil;
        this.securityService = securityService;
    }

    @GetMapping("/show")
    public String showPage(Model model) {
        ChartData chartData = pointCourseService.reportPointsCourse(securityService.getCurrentUserName(), LocaleContextHolder.getLocale());

        if (chartData.isEmpty()) {
            webMessageUtil.addInfoMsg(model, "pointsfrequency.noData");
            return "info/pointcourse";
        }

        model.addAttribute("chartData", chartData);
        return "info/pointcourse";
    }
}
