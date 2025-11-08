package de.fred4jupiter.fredbet.web.image;

import de.fred4jupiter.fredbet.domain.entity.AppUser;
import de.fred4jupiter.fredbet.domain.entity.ImageMetaData;
import de.fred4jupiter.fredbet.security.FredBetPermission;
import de.fred4jupiter.fredbet.image.ImageAdministrationService;
import de.fred4jupiter.fredbet.image.ImageUploadLimitReachedException;
import de.fred4jupiter.fredbet.web.WebMessageUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Base64;
import java.util.List;

@Controller
@RequestMapping("/image")
public class ImageUploadController {

    private static final String REDIRECT_SHOW_PAGE = "redirect:/image/show";

    private final ImageAdministrationService imageAdministrationService;

    private final WebMessageUtil messageUtil;

    private final ImageCommandMapper imageCommandMapper;

    ImageUploadController(ImageAdministrationService imageAdministrationService, WebMessageUtil messageUtil,
                                 ImageCommandMapper imageCommandMapper) {
        this.imageAdministrationService = imageAdministrationService;
        this.messageUtil = messageUtil;
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
        if (StringUtils.isBlank(imageUploadCommand.getMyFileBase64())) {
            messageUtil.addErrorMsg(redirect, "image.upload.msg.noFileGiven");
            return REDIRECT_SHOW_PAGE;
        }

        final byte[] imageByte = Base64.getDecoder().decode(imageUploadCommand.getMyFileBase64().split(",")[1]);

        if (imageByte.length == 0) {
            messageUtil.addErrorMsg(redirect, "image.upload.msg.noFileGiven");
            return REDIRECT_SHOW_PAGE;
        }

        try {
            imageAdministrationService.saveImage(imageByte, imageUploadCommand.getGalleryGroup(), imageUploadCommand.getDescription());
            messageUtil.addInfoMsg(redirect, "image.upload.msg.saved");
        } catch (ImageUploadLimitReachedException e) {
            messageUtil.addErrorMsg(redirect, "image.upload.msg.limitReached", e.getLimit());
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
