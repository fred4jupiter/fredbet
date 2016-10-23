package de.fred4jupiter.fredbet.web.image;

import java.io.IOException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import de.fred4jupiter.fredbet.service.ImageUploadService;
import de.fred4jupiter.fredbet.web.MessageUtil;

@Controller
@RequestMapping("/image")
public class ImageUploadController {

	private static final Logger log = LoggerFactory.getLogger(ImageUploadController.class);

	@Autowired
	private ImageUploadService imageUploadService;

	@Autowired
	private MessageUtil messageUtil;
	
	@ModelAttribute("availableImages")
	public List<String> availableImages() {
		return imageUploadService.fetchAllImagesAsBase64();
	}

	@RequestMapping(value = "/show", method = RequestMethod.GET)
	public ModelAndView showUploadPage() {
		ModelAndView modelAndView = new ModelAndView("image/imageUpload");
		return modelAndView;
	}

	@RequestMapping(value = "/upload", method = RequestMethod.POST)
	public ModelAndView importParse(@RequestParam("myFile") MultipartFile myFile, RedirectAttributes redirect) {
		try {
			imageUploadService.saveImageInDatabase(myFile.getOriginalFilename(), myFile.getBytes());
			messageUtil.addInfoMsg(redirect, "image.upload.msg.saved");
		} catch (IOException e) {
			log.error(e.getMessage(), e);
			messageUtil.addErrorMsg(redirect, "image.upload.msg.failed");
		}

		return new ModelAndView("redirect:/image/show");
	}
}
