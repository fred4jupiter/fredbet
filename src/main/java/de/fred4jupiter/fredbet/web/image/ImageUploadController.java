package de.fred4jupiter.fredbet.web.image;

import de.fred4jupiter.fredbet.domain.AppUser;
import de.fred4jupiter.fredbet.domain.ImageGroup;
import de.fred4jupiter.fredbet.domain.ImageMetaData;
import de.fred4jupiter.fredbet.security.FredBetPermission;
import de.fred4jupiter.fredbet.security.SecurityService;
import de.fred4jupiter.fredbet.service.image.ImageAdministrationService;
import de.fred4jupiter.fredbet.web.WebMessageUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.Base64;
import java.util.List;

@Controller
@RequestMapping("/image")
public class ImageUploadController {

    private static final String REDIRECT_SHOW_PAGE = "redirect:/image/show";

    private static final Logger log = LoggerFactory.getLogger(ImageUploadController.class);

    private final ImageAdministrationService imageAdministrationService;

    private final WebMessageUtil messageUtil;

    private final SecurityService securityService;

    private final ImageCommandMapper imageCommandMapper;

    public ImageUploadController(ImageAdministrationService imageAdministrationService, WebMessageUtil messageUtil,
                                 SecurityService securityService, ImageCommandMapper imageCommandMapper) {
        this.imageAdministrationService = imageAdministrationService;
        this.messageUtil = messageUtil;
        this.securityService = securityService;
        this.imageCommandMapper = imageCommandMapper;
    }

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

    @GetMapping("/show")
    public String showUploadPage() {
        return "image/image_upload";
    }

    @PostMapping("/upload")
    public String uploadImage(ImageUploadCommand imageUploadCommand, RedirectAttributes redirect) {
        if (StringUtils.isBlank(imageUploadCommand.getMyFileBase64())) {
            messageUtil.addErrorMsg(redirect, "image.upload.msg.noFileGiven");
            return REDIRECT_SHOW_PAGE;
        }

        final byte[] imageByte = Base64.getDecoder().decode(imageUploadCommand.getMyFileBase64().split(",")[1]);

        if (imageByte.length == 0) {
            messageUtil.addErrorMsg(redirect, "image.upload.msg.noFileGiven");
            return REDIRECT_SHOW_PAGE;
        }

        final ImageGroup imageGroup = imageAdministrationService.createOrFetchImageGroup(imageUploadCommand.getGalleryGroup());
        imageAdministrationService.saveImage(imageByte, imageGroup.getId(), imageUploadCommand.getDescription());
        messageUtil.addInfoMsg(redirect, "image.upload.msg.saved");

        return REDIRECT_SHOW_PAGE;
    }

    @GetMapping("/delete/{imageKey}")
    public String deleteImage(@PathVariable("imageKey") String imageKey, RedirectAttributes redirect) {
        if (!isAllowedToDeleteImageWithImageKey(imageKey)) {
            messageUtil.addErrorMsg(redirect, "image.gallery.msg.delete.perm.denied");
            return REDIRECT_SHOW_PAGE;
        }

        imageAdministrationService.deleteImageByImageKey(imageKey);

        messageUtil.addInfoMsg(redirect, "image.gallery.msg.deleted");

        return REDIRECT_SHOW_PAGE;
    }

    private boolean isAllowedToDeleteImageWithImageKey(String imageKey) {
        final AppUser appUser = securityService.getCurrentUser();
        if (appUser.hasPermission(FredBetPermission.PERM_DELETE_ALL_IMAGES)) {
            return true;
        }

        return imageAdministrationService.isImageOfUser(imageKey, appUser);
    }
}
