package de.fred4jupiter.fredbet.web.image;

import de.fred4jupiter.fredbet.domain.ImageMetaData;
import de.fred4jupiter.fredbet.service.image.BinaryImage;
import de.fred4jupiter.fredbet.service.image.DownloadService;
import de.fred4jupiter.fredbet.service.image.ImageAdministrationService;
import de.fred4jupiter.fredbet.web.WebMessageUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.request.WebRequest;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/gallery")
public class ImageGalleryController {

    private static final Logger LOG = LoggerFactory.getLogger(ImageGalleryController.class);

    private static final String PAGE_IMAGE_GALLERY = "image/gallery";

    @Autowired
    private WebMessageUtil messageUtil;

    @Autowired
    private ImageAdministrationService imageAdministrationService;

    @Autowired
    private DownloadService downloadService;

    @Autowired
    private ImageCommandMapper imageCommandMapper;

    @GetMapping("/show")
    public String showGallery(Model model) {
        List<ImageMetaData> images = imageAdministrationService.fetchAllImages();
        List<ImageCommand> imageCommands = imageCommandMapper.toListOfImageCommand(images);

        Map<String, List<ImageCommand>> grouped = imageCommands.stream().collect(Collectors.groupingBy(ImageCommand::getGalleryGroup));

        model.addAttribute("groupedImageCommands", new TreeMap<>(grouped));

        if (grouped.isEmpty()) {
            messageUtil.addInfoMsg(model, "image.gallery.msg.noImages");
        }

        return PAGE_IMAGE_GALLERY;
    }

    @GetMapping(value = "/show/{imageKey}", produces = MediaType.IMAGE_JPEG_VALUE)
    public ResponseEntity<byte[]> showImage(@PathVariable("imageKey") String imageKey, WebRequest webRequest) {
        return createResponseEntityForImageId(imageKey, webRequest,
                imageAdministrationService -> imageAdministrationService.loadImageByImageKey(imageKey));
    }

    @GetMapping(value = "/showthumb/{imageKey}", produces = MediaType.IMAGE_JPEG_VALUE)
    public ResponseEntity<byte[]> showThumbnail(@PathVariable("imageKey") String imageKey, WebRequest webRequest) {
        return createResponseEntityForImageId(imageKey, webRequest,
                imageAdministrationService -> imageAdministrationService.loadThumbnailByImageKey(imageKey));
    }

    @GetMapping(value = "/download/all", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public ResponseEntity<byte[]> downloadAllImagesAsZip() {
        final String zipFileName = "allImages.zip";
        byte[] zipFile = downloadService.downloadAllImagesAsZipFile();
        if (zipFile == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok().header("Content-Type", MediaType.APPLICATION_OCTET_STREAM_VALUE)
                .header("Content-Disposition", "inline; filename=\"" + zipFileName + "\"").body(zipFile);
    }

    private ResponseEntity<byte[]> createResponseEntityForImageId(String imageKey, WebRequest webRequest,
                                                                  ImageLoadingCallback imageLoadingCallback) {
        final String etag = "" + imageKey;
        boolean notModified = webRequest.checkNotModified(etag);
        if (notModified) {
            LOG.debug("image with imageKey={} and etag={} has not been modified.", imageKey, etag);
            return ResponseEntity.status(HttpStatus.NOT_MODIFIED).cacheControl(CacheControl.maxAge(1, TimeUnit.DAYS)).eTag(etag).body(null);
        }

        final BinaryImage binaryImage = imageLoadingCallback.loadImage(imageAdministrationService);

        if (binaryImage == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return ResponseEntity.ok().eTag(etag).header("Content-Type", MediaType.IMAGE_JPEG_VALUE).body(binaryImage.getImageBinary());
    }

    @FunctionalInterface
    interface ImageLoadingCallback {

        BinaryImage loadImage(ImageAdministrationService imageAdministrationService);
    }
}
