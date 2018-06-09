package de.fred4jupiter.fredbet.web.image;

import java.io.IOException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import de.fred4jupiter.fredbet.domain.AppUser;
import de.fred4jupiter.fredbet.domain.ImageGroup;
import de.fred4jupiter.fredbet.domain.ImageMetaData;
import de.fred4jupiter.fredbet.security.FredBetPermission;
import de.fred4jupiter.fredbet.security.SecurityService;
import de.fred4jupiter.fredbet.service.image.ImageAdministrationService;
import de.fred4jupiter.fredbet.web.WebMessageUtil;

@Controller
@RequestMapping("/image")
public class ImageUploadController {

	private static final String REDIRECT_SHOW_PAGE = "redirect:/image/show";

	private static final Logger log = LoggerFactory.getLogger(ImageUploadController.class);

	@Autowired
	private ImageAdministrationService imageAdministrationService;

	@Autowired
	private WebMessageUtil messageUtil;

	@Autowired
	private SecurityService securityService;

	@Autowired
	private ImageCommandMapper imageCommandMapper;

	@ModelAttribute("availableImages")
	public List<ImageCommand> availableImages() {
		List<ImageMetaData> imageMetadataList;
		if (securityService.isCurrentUserHavingPermission(FredBetPermission.PERM_DELETE_ALL_IMAGES)) {
			imageMetadataList = imageAdministrationService.fetchAllImagesExceptUserProfileImages();
		} else {
			imageMetadataList = imageAdministrationService.fetchImagesOfUserExceptUserProfileImages(securityService.getCurrentUserName());
		}

		return imageCommandMapper.toListOfImageCommand(imageMetadataList);
	}

	@ModelAttribute("availableImageGroups")
	public List<String> availableImageGroups() {
		return imageAdministrationService.findAvailableImageGroups();
	}

	@ModelAttribute("imageUploadCommand")
	public ImageUploadCommand initImageUploadCommand() {
		return new ImageUploadCommand();
	}

	@RequestMapping(value = "/show", method = RequestMethod.GET)
	public ModelAndView showUploadPage() {
		return new ModelAndView("image/image_upload");
	}

	@RequestMapping(value = "/upload", method = RequestMethod.POST)
	public ModelAndView uploadImage(ImageUploadCommand imageUploadCommand, BindingResult result, RedirectAttributes redirect) {
		try {
			MultipartFile myFile = imageUploadCommand.getMyFile();
			if (myFile == null || myFile.getBytes() == null || myFile.getBytes().length == 0) {
				messageUtil.addErrorMsg(redirect, "image.upload.msg.noFileGiven");
				return new ModelAndView(REDIRECT_SHOW_PAGE);
			}

			if (!MediaType.IMAGE_JPEG_VALUE.equals(myFile.getContentType())) {
				messageUtil.addErrorMsg(redirect, "image.upload.msg.noJpegImage");
				return new ModelAndView(REDIRECT_SHOW_PAGE);
			}

			final ImageGroup imageGroup = imageAdministrationService.createOrFetchImageGroup(imageUploadCommand.getGalleryGroup());
			imageAdministrationService.saveImage(myFile.getBytes(), imageGroup.getId(), imageUploadCommand.getDescription(),
					imageUploadCommand.getRotation());
			messageUtil.addInfoMsg(redirect, "image.upload.msg.saved");
		} catch (IOException e) {
			log.error(e.getMessage(), e);
			messageUtil.addErrorMsg(redirect, "image.upload.msg.failed");
		}

		return new ModelAndView(REDIRECT_SHOW_PAGE);
	}

	@RequestMapping(value = "/delete/{imageKey}", method = RequestMethod.GET)
	public ModelAndView deleteImage(@PathVariable("imageKey") String imageKey, RedirectAttributes redirect) {
		if (!isAllowedToDeleteImageWithImageKey(imageKey)) {
			messageUtil.addErrorMsg(redirect, "image.gallery.msg.delete.perm.denied");
			return new ModelAndView(REDIRECT_SHOW_PAGE);
		}

		imageAdministrationService.deleteImageByImageKey(imageKey);

		messageUtil.addInfoMsg(redirect, "image.gallery.msg.deleted");

		return new ModelAndView(REDIRECT_SHOW_PAGE);
	}

	private boolean isAllowedToDeleteImageWithImageKey(String imageKey) {
		final AppUser appUser = securityService.getCurrentUser();
		if (appUser.hasPermission(FredBetPermission.PERM_DELETE_ALL_IMAGES)) {
			return true;
		}

		if (imageAdministrationService.isImageOfUser(imageKey, appUser)) {
			return true;
		}

		return false;
	}
}
