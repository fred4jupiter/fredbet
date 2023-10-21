package de.fred4jupiter.fredbet.web.image;

import de.fred4jupiter.fredbet.domain.ImageGroup;
import de.fred4jupiter.fredbet.security.FredBetPermission;
import de.fred4jupiter.fredbet.service.image.ImageGroupExistsException;
import de.fred4jupiter.fredbet.service.image.ImageGroupNotDeletableException;
import de.fred4jupiter.fredbet.service.image.ImageGroupService;
import de.fred4jupiter.fredbet.web.WebMessageUtil;
import jakarta.validation.Valid;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/imagegroup")
@PreAuthorize("hasAuthority('" + FredBetPermission.PERM_EDIT_IMAGE_GROUP + "')")
public class ImageGroupController {

    private static final String REDIRECT_SHOW_IMAGEGROUP = "redirect:/imagegroup/show";

    private final ImageGroupService imageGroupService;

    private final WebMessageUtil webMessageUtil;

    public ImageGroupController(ImageGroupService imageGroupService, WebMessageUtil webMessageUtil) {
        this.imageGroupService = imageGroupService;
        this.webMessageUtil = webMessageUtil;
    }

    @ModelAttribute("imageGroupCommand")
    public ImageGroupCommand init() {
        return new ImageGroupCommand();
    }

    @ModelAttribute("availableImageGroups")
    public List<ImageGroupCommand> availableImageGroups() {
        List<ImageGroup> imageGroups = imageGroupService.findAvailableImageGroups();
        return imageGroups.stream().map(this::mapToImageGroupCommand).sorted().collect(Collectors.toList());
    }

    private ImageGroupCommand mapToImageGroupCommand(ImageGroup imageGroup) {
        ImageGroupCommand imageGroupCommand = new ImageGroupCommand();
        imageGroupCommand.setId(imageGroup.getId());
        imageGroupCommand.setName(imageGroup.getName());
        return imageGroupCommand;
    }

    @GetMapping("/show")
    public String show() {
        return "image/image_group";
    }

    @PostMapping("/delete")
    public String deleteImage(@ModelAttribute("imageGroupCommand") ImageGroupCommand imageGroupCommand, RedirectAttributes redirect) {
        try {
            imageGroupService.deleteImageGroup(imageGroupCommand.getId());
            webMessageUtil.addInfoMsg(redirect, "image.group.msg.deleted");
        } catch (DataIntegrityViolationException e) {
            webMessageUtil.addErrorMsg(redirect, "image.group.msg.deletionHasReferences");
        } catch (ImageGroupNotDeletableException e) {
            webMessageUtil.addErrorMsg(redirect, "image.group.msg.deletionNotAllowed");
        }

        return REDIRECT_SHOW_IMAGEGROUP;
    }

    @PostMapping("/save")
    public String addImageGroup(@Valid ImageGroupCommand imageGroupCommand, BindingResult bindingResult, RedirectAttributes redirect) {
        if (bindingResult.hasErrors()) {
            return "image/image_group";
        }

        try {
            if (imageGroupCommand.getId() == null) {
                imageGroupService.addImageGroup(imageGroupCommand.getName());
                webMessageUtil.addInfoMsg(redirect, "image.group.msg.added", imageGroupCommand.getName());
            } else {
                imageGroupService.updateImageGroup(imageGroupCommand.getId(), imageGroupCommand.getName());
                webMessageUtil.addInfoMsg(redirect, "image.group.msg.updated", imageGroupCommand.getName());
            }
        } catch (ImageGroupExistsException e) {
            webMessageUtil.addErrorMsg(redirect, "image.group.msg.groupExist", imageGroupCommand.getName());
        }

        return REDIRECT_SHOW_IMAGEGROUP;
    }
}
