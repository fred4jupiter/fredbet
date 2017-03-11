package de.fred4jupiter.fredbet.web.image;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.ModelAndView;

import de.fred4jupiter.fredbet.service.image.BinaryImage;
import de.fred4jupiter.fredbet.service.image.DownloadService;
import de.fred4jupiter.fredbet.service.image.ImageAdministrationService;
import de.fred4jupiter.fredbet.web.MessageUtil;

@Controller
@RequestMapping("/gallery")
public class ImageGalleryController {

	private static final Logger LOG = LoggerFactory.getLogger(ImageGalleryController.class);

	@Autowired
	private MessageUtil messageUtil;

	@Autowired
	private ImageAdministrationService imageAdministrationService;

	@Autowired
	private DownloadService downloadService;

	@RequestMapping(value = "/show", method = RequestMethod.GET)
	public ModelAndView showGallery(ModelMap modelMap) {
		ModelAndView modelAndView = new ModelAndView("image/gallery");

		List<ImageCommand> images = imageAdministrationService.fetchAllImages();

		Map<String, List<ImageCommand>> grouped = images.stream().collect(Collectors.groupingBy(ImageCommand::getGalleryGroup));

		modelAndView.addObject("groupedImageCommands", new TreeMap<String, List<ImageCommand>>(grouped));

		if (grouped.isEmpty()) {
			messageUtil.addInfoMsg(modelMap, "image.gallery.msg.noImages");
		}

		return modelAndView;
	}

	@RequestMapping(value = "/show/{id}", method = RequestMethod.GET, produces = MediaType.IMAGE_JPEG_VALUE)
	public ResponseEntity<byte[]> showImage(@PathVariable("id") Long imageId, WebRequest webRequest) {
		return createResponseEntityForImageId(imageId, webRequest,
				imageAdministrationService -> imageAdministrationService.loadImageById(imageId));
	}

	@RequestMapping(value = "/showthumb/{id}", method = RequestMethod.GET, produces = MediaType.IMAGE_JPEG_VALUE)
	public ResponseEntity<byte[]> showThumbnail(@PathVariable("id") Long imageId, WebRequest webRequest) {
		return createResponseEntityForImageId(imageId, webRequest,
				imageAdministrationService -> imageAdministrationService.loadThumbnailById(imageId));
	}

	private ResponseEntity<byte[]> createResponseEntityForImageId(Long imageId, WebRequest webRequest,
			ImageLoadingCallback imageLoadingCallback) {
		final String etag = "" + imageId;
		boolean notModified = webRequest.checkNotModified(etag);
		if (notModified) {
			LOG.debug("image with id={} and etag={} has not been modified.", imageId, etag);
			return ResponseEntity.status(HttpStatus.NOT_MODIFIED).cacheControl(CacheControl.maxAge(1, TimeUnit.DAYS)).eTag(etag).body(null);
		}

		final BinaryImage binaryImage = imageLoadingCallback.loadImage(imageAdministrationService);

		if (binaryImage == null) {
			return new ResponseEntity<byte[]>(HttpStatus.NOT_FOUND);
		}

		return ResponseEntity.ok().eTag(etag).header("Content-Type", MediaType.IMAGE_JPEG_VALUE).body(binaryImage.getImageBinary());
	}

	@RequestMapping(value = "/download/all", method = RequestMethod.GET, produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
	public ResponseEntity<byte[]> downloadAllImagesAsZip(HttpServletResponse response) {
		final String zipFileName = "allImages.zip";
		byte[] zipFile = downloadService.downloadAllImagesAsZipFile();
		if (zipFile == null) {
			return new ResponseEntity<byte[]>(HttpStatus.NOT_FOUND);
		}

		return ResponseEntity.ok().header("Content-Type", MediaType.APPLICATION_OCTET_STREAM_VALUE)
				.header("Content-Disposition", "inline; filename=\"" + zipFileName + "\"").body(zipFile);
	}

	@FunctionalInterface
	interface ImageLoadingCallback {

		BinaryImage loadImage(ImageAdministrationService imageAdministrationService);
	}
}
