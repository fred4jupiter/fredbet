package de.fred4jupiter.fredbet.web.user;

import java.io.File;
import java.io.IOException;
import java.util.Base64;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import de.fred4jupiter.fredbet.security.SecurityService;
import de.fred4jupiter.fredbet.service.image.BinaryImage;
import de.fred4jupiter.fredbet.service.image.ImageAdministrationService;
import de.fred4jupiter.fredbet.web.MessageUtil;

@Controller
@RequestMapping("/cropping")
public class ImageCroppingController {

	private static final String GALLERY_NAME = "USER_IMAGE_GALLERY";

	private static final Logger LOG = LoggerFactory.getLogger(ImageCroppingController.class);

	private static final String REDIRECT_SHOW_PAGE = "redirect:/cropping/show";

	@Autowired
	private MessageUtil messageUtil;

	@Autowired
	private ImageAdministrationService imageAdministrationService;

	@Autowired
	private SecurityService securityService;

	@RequestMapping("/show")
	public ModelAndView show() {
		ImageCroppingCommand imageCroppingCommand = new ImageCroppingCommand();

		final Long imageId = imageAdministrationService.loadImageOfUser(securityService.getCurrentUserName(), GALLERY_NAME);
		imageCroppingCommand.setImageId(imageId);
		return new ModelAndView("user/crop_image", "imageCroppingCommand", imageCroppingCommand);
	}

	@RequestMapping(value = "/show/{id}", method = RequestMethod.GET, produces = MediaType.IMAGE_JPEG_VALUE)
	public ResponseEntity<byte[]> showImage(@PathVariable("id") Long imageId, WebRequest webRequest) {
		final String etag = "" + imageId;
		boolean notModified = webRequest.checkNotModified(etag);
		if (notModified) {
			LOG.debug("image with id={} and etag={} has not been modified.", imageId, etag);
			return ResponseEntity.status(HttpStatus.NOT_MODIFIED).cacheControl(CacheControl.maxAge(1, TimeUnit.DAYS)).eTag(etag).body(null);
		}

		final BinaryImage binaryImage = imageAdministrationService.loadImageById(imageId);

		if (binaryImage == null) {
			return new ResponseEntity<byte[]>(HttpStatus.NOT_FOUND);
		}

		return ResponseEntity.ok().eTag(etag).header("Content-Type", MediaType.IMAGE_JPEG_VALUE).body(binaryImage.getImageBinary());
	}

	@RequestMapping(value = "/upload", method = RequestMethod.POST)
	public ModelAndView uploadImage(@RequestParam("croppedFileBase64") String imageBase64, RedirectAttributes redirect) {
		LOG.debug("imageBase64: {}", imageBase64);

		if (StringUtils.isBlank(imageBase64)) {
			LOG.error("No base64 image given");
			return new ModelAndView(REDIRECT_SHOW_PAGE);
		}

		String[] splitted = imageBase64.split(",");

		byte[] imageByte = Base64.getDecoder().decode(splitted[1]);

		try {
			FileUtils.writeByteArrayToFile(new File("d://Temp2/image.png"), imageByte);
		} catch (IOException e) {
			LOG.error(e.getMessage(), e);
		}

		// try {
		// if (multipartFile == null || multipartFile.getBytes() == null ||
		// multipartFile.getBytes().length == 0) {
		// messageUtil.addErrorMsg(redirect, "image.upload.msg.noFileGiven");
		// return new ModelAndView(REDIRECT_SHOW_PAGE);
		// }
		//
		// if
		// (!multipartFile.getContentType().equals(MediaType.IMAGE_JPEG_VALUE))
		// {
		// messageUtil.addErrorMsg(redirect, "image.upload.msg.noJpegImage");
		// return new ModelAndView(REDIRECT_SHOW_PAGE);
		// }
		//
		// String currentUserName = securityService.getCurrentUserName();
		// imageAdministrationService.saveImage(multipartFile.getBytes(),
		// GALLERY_NAME, currentUserName, Rotation.NONE);
		// messageUtil.addInfoMsg(redirect, "image.upload.msg.saved");
		// } catch (IOException e) {
		// LOG.error(e.getMessage(), e);
		// messageUtil.addErrorMsg(redirect, "image.upload.msg.failed");
		// }

		return new ModelAndView(REDIRECT_SHOW_PAGE);
	}

}
