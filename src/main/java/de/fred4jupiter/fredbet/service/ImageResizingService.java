package de.fred4jupiter.fredbet.service;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import javax.imageio.ImageIO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.fred4jupiter.fredbet.FredbetProperties;
import net.coobird.thumbnailator.Thumbnails;
import net.coobird.thumbnailator.Thumbnails.Builder;
import net.coobird.thumbnailator.geometry.Positions;

@Service
public class ImageResizingService {

	private final int thumbnailSize;

	private final int imageSize;

	private static final Logger log = LoggerFactory.getLogger(ImageResizingService.class);

	@Autowired
	public ImageResizingService(FredbetProperties fredbetProperties) {
		this.thumbnailSize = fredbetProperties.getThumbnailSize();
		this.imageSize = fredbetProperties.getImageSize();
	}
	
	public byte[] createThumbnail(byte[] imageBinary) {
		return minimizeToSize(imageBinary, thumbnailSize, builder -> builder.crop(Positions.CENTER).size(thumbnailSize, thumbnailSize));
	}

	public byte[] minimizeToDefaultSize(byte[] imageBinary) {
		return minimizeToSize(imageBinary, imageSize);
	}

	public byte[] minimizeToSize(byte[] imageBinary, int size) {
		return minimizeToSize(imageBinary, size, builder -> builder.size(size, size));
	}
	
	public byte[] minimizeToSize(byte[] imageBinary, int size, ThumbnailsBuilderCallback byteArrayConverter) {
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
	private static interface ThumbnailsBuilderCallback {
		
		void doWithBuilder(Builder<BufferedImage> builder);
	}
}
