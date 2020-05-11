package de.fred4jupiter.fredbet.web.info.pointcourse;

import de.fred4jupiter.fredbet.util.JsonObjectConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/pointcourse")
public class PointCourseController {

    @GetMapping("/show")
    public String showPage(Model model) {
        BarChartData barChartData = new BarChartData("4 points", "3 points", "2 points", "1 points");
        barChartData.addDataSet("admin", 2, 4, 3, 12);
        barChartData.addDataSet("michael", 0, 1, 8, 9);
        barChartData.addDataSet("test", 0, 3, 3, 13);

        model.addAttribute("barChartData", barChartData);
        return "info/pointcourse";
    }
}
