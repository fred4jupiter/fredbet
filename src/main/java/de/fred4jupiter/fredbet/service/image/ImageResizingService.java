package de.fred4jupiter.fredbet.service.image;

import de.fred4jupiter.fredbet.props.FredbetProperties;
import net.coobird.thumbnailator.Thumbnails;
import net.coobird.thumbnailator.geometry.Positions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

/**
 * For resizing images and thumbnail generation.
 *
 * @author michael
 */
@Service
class ImageResizingService {

    private static final Logger LOG = LoggerFactory.getLogger(ImageResizingService.class);

    private final FredbetProperties fredbetProperties;

    public ImageResizingService(FredbetProperties fredbetProperties) {
        this.fredbetProperties = fredbetProperties;
    }

    public byte[] createThumbnail(byte[] imageBinary) {
        Integer thumbnailSize = fredbetProperties.getThumbnailSize();
        if (thumbnailSize == 0) {
            throw new IllegalArgumentException("Given thumbnailSize must be greather than 0!");
        }
        try (ByteArrayInputStream byteIn = new ByteArrayInputStream(imageBinary);
             ByteArrayOutputStream byteOut = new ByteArrayOutputStream()) {

            BufferedImage bufferedImage = ImageIO.read(byteIn);
            if (bufferedImage.getWidth() <= thumbnailSize && bufferedImage.getHeight() <= thumbnailSize) {
                LOG.info(
                        "The original image does have the prefered thumbnailSize of {} or is smaller than that. Do not change image thumbnailSize. image with: {}, image height: {}",
                        thumbnailSize, bufferedImage.getWidth(), bufferedImage.getHeight());
                return imageBinary;
            }

            Thumbnails.of(bufferedImage).crop(Positions.CENTER).size(thumbnailSize, thumbnailSize).outputFormat("JPEG").toOutputStream(byteOut);
            return byteOut.toByteArray();
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
            return null;
        }
    }

}
