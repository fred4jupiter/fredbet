package de.fred4jupiter.fredbet.web.points;

import de.fred4jupiter.fredbet.points.PointsConfigService;
import de.fred4jupiter.fredbet.points.PointsConfiguration;
import de.fred4jupiter.fredbet.web.WebMessageUtil;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/config/points")
public class PointsConfigController {

    private final WebMessageUtil messageUtil;

    private final PointsConfigService pointsConfigService;

    public PointsConfigController(WebMessageUtil messageUtil, PointsConfigService pointsConfigService) {
        this.messageUtil = messageUtil;
        this.pointsConfigService = pointsConfigService;
    }

    @GetMapping
    public String show(Model model) {
        PointsConfiguration pointsConfig = pointsConfigService.loadPointsConfig();
        model.addAttribute("pointsConfig", pointsConfig);
        return "config/points_config";
    }

    @GetMapping("/reset")
    public String resetToDefaults(Model model) {
        PointsConfiguration pointsConfig = pointsConfigService.createDefaultPointsConfig();
        model.addAttribute("pointsConfig", pointsConfig);
        return "config/points_config";
    }

    @PostMapping
    public String saveConfig(@Valid PointsConfiguration pointsConfig, BindingResult bindingResult, RedirectAttributes redirect) {
        if (bindingResult.hasErrors()) {
            return "config/points_config";
        }

        pointsConfigService.savePointsConfig(pointsConfig);

        messageUtil.addInfoMsg(redirect, "points.config.saved");
        return "redirect:/config/points";
    }
}
