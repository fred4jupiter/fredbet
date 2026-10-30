package de.fred4jupiter.fredbet.web.login;

import de.fred4jupiter.fredbet.admin.LoginLogoService;
import de.fred4jupiter.fredbet.image.BinaryImage;
import de.fred4jupiter.fredbet.web.WebMessageUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.Optional;

@Controller
@RequestMapping("/loginlogo")
public class LoginLogoController {

    private static final Logger LOG = LoggerFactory.getLogger(LoginLogoController.class);

    private static final String REDIRECT_SHOW_PAGE = "redirect:/loginlogo";

    private static final String CONTENT_TYPE_JPEG = "image/jpeg";

    private final WebMessageUtil messageUtil;

    private final LoginLogoService loginLogoService;

    public LoginLogoController(WebMessageUtil messageUtil, LoginLogoService loginLogoService) {
        this.messageUtil = messageUtil;
        this.loginLogoService = loginLogoService;
    }

    @ModelAttribute("loginLogoCommand")
    public LoginLogoCommand loginLogoCommand() {
        return new LoginLogoCommand();
    }

    @GetMapping
    public String showPage(Model model) {
        Optional<BinaryImage> bytes = loginLogoService.loadLoginLogo();
        bytes.ifPresent(value -> model.addAttribute("currentLoginLogo", value.getAsBase64()));
        return "loginlogo/login_logo";
    }

    @PostMapping
    public String uploadImage(LoginLogoCommand loginLogoCommand, RedirectAttributes redirect) {
        final MultipartFile imageFile = loginLogoCommand.getImageFile();
        if (imageFile == null || imageFile.isEmpty()) {
            messageUtil.addErrorMsg(redirect, "image.upload.msg.noFileGiven");
            return REDIRECT_SHOW_PAGE;
        }

        if (!CONTENT_TYPE_JPEG.equals(imageFile.getContentType())) {
            messageUtil.addErrorMsg(redirect, "image.upload.msg.noJpegImage");
            return REDIRECT_SHOW_PAGE;
        }

        try {
            loginLogoService.saveImage(imageFile.getBytes());
            messageUtil.addInfoMsg(redirect, "image.upload.msg.saved");
        } catch (IOException e) {
            LOG.error(e.getMessage(), e);
            messageUtil.addErrorMsg(redirect, "image.upload.msg.failed");
        }

        return REDIRECT_SHOW_PAGE;
    }

    @GetMapping("/delete")
    public String deleteLoginLogo(RedirectAttributes redirect) {
        loginLogoService.deleteLoginLogo();
        messageUtil.addInfoMsg(redirect, "login.logo.deleted");
        return REDIRECT_SHOW_PAGE;
    }
}
