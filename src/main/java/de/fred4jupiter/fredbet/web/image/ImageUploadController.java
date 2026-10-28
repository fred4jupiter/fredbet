package de.fred4jupiter.fredbet.web.image;

import de.fred4jupiter.fredbet.domain.entity.AppUser;
import de.fred4jupiter.fredbet.domain.entity.ImageMetaData;
import de.fred4jupiter.fredbet.image.ImageAdministrationService;
import de.fred4jupiter.fredbet.image.ImageUploadLimitReachedException;
import de.fred4jupiter.fredbet.security.FredBetPermission;
import de.fred4jupiter.fredbet.web.WebMessageUtil;
import de.fred4jupiter.fredbet.web.WebSecurityUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.List;

@Controller
@RequestMapping("/image")
public class ImageUploadController {

    private static final Logger LOG = LoggerFactory.getLogger(ImageUploadController.class);

    private static final String REDIRECT_SHOW_PAGE = "redirect:/image/show";

    private static final String CONTENT_TYPE_JPEG = "image/jpeg";

    private final ImageAdministrationService imageAdministrationService;

    private final WebMessageUtil messageUtil;

    private final WebSecurityUtil webSecurityUtil;

    private final ImageCommandMapper imageCommandMapper;

    ImageUploadController(ImageAdministrationService imageAdministrationService, WebMessageUtil messageUtil,
                          WebSecurityUtil webSecurityUtil,
                          ImageCommandMapper imageCommandMapper) {
        this.imageAdministrationService = imageAdministrationService;
        this.messageUtil = messageUtil;
        this.webSecurityUtil = webSecurityUtil;
        this.imageCommandMapper = imageCommandMapper;
    }

    @ModelAttribute("availableImages")
    public List<ImageCommand> availableImages(@AuthenticationPrincipal AppUser currentUser) {
        return imageCommandMapper.toListOfImageCommand(loadImageMetaData(currentUser));
    }

    private List<ImageMetaData> loadImageMetaData(AppUser currentUser) {
        if (currentUser.hasPermission(FredBetPermission.PERM_DELETE_ALL_IMAGES)) {
            return imageAdministrationService.fetchAllImagesExceptUserProfileImages();
        } else {
            return imageAdministrationService.fetchImagesOfUserExceptUserProfileImages(currentUser.getUsername());
        }
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
        final MultipartFile imageFile = imageUploadCommand.getImageFile();
        if (imageFile == null || imageFile.isEmpty()) {
            messageUtil.addErrorMsg(redirect, "image.upload.msg.noFileGiven");
            return REDIRECT_SHOW_PAGE;
        }

        if (!CONTENT_TYPE_JPEG.equals(imageFile.getContentType())) {
            messageUtil.addErrorMsg(redirect, "image.upload.msg.noJpegImage");
            return REDIRECT_SHOW_PAGE;
        }

        try {
            imageAdministrationService.saveImage(imageFile.getBytes(), imageUploadCommand.getGalleryGroup(),
                imageUploadCommand.getDescription(), webSecurityUtil.getCurrentUser());
            messageUtil.addInfoMsg(redirect, "image.upload.msg.saved");
        } catch (ImageUploadLimitReachedException e) {
            messageUtil.addErrorMsg(redirect, "image.upload.msg.limitReached", e.getLimit());
        } catch (IOException e) {
            LOG.error(e.getMessage(), e);
            messageUtil.addErrorMsg(redirect, "image.upload.msg.failed");
        }

        return REDIRECT_SHOW_PAGE;
    }

    @GetMapping("/delete/{imageKey}")
    public String deleteImage(@PathVariable String imageKey, RedirectAttributes redirect, @AuthenticationPrincipal AppUser currentUser) {
        if (!isAllowedToDeleteImageWithImageKey(currentUser, imageKey)) {
            messageUtil.addErrorMsg(redirect, "image.gallery.msg.delete.perm.denied");
            return REDIRECT_SHOW_PAGE;
        }

        imageAdministrationService.deleteImageByImageKey(imageKey);

        messageUtil.addInfoMsg(redirect, "image.gallery.msg.deleted");

        return REDIRECT_SHOW_PAGE;
    }

    private boolean isAllowedToDeleteImageWithImageKey(AppUser currentUser, String imageKey) {
        if (currentUser.hasPermission(FredBetPermission.PERM_DELETE_ALL_IMAGES)) {
            return true;
        }

        return imageAdministrationService.isImageOfUser(imageKey, currentUser);
    }
}
