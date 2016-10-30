package de.fred4jupiter.fredbet.web.image;

import java.io.ByteArrayInputStream;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import de.fred4jupiter.fredbet.service.ImageUploadService;

@Controller
@RequestMapping("/gallery")
public class ImageGalleryController {

	private static final Logger log = LoggerFactory.getLogger(ImageGalleryController.class);

	@Autowired
	private ImageUploadService imageUploadService;

	@RequestMapping(value = "/show", method = RequestMethod.GET)
	public ModelAndView showGallery() {
		ModelAndView modelAndView = new ModelAndView("image/gallery");
		List<ImageCommand> images = imageUploadService.fetchAllImages();

		Map<String, List<ImageCommand>> grouped = images.stream()
				.collect(Collectors.groupingBy(ImageCommand::getGalleryGroup));

		modelAndView.addObject("groupedImageCommands", grouped);
		return modelAndView;
	}

	@RequestMapping(value = "/show/{id}", method = RequestMethod.GET, produces = MediaType.IMAGE_JPEG_VALUE)
	public ResponseEntity<byte[]> showImage(@PathVariable("id") Long imageId, HttpServletResponse response) {
		response.setHeader("Content-Type", MediaType.IMAGE_JPEG_VALUE);
		byte[] imageByte = imageUploadService.loadImageById(imageId);
		if (imageByte == null) {
			return new ResponseEntity<byte[]>(HttpStatus.NOT_FOUND);
		}

		try (ByteArrayInputStream in = new ByteArrayInputStream(imageByte)) {
			IOUtils.copy(in, response.getOutputStream());
			response.flushBuffer();
			return new ResponseEntity<byte[]>(HttpStatus.OK);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return new ResponseEntity<byte[]>(HttpStatus.NOT_FOUND);
		}
	}

}
