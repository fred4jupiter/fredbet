package de.fred4jupiter.fredbet.web.user;

import java.util.Base64;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import de.fred4jupiter.fredbet.service.ImageCroppingService;
import de.fred4jupiter.fredbet.web.MessageUtil;

@Controller
@RequestMapping("/cropping")
public class ImageCroppingController {

	private static final Logger LOG = LoggerFactory.getLogger(ImageCroppingController.class);

	private static final String REDIRECT_SHOW_PAGE = "redirect:/cropping/show";

	@Autowired
	private MessageUtil messageUtil;

	@Autowired
	private ImageCroppingService imageCroppingService;

	@RequestMapping("/show")
	public ModelAndView show() {
		return new ModelAndView("user/crop_image");
	}

	@RequestMapping(value = "/upload", method = RequestMethod.POST)
	public ModelAndView uploadImage(@RequestParam("croppedFileBase64") String imageBase64, RedirectAttributes redirect) {
		LOG.debug("imageBase64: {}", imageBase64);

		if (StringUtils.isBlank(imageBase64)) {
			LOG.error("No base64 image given");
			messageUtil.addErrorMsg(redirect, "image.upload.msg.noFileGiven");
			return new ModelAndView(REDIRECT_SHOW_PAGE);
		}

		byte[] imageByte = Base64.getDecoder().decode(imageBase64.split(",")[1]);

		if (imageByte.length == 0) {
			messageUtil.addErrorMsg(redirect, "image.upload.msg.noFileGiven");
			return new ModelAndView(REDIRECT_SHOW_PAGE);
		}

		imageCroppingService.saveUserProfileImage(imageByte);

		messageUtil.addInfoMsg(redirect, "image.upload.msg.saved");

		return new ModelAndView(REDIRECT_SHOW_PAGE);
	}

}
