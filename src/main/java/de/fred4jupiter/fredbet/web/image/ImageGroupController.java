package de.fred4jupiter.fredbet.web.image;

import de.fred4jupiter.fredbet.domain.entity.ImageGroup;
import de.fred4jupiter.fredbet.security.FredBetPermission;
import de.fred4jupiter.fredbet.image.group.ImageGroupExistsException;
import de.fred4jupiter.fredbet.image.group.ImageGroupNotDeletableException;
import de.fred4jupiter.fredbet.image.group.ImageGroupService;
import de.fred4jupiter.fredbet.web.WebMessageUtil;
import jakarta.validation.Valid;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Comparator;
import java.util.List;

@Controller
@RequestMapping("/imagegroup")
@PreAuthorize("hasAuthority('" + FredBetPermission.PERM_EDIT_IMAGE_GROUP + "')")
public class ImageGroupController {

    private static final String REDIRECT_SHOW_IMAGEGROUP = "redirect:/imagegroup/show";

    private static final String PAGE_IMAGE_GROUP = "image/image_group";

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

    private ImageGroupCommand mapToImageGroupCommand(ImageGroup imageGroup) {
        ImageGroupCommand imageGroupCommand = new ImageGroupCommand();
        imageGroupCommand.setId(imageGroup.getId());
        imageGroupCommand.setName(imageGroup.getName());
        return imageGroupCommand;
    }

    @GetMapping("/show")
    public String show(Model model) {
        List<ImageGroup> imageGroups = imageGroupService.findAvailableImageGroups();
        List<ImageGroupCommand> availableImageGroups = imageGroups.stream().map(this::mapToImageGroupCommand)
                .sorted(Comparator.comparing(ImageGroupCommand::getName, String.CASE_INSENSITIVE_ORDER))
                .toList();
        model.addAttribute("availableImageGroups", availableImageGroups);
        return PAGE_IMAGE_GROUP;
    }

    @PostMapping("/delete")
    public String deleteImage(@ModelAttribute ImageGroupCommand imageGroupCommand, RedirectAttributes redirect) {
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
            return PAGE_IMAGE_GROUP;
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
