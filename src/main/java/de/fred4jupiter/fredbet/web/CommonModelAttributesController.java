package de.fred4jupiter.fredbet.web;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice(annotations = Controller.class)
public class CommonModelAttributesController {

    private final LanguageUtil languageUtil;

    public CommonModelAttributesController(LanguageUtil languageUtil) {
        this.languageUtil = languageUtil;
    }

    @ModelAttribute
    public CommonAttributes processRequest(Model model) {
        CommonAttributes commonAttributes = new CommonAttributes();
        commonAttributes.setLanguageUtil(languageUtil);
        model.addAttribute("commonAttributes", commonAttributes);
        return commonAttributes;
    }
}
