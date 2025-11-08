package de.fred4jupiter.fredbet.web.profile;

import de.fred4jupiter.fredbet.image.ImageAdministrationService;
import de.fred4jupiter.fredbet.security.SecurityService;
import de.fred4jupiter.fredbet.user.UserService;
import de.fred4jupiter.fredbet.web.WebMessageUtil;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Base64;

@Controller
@RequestMapping("/cropping")
public class ImageCroppingController {

    private static final Logger LOG = LoggerFactory.getLogger(ImageCroppingController.class);

    private static final String REDIRECT_SHOW_PAGE = "redirect:/cropping/show";

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
    public String uploadImage(@RequestParam("croppedFileBase64") String imageBase64, RedirectAttributes redirect) {
        if (StringUtils.isBlank(imageBase64)) {
            LOG.error("No base64 image given");
            messageUtil.addErrorMsg(redirect, "image.upload.msg.noFileGiven");
            return REDIRECT_SHOW_PAGE;
        }

        byte[] imageByte = Base64.getDecoder().decode(imageBase64.split(",")[1]);
        LOG.debug("image size: {}", imageByte != null ? FileUtils.byteCountToDisplaySize(imageByte.length) : null);

        if (imageByte == null || imageByte.length == 0) {
            messageUtil.addErrorMsg(redirect, "image.upload.msg.noFileGiven");
            return REDIRECT_SHOW_PAGE;
        }

        imageAdministrationService.saveUserProfileImage(imageByte, securityServic.getCurrentUser());

        messageUtil.addInfoMsg(redirect, "image.upload.msg.saved");

        return REDIRECT_SHOW_PAGE;
    }

}
