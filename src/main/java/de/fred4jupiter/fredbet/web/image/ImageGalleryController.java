package de.fred4jupiter.fredbet.web.image;

import java.io.ByteArrayInputStream;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
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

import de.fred4jupiter.fredbet.service.image.DownloadService;
import de.fred4jupiter.fredbet.service.image.ImageAdministrationService;
import de.fred4jupiter.fredbet.service.image.ImageData;
import de.fred4jupiter.fredbet.web.MessageUtil;

@Controller
@RequestMapping("/gallery")
public class ImageGalleryController {

	private static final Logger log = LoggerFactory.getLogger(ImageGalleryController.class);

	@Autowired
	private MessageUtil messageUtil;

	@Autowired
	private ImageAdministrationService imageUploadService;

	@Autowired
	private DownloadService downloadService;

	@RequestMapping(value = "/show", method = RequestMethod.GET)
	public ModelAndView showGallery(ModelMap modelMap) {
		ModelAndView modelAndView = new ModelAndView("image/gallery");

		List<ImageCommand> images = imageUploadService.fetchAllImages();

		Map<String, List<ImageCommand>> grouped = images.stream()
				.collect(Collectors.groupingBy(ImageCommand::getGalleryGroup));

		modelAndView.addObject("groupedImageCommands", new TreeMap<String, List<ImageCommand>>(grouped));

		if (grouped.isEmpty()) {
			messageUtil.addInfoMsg(modelMap, "image.gallery.msg.noImages");
		}

		return modelAndView;
	}

	@RequestMapping(value = "/show/{id}", method = RequestMethod.GET, produces = MediaType.IMAGE_JPEG_VALUE)
	public ResponseEntity<byte[]> showImage(@PathVariable("id") Long imageId, WebRequest webRequest) {
		return createResponseEntityForImageId(imageId, webRequest, imageData -> imageData.getBinary());
	}

	@RequestMapping(value = "/showthumb/{id}", method = RequestMethod.GET, produces = MediaType.IMAGE_JPEG_VALUE)
	public ResponseEntity<byte[]> showThumbnail(@PathVariable("id") Long imageId, WebRequest webRequest) {
		return createResponseEntityForImageId(imageId, webRequest, imageData -> imageData.getThumbnailBinary());
	}

	private ResponseEntity<byte[]> createResponseEntityForImageId(Long imageId, WebRequest webRequest,
			ImageDataCallback imageDataCallback) {
		final String etag = "" + imageId;
		boolean notModified = webRequest.checkNotModified(etag);
		if (notModified) {
			return ResponseEntity.status(HttpStatus.NOT_MODIFIED).cacheControl(CacheControl.maxAge(1, TimeUnit.DAYS))
					.eTag(etag).body(null);
		}

		ImageData imageData = imageUploadService.loadImageById(imageId);
		if (imageData == null) {
			return new ResponseEntity<byte[]>(HttpStatus.NOT_FOUND);
		}

		return ResponseEntity.ok().cacheControl(CacheControl.maxAge(1, TimeUnit.DAYS)).eTag(etag)
				.header("Content-Type", MediaType.IMAGE_JPEG_VALUE).body(imageDataCallback.getBinaryToUse(imageData));
	}

	@RequestMapping(value = "/download/all", method = RequestMethod.GET, produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
	public ResponseEntity<byte[]> downloadAllImagesAsZip(HttpServletResponse response) {
		response.setHeader("Content-Type", MediaType.APPLICATION_OCTET_STREAM_VALUE);
		final String zipFileName = "allImages.zip";
		response.setHeader("Content-Disposition", "inline; filename=\"" + zipFileName + "\"");

		byte[] zipFile = downloadService.downloadAllImagesAsZipFile();
		return copyIntoResponse(response, zipFile);
	}

	private ResponseEntity<byte[]> copyIntoResponse(HttpServletResponse response, byte[] byteArray) {
		if (byteArray == null) {
			return new ResponseEntity<byte[]>(HttpStatus.NOT_FOUND);
		}

		try (ByteArrayInputStream in = new ByteArrayInputStream(byteArray)) {
			IOUtils.copy(in, response.getOutputStream());
			response.flushBuffer();
			return new ResponseEntity<byte[]>(HttpStatus.OK);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return new ResponseEntity<byte[]>(HttpStatus.NOT_FOUND);
		}
	}

	@FunctionalInterface
	private static interface ImageDataCallback {
		byte[] getBinaryToUse(ImageData imageData);
	}
}
