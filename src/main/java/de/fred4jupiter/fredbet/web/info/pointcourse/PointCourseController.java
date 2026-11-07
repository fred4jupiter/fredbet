package de.fred4jupiter.fredbet.web.info.pointcourse;

import de.fred4jupiter.fredbet.pointcourse.ChartData;
import de.fred4jupiter.fredbet.pointcourse.PointCourseService;
import de.fred4jupiter.fredbet.security.SecurityService;
import de.fred4jupiter.fredbet.util.JsonObjectConverter;
import de.fred4jupiter.fredbet.web.WebMessageUtil;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Map;

// Punkteverlauf
@Controller
@RequestMapping("/pointcourse")
public class PointCourseController {

    private final PointCourseService pointCourseService;

    private final WebMessageUtil webMessageUtil;

    private final SecurityService securityService;

    private final JsonObjectConverter jsonObjectConverter;

    public PointCourseController(PointCourseService pointCourseService, WebMessageUtil webMessageUtil, SecurityService securityService,
                                 JsonObjectConverter jsonObjectConverter) {
        this.pointCourseService = pointCourseService;
        this.webMessageUtil = webMessageUtil;
        this.securityService = securityService;
        this.jsonObjectConverter = jsonObjectConverter;
    }

    @GetMapping("/show")
    public String showPage(Model model) {
        final ChartData chartData = pointCourseService.reportPointsCourse(securityService.getCurrentUserName(), LocaleContextHolder.getLocale());

        if (chartData.isEmpty()) {
            webMessageUtil.addInfoMsg(model, "pointcourse.noData");
            return "info/pointcourse";
        }

        // passed as JSON in a data attribute of the canvas, Thymeleaf HTML-escapes it there
        Map<String, Object> chartJson = Map.of("labels", chartData.getLabels(), "datasets", chartData.getDatasets());
        model.addAttribute("chartJson", jsonObjectConverter.toJson(chartJson));
        return "info/pointcourse";
    }
}
