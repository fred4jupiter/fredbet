package de.fred4jupiter.fredbet.service.image;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import javax.imageio.ImageIO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.fred4jupiter.fredbet.FredbetProperties;
import de.fred4jupiter.fredbet.web.image.Rotation;
import net.coobird.thumbnailator.Thumbnails;
import net.coobird.thumbnailator.Thumbnails.Builder;
import net.coobird.thumbnailator.geometry.Positions;

/**
 * For resizing images and thumbnail generation.
 * 
 * @author michael
 *
 */
@Service
class ImageResizingService {

	private final int thumbnailSize;

	private final int imageSize;

	private static final Logger log = LoggerFactory.getLogger(ImageResizingService.class);

	@Autowired
	public ImageResizingService(FredbetProperties fredbetProperties) {
		this.thumbnailSize = fredbetProperties.getThumbnailSize();
		this.imageSize = fredbetProperties.getImageSize();
	}

	public byte[] createThumbnail(byte[] imageBinary) {
		return createThumbnail(imageBinary, Rotation.NONE);
	}

	public byte[] createThumbnail(byte[] imageBinary, Rotation rotation) {
		return minimizeToSize(imageBinary, thumbnailSize, rotation,
				builder -> builder.crop(Positions.CENTER).size(thumbnailSize, thumbnailSize));
	}

	public byte[] minimizeToDefaultSize(byte[] imageBinary) {
		return minimizeToDefaultSize(imageBinary, Rotation.NONE);
	}

	public byte[] minimizeToDefaultSize(byte[] imageBinary, Rotation rotation) {
		return minimizeToSize(imageBinary, imageSize, rotation);
	}

	public byte[] minimizeToSize(byte[] imageBinary, int size, Rotation rotation) {
		return minimizeToSize(imageBinary, size, rotation, builder -> builder.size(size, size));
	}

	public byte[] minimizeToSize(byte[] imageBinary, int size, Rotation rotation,
			ThumbnailsBuilderCallback byteArrayConverter) {
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

			if (Rotation.LEFT.equals(rotation)) {
				builder.rotate(-90);
			} else if (Rotation.RIGHT.equals(rotation)) {
				builder.rotate(90);
			}

			builder.outputFormat("JPEG").toOutputStream(byteOut);
			return byteOut.toByteArray();
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return null;
		}
	}

	@FunctionalInterface
	private static interface ThumbnailsBuilderCallback {

		void doWithBuilder(Builder<BufferedImage> builder);
	}
}
