package de.fred4jupiter.fredbet.web.profile;

import java.util.Base64;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import de.fred4jupiter.fredbet.service.image.ImageAdministrationService;
import de.fred4jupiter.fredbet.web.WebMessageUtil;

@Controller
@RequestMapping("/cropping")
public class ImageCroppingController {

	private static final Logger LOG = LoggerFactory.getLogger(ImageCroppingController.class);

	private static final String REDIRECT_SHOW_PAGE = "redirect:/cropping/show";

	@Autowired
	private WebMessageUtil messageUtil;

	@Autowired
	private ImageAdministrationService imageAdministrationService;

	@GetMapping("/show")
	public String show() {
		return "profile/crop_image";
	}

	@PostMapping("/upload")
	public String uploadImage(@RequestParam("croppedFileBase64") String imageBase64, RedirectAttributes redirect) {
		LOG.debug("imageBase64: {}", imageBase64);

		if (StringUtils.isBlank(imageBase64)) {
			LOG.error("No base64 image given");
			messageUtil.addErrorMsg(redirect, "image.upload.msg.noFileGiven");
			return REDIRECT_SHOW_PAGE;
		}

		byte[] imageByte = Base64.getDecoder().decode(imageBase64.split(",")[1]);

		if (imageByte.length == 0) {
			messageUtil.addErrorMsg(redirect, "image.upload.msg.noFileGiven");
			return REDIRECT_SHOW_PAGE;
		}

		imageAdministrationService.saveUserProfileImage(imageByte);

		messageUtil.addInfoMsg(redirect, "image.upload.msg.saved");

		return REDIRECT_SHOW_PAGE;
	}

}
