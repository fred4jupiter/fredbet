package de.fred4jupiter.fredbet.web.info.pointcourse;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/pointcourse")
public class PointCourseController {

    @GetMapping("/show")
    public String showPage(Model model) {
        return "info/pointcourse";
    }
}
