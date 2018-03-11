package de.fred4jupiter.fredbet.web.info;

import org.apache.commons.collections4.MultiValuedMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import de.fred4jupiter.fredbet.repository.PointCountResult;
import de.fred4jupiter.fredbet.service.excel.ReportService;

@Controller
@RequestMapping("/info/pointsfrequency")
public class PointsFrequencyController {

	@Autowired
	private ReportService reportService;

	@GetMapping
	public ModelAndView show() {
		MultiValuedMap<Integer, PointCountResult> map = reportService.reportPointsFrequency();

		ModelAndView modelAndView = new ModelAndView("info/points_freq");
		modelAndView.addObject("fourPointsMap", map.get(4));
		modelAndView.addObject("threePointsMap", map.get(3));
		modelAndView.addObject("twoPointsMap", map.get(2));
		modelAndView.addObject("onePointsMap", map.get(1));
		return modelAndView;
	}

}
