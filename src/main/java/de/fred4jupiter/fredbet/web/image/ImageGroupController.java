package de.fred4jupiter.fredbet.web.image;

import java.util.List;
import java.util.stream.Collectors;

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

import de.fred4jupiter.fredbet.domain.ImageGroup;
import de.fred4jupiter.fredbet.repository.ImageGroupRepository;
import de.fred4jupiter.fredbet.security.FredBetPermission;
import de.fred4jupiter.fredbet.web.MessageUtil;

@Controller
@RequestMapping("/imagegroup")
public class ImageGroupController {

	@Autowired
	private ImageGroupRepository imageGroupRepository;

	@Autowired
	private MessageUtil messageUtil;

	@ModelAttribute("imageGroupCommand")
	public ImageGroupCommand init() {
		return new ImageGroupCommand();
	}

	@ModelAttribute("availableImageGroups")
	public List<ImageGroupCommand> availableImages() {
		List<ImageGroup> imageGroups = imageGroupRepository.findAll();

		return imageGroups.stream().map(imageGroup -> mapToImageGroupCommand(imageGroup)).collect(Collectors.toList());
	}

	@RequestMapping(value = "/show", method = RequestMethod.GET)
	public ModelAndView show() {
		return new ModelAndView("image/imageGroup");
	}

	@RequestMapping(value = "/delete/{id}", method = RequestMethod.GET)
	public ModelAndView deleteImage(@PathVariable("id") Long imageGroupId, RedirectAttributes redirect) {
		try {
			imageGroupRepository.delete(imageGroupId);
			messageUtil.addInfoMsg(redirect, "image.group.msg.deleted");
		} catch (DataIntegrityViolationException e) {
			messageUtil.addErrorMsg(redirect, "image.group.msg.deletionHasReferences");
		}

		return new ModelAndView("redirect:/imagegroup/show");
	}

	@PreAuthorize("hasAuthority('" + FredBetPermission.PERM_EDIT_IMAGE_GROUP + "')")
	@RequestMapping(value = "/add", method = RequestMethod.POST)
	public ModelAndView addImageGroup(@ModelAttribute("imageGroupCommand") ImageGroupCommand imageGroupCommand,
			BindingResult result, RedirectAttributes redirect) {

		ImageGroup foundImageGroup = imageGroupRepository.findByName(imageGroupCommand.getName());
		if (foundImageGroup != null) {
			messageUtil.addErrorMsg(redirect, "image.group.msg.groupExist", imageGroupCommand.getName());
		} else {
			imageGroupRepository.save(new ImageGroup(imageGroupCommand.getName()));
			messageUtil.addInfoMsg(redirect, "image.group.msg.added", imageGroupCommand.getName());
		}

		return new ModelAndView("redirect:/imagegroup/show");
	}

	private ImageGroupCommand mapToImageGroupCommand(ImageGroup imageGroup) {
		ImageGroupCommand imageGroupCommand = new ImageGroupCommand();
		imageGroupCommand.setId(imageGroup.getId());
		imageGroupCommand.setName(imageGroup.getName());
		return imageGroupCommand;
	}
}
