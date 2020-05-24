package de.fred4jupiter.fredbet.web.info.pointcourse;

import de.fred4jupiter.fredbet.service.excel.PointCourseContainer;
import de.fred4jupiter.fredbet.service.excel.ReportService;
import de.fred4jupiter.fredbet.web.WebMessageUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/pointcourse")
public class PointCourseController {

    @Autowired
    private ReportService reportService;

    @Autowired
    private WebMessageUtil webMessageUtil;

    @GetMapping("/show")
    public String showPage(Model model) {
        PointCourseContainer pointCourseContainer = reportService.reportPointsCourse(LocaleContextHolder.getLocale());

        if (pointCourseContainer.isEmpty()) {
            webMessageUtil.addInfoMsg(model, "pointsfrequency.noData");
            return "info/pointcourse";
        }

        ChartData chartData = new ChartData(pointCourseContainer.getLabels());
        pointCourseContainer.iteratePointsPerUser(chartData::addDataSet);

        model.addAttribute("chartData", chartData);
        return "info/pointcourse";
    }
}
