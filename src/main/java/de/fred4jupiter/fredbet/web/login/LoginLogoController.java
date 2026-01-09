package de.fred4jupiter.fredbet.web.login;

import de.fred4jupiter.fredbet.admin.LoginLogoService;
import de.fred4jupiter.fredbet.web.WebMessageUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Base64;

@Controller
@RequestMapping("/loginlogo")
public class LoginLogoController {

    private static final String REDIRECT_SHOW_PAGE = "redirect:/loginlogo";

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
    public String showPage() {
        return "loginlogo/login_logo";
    }

    @PostMapping
    public String uploadImage(LoginLogoCommand loginLogoCommand, RedirectAttributes redirect) {
        if (StringUtils.isBlank(loginLogoCommand.getMyFileBase64())) {
            messageUtil.addErrorMsg(redirect, "image.upload.msg.noFileGiven");
            return REDIRECT_SHOW_PAGE;
        }

        final byte[] imageByte = Base64.getDecoder().decode(loginLogoCommand.getMyFileBase64().split(",")[1]);

        if (imageByte.length == 0) {
            messageUtil.addErrorMsg(redirect, "image.upload.msg.noFileGiven");
            return REDIRECT_SHOW_PAGE;
        }

        loginLogoService.saveImage(imageByte);
        messageUtil.addInfoMsg(redirect, "image.upload.msg.saved");

        return REDIRECT_SHOW_PAGE;
    }

    @GetMapping("/delete")
    public String deleteLoginLogo(RedirectAttributes redirect) {
        loginLogoService.deleteLoginLogo();
        messageUtil.addInfoMsg(redirect, "login.logo.deleted");
        return REDIRECT_SHOW_PAGE;
    }
}
