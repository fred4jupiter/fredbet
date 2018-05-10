package de.fred4jupiter.fredbet.web.info;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.collections4.MultiValuedMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import de.fred4jupiter.fredbet.repository.PointCountResult;
import de.fred4jupiter.fredbet.service.excel.ReportService;
import de.fred4jupiter.fredbet.web.WebMessageUtil;

@Controller
@RequestMapping("/info/pointsfrequency")
public class PointsFrequencyController {

	@Autowired
	private ReportService reportService;

	@Autowired
	private WebMessageUtil webMessageUtil;

	@GetMapping
	public ModelAndView show(ModelMap modelMap) {
		MultiValuedMap<Integer, PointCountResult> map = reportService.reportPointsFrequency();

		final ModelAndView modelAndView = new ModelAndView("info/points_freq");

		if (map.isEmpty()) {
			webMessageUtil.addInfoMsg(modelMap, "pointsfrequency.noData");
			return modelAndView;
		}

		List<Integer> collect = map.keySet().stream().collect(Collectors.toList());
		Collections.reverse(collect);

		PointsFrequencyCommand command = new PointsFrequencyCommand();

		for (Integer points : collect) {
			command.add(points, new ArrayList<>(map.get(points)));
		}

		modelAndView.addObject("pointsFrequencyCommand", command);
		return modelAndView;
	}

}
