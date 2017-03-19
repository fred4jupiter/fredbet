package de.fred4jupiter.fredbet.web.image;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import de.fred4jupiter.fredbet.security.FredBetPermission;
import de.fred4jupiter.fredbet.service.image.ImageGroupExistsException;
import de.fred4jupiter.fredbet.service.image.ImageGroupService;
import de.fred4jupiter.fredbet.web.MessageUtil;

@Controller
@RequestMapping("/imagegroup")
public class ImageGroupController {

	@Autowired
	private ImageGroupService imageGroupService;

	@Autowired
	private MessageUtil messageUtil;

	@ModelAttribute("imageGroupCommand")
	public ImageGroupCommand init() {
		return new ImageGroupCommand();
	}

	@ModelAttribute("availableImageGroups")
	public List<ImageGroupCommand> availableImages() {
		return imageGroupService.findAvailableImages();
	}

	@RequestMapping(value = "/show", method = RequestMethod.GET)
	public ModelAndView show() {
		return new ModelAndView("image/imageGroup");
	}

	@RequestMapping(value = "/delete/{id}", method = RequestMethod.GET)
	public ModelAndView deleteImage(@PathVariable("id") Long imageGroupId, RedirectAttributes redirect) {
		try {
			imageGroupService.deleteImageGroup(imageGroupId);
			messageUtil.addInfoMsg(redirect, "image.group.msg.deleted");
		} catch (DataIntegrityViolationException e) {
			messageUtil.addErrorMsg(redirect, "image.group.msg.deletionHasReferences");
		}

		return new ModelAndView("redirect:/imagegroup/show");
	}

	@PreAuthorize("hasAuthority('" + FredBetPermission.PERM_EDIT_IMAGE_GROUP + "')")
	@RequestMapping(value = "/add", method = RequestMethod.POST)
	public ModelAndView addImageGroup(@ModelAttribute("imageGroupCommand") ImageGroupCommand imageGroupCommand, BindingResult result,
			RedirectAttributes redirect) {

		try {
			imageGroupService.addImageGroup(imageGroupCommand.getName());
			messageUtil.addInfoMsg(redirect, "image.group.msg.added", imageGroupCommand.getName());
		} catch (ImageGroupExistsException e) {
			messageUtil.addErrorMsg(redirect, "image.group.msg.groupExist", imageGroupCommand.getName());
		}

		return new ModelAndView("redirect:/imagegroup/show");
	}
}
