package de.fred4jupiter.fredbet.web.image;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/gallery")
public class ImageGalleryController {

	@RequestMapping(value = "/show", method = RequestMethod.GET)
	public ModelAndView showUploadPage() {
		ModelAndView modelAndView = new ModelAndView("image/gallery");
		return modelAndView;
	}
}
