package de.fred4jupiter.fredbet.service.image;

import de.fred4jupiter.fredbet.props.FredbetProperties;
import net.coobird.thumbnailator.Thumbnails;
import net.coobird.thumbnailator.Thumbnails.Builder;
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

    private static final Logger log = LoggerFactory.getLogger(ImageResizingService.class);

    private final FredbetProperties fredbetProperties;

    public ImageResizingService(FredbetProperties fredbetProperties) {
        this.fredbetProperties = fredbetProperties;
    }

    public byte[] createThumbnail(byte[] imageBinary) {
        Integer thumbnailSize = fredbetProperties.getThumbnailSize();
        return minimizeToSize(imageBinary, thumbnailSize, builder -> builder.crop(Positions.CENTER).size(thumbnailSize, thumbnailSize));
    }

    public byte[] minimizeToSize(byte[] imageBinary, int size) {
        return minimizeToSize(imageBinary, size, builder -> builder.size(size, size));
    }

    public byte[] minimizeToSize(byte[] imageBinary, int size, ThumbnailsBuilderCallback byteArrayConverter) {
        if (size == 0) {
            throw new IllegalArgumentException("Given size must be greather than 0!");
        }
        try (ByteArrayInputStream byteIn = new ByteArrayInputStream(imageBinary);
             ByteArrayOutputStream byteOut = new ByteArrayOutputStream()) {

            BufferedImage bufferedImage = ImageIO.read(byteIn);
            if (bufferedImage.getWidth() <= size && bufferedImage.getHeight() <= size) {
                log.info(
                        "The original image does have the prefered size of {} or is smaller than that. Do not change image size. image with: {}, image height: {}",
                        size, bufferedImage.getWidth(), bufferedImage.getHeight());
                return imageBinary;
            }

            Builder<BufferedImage> builder = Thumbnails.of(bufferedImage);
            byteArrayConverter.doWithBuilder(builder);

            builder.outputFormat("JPEG").toOutputStream(byteOut);
            return byteOut.toByteArray();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return null;
        }
    }

    @FunctionalInterface
    private interface ThumbnailsBuilderCallback {

        void doWithBuilder(Builder<BufferedImage> builder);
    }
}
