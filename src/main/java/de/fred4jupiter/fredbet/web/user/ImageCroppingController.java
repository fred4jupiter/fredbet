package de.fred4jupiter.fredbet.web.user;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/cropping")
public class ImageCroppingController {

	@RequestMapping("/show")
    public ModelAndView show() {
        return new ModelAndView("user/crop_image");
    }
}
