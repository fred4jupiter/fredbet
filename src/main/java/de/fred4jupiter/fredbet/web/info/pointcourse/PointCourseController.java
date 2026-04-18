package de.fred4jupiter.fredbet.web.info.pointcourse;

import de.fred4jupiter.fredbet.pointcourse.ChartData;
import de.fred4jupiter.fredbet.pointcourse.PointCourseService;
import de.fred4jupiter.fredbet.security.SecurityService;
import de.fred4jupiter.fredbet.web.WebMessageUtil;
import org.jspecify.annotations.NonNull;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        final ChartData chartData = pointCourseService.reportPointsCourse(securityService.getCurrentUserName(), LocaleContextHolder.getLocale());

        if (chartData.isEmpty()) {
            webMessageUtil.addInfoMsg(model, "pointsfrequency.noData");
            return "info/pointcourse";
        }

        // Convert ChartData (POJO + record DataSet) to plain Maps/Lists so Thymeleaf JS inlining
        // produces valid JavaScript literals: { labels: [...], datasets: [{label:..., data:[...]}] }
        Map<String, Object> chartDataMap = new HashMap<>();
        chartDataMap.put("labels", chartData.getLabels());
        chartDataMap.put("datasets", convertToDataSets(chartData));

        model.addAttribute("chartData", chartDataMap);
        return "info/pointcourse";
    }

    private @NonNull List<Map<String, Object>> convertToDataSets(ChartData chartData) {
        return chartData.getDatasets().stream()
            .map(ds -> {
                Map<String, Object> m = new HashMap<>();
                m.put("label", ds.label());
                m.put("data", ds.data());
                return m;
            }).toList();
    }
}
