package de.fred4jupiter.fredbet.web.info.pointcourse;

import de.fred4jupiter.fredbet.service.excel.PointsFrequencyContainer;
import de.fred4jupiter.fredbet.service.excel.ReportService;
import de.fred4jupiter.fredbet.web.WebMessageUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/pointcourse")
public class PointCourseController {

    @Autowired
    private ReportService reportService;

    @Autowired
    private WebMessageUtil webMessageUtil;

    @GetMapping("/show")
    public String showPage(Model model) {
        PointsFrequencyContainer container = reportService.reportPointsFrequencyToContainer();

        if (container.isEmpty()) {
            webMessageUtil.addInfoMsg(model, "pointsfrequency.noData");
            return "info/pointcourse";
        }

        List<String> collected = container.getNumberOfPointsCountList().stream().map(point -> point + " Points").collect(Collectors.toList());
        BarChartData barChartData = new BarChartData(collected);

        container.iterateResult(barChartData::addDataSet);

        model.addAttribute("barChartData", barChartData);
        return "info/pointcourse";
    }
}
