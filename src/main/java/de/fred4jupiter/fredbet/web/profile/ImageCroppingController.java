package de.fred4jupiter.fredbet.web.profile;

import de.fred4jupiter.fredbet.image.ImageAdministrationService;
import de.fred4jupiter.fredbet.security.SecurityService;
import de.fred4jupiter.fredbet.user.UserService;
import de.fred4jupiter.fredbet.web.WebMessageUtil;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;

@Controller
@RequestMapping("/cropping")
public class ImageCroppingController {

    private static final Logger LOG = LoggerFactory.getLogger(ImageCroppingController.class);

    private static final String REDIRECT_SHOW_PAGE = "redirect:/cropping/show";

    private static final String CONTENT_TYPE_JPEG = "image/jpeg";

    private final WebMessageUtil messageUtil;

    private final ImageAdministrationService imageAdministrationService;

    private final SecurityService securityServic;

    public ImageCroppingController(WebMessageUtil messageUtil, ImageAdministrationService imageAdministrationService, SecurityService securityServic) {
        this.messageUtil = messageUtil;
        this.imageAdministrationService = imageAdministrationService;
        this.securityServic = securityServic;
    }

    @GetMapping("/show")
    public String show() {
        return "profile/crop_image";
    }

    @PostMapping("/upload")
    public String uploadImage(@RequestParam(value = "croppedFile", required = false) MultipartFile croppedFile, RedirectAttributes redirect) {
        if (croppedFile == null || croppedFile.isEmpty()) {
            LOG.error("No cropped image given");
            messageUtil.addErrorMsg(redirect, "image.upload.msg.noFileGiven");
            return REDIRECT_SHOW_PAGE;
        }

        if (!CONTENT_TYPE_JPEG.equals(croppedFile.getContentType())) {
            messageUtil.addErrorMsg(redirect, "image.upload.msg.noJpegImage");
            return REDIRECT_SHOW_PAGE;
        }

        try {
            final byte[] imageByte = croppedFile.getBytes();
            LOG.debug("image size: {}", FileUtils.byteCountToDisplaySize(imageByte.length));
            imageAdministrationService.saveUserProfileImage(imageByte, securityServic.getCurrentUser());
        } catch (IOException e) {
            LOG.error(e.getMessage(), e);
            messageUtil.addErrorMsg(redirect, "image.upload.msg.failed");
            return REDIRECT_SHOW_PAGE;
        }

        messageUtil.addInfoMsg(redirect, "image.upload.msg.saved");

        return REDIRECT_SHOW_PAGE;
    }

}
