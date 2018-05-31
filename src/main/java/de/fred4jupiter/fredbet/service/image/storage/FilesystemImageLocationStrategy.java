package de.fred4jupiter.fredbet.service.image.storage;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.fred4jupiter.fredbet.props.FredbetConstants;
import de.fred4jupiter.fredbet.service.image.BinaryImage;

/**
 * Storing images of the image gallery in file system.
 * 
 * @author michael
 *
 */
public class FilesystemImageLocationStrategy implements ImageLocationStrategy {

	private static final Logger LOG = LoggerFactory.getLogger(FilesystemImageLocationStrategy.class);

	private final String basePath;

	public FilesystemImageLocationStrategy() {
		this(null);
	}

	public FilesystemImageLocationStrategy(String basePath) {
		if (StringUtils.isBlank(basePath)) {
			this.basePath = System.getProperty("user.home") + File.separator + "fredbet_images";
		} else {
			this.basePath = basePath;
		}
		LOG.info("Storing images at location: {}", this.basePath);
	}

	@Override
	public void saveImage(String imageKey, Long imageGroupId, byte[] imageBinary, byte[] thumbImageBinary) {
		writeByteArrayToFile(getImageFileForGroup(imageGroupId, imageKey), imageBinary);
		writeByteArrayToFile(getThumbnailFileForGroup(imageGroupId, imageKey), thumbImageBinary);
	}

	@Override
	public List<BinaryImage> findAllImages() {
		File imageFolder = new File(basePath);
		return readFilesToImageData(imageFolder);
	}

	@Override
	public BinaryImage getImageByKey(String imageKey, Long imageGroupId) {
		byte[] imageBytes = readToByteArray(getImageFileForGroup(imageGroupId, imageKey));
		return new BinaryImage(imageKey, imageBytes);
	}

	@Override
	public BinaryImage getThumbnailByKey(String imageKey, Long imageGroupId) {
		byte[] imageBytes = readToByteArray(getThumbnailFileForGroup(imageGroupId, imageKey));
		return new BinaryImage(imageKey, imageBytes);
	}

	@Override
	public void deleteImage(String imageKey, Long imageGroupId) {
		getImageFileForGroup(imageGroupId, imageKey).delete();
		getThumbnailFileForGroup(imageGroupId, imageKey).delete();
	}

	private File getImageFileForGroup(Long imageGroupId, String imageKey) {
		return getFileFor(imageGroupId, imageKey, IMAGE_PREFIX);
	}

	private File getThumbnailFileForGroup(Long imageGroupId, String imageKey) {
		return getFileFor(imageGroupId, imageKey, THUMBNAIL_PREFIX);
	}

	private File getFileFor(Long imageGroupId, String imageKey, String filePrefix) {
		return new File(basePath + File.separator + imageGroupId + File.separator + filePrefix + imageKey + FredbetConstants.IMAGE_JPG_EXTENSION);
	}

	private List<BinaryImage> readFilesToImageData(File imageFolder) {
		List<BinaryImage> resultList = new ArrayList<>();

		Map<String, byte[]> imagesMap = new HashMap<>();

		readImageInFolder(imageFolder, imagesMap);

		for (String imageKey : imagesMap.keySet()) {
			resultList.add(new BinaryImage(imageKey, imagesMap.get(imageKey)));
		}

		return resultList;
	}

	private void readImageInFolder(File imageFolder, Map<String, byte[]> imagesMap) {
		try {
			Files.walkFileTree(Paths.get(imageFolder.getAbsolutePath()), new SimpleFileVisitor<Path>() {

				@Override
				public FileVisitResult visitFile(Path path, BasicFileAttributes attrs) throws IOException {
					final File file = path.toFile();
					if (!attrs.isDirectory()) {
						String imageKey = toImageKey(file.getName());
						byte[] imageBytes = readToByteArray(file);
						if (file.getName().startsWith(IMAGE_PREFIX)) {
							imagesMap.put(imageKey, imageBytes);
						}
					}
					return FileVisitResult.CONTINUE;
				}
			});
		} catch (IOException e) {
			LOG.error(e.getMessage(), e);
			throw new FileReadWriteExcpetion(e.getMessage(), e);
		}
	}

	private String toImageKey(String fileName) {
		return FilenameUtils.removeExtension(fileName).substring(3);
	}

	private byte[] readToByteArray(File file) {
		try {
			LOG.debug("try to read file: {}", file);
			return FileUtils.readFileToByteArray(file);
		} catch (IOException e) {
			LOG.error(e.getMessage(), e);
			throw new FileReadWriteExcpetion("Could not read file: " + file);
		}
	}

	private void writeByteArrayToFile(File file, byte[] byteArray) {
		try {
			FileUtils.writeByteArrayToFile(file, byteArray);
			LOG.debug("Written file: {}", file);
		} catch (IOException e) {
			LOG.error(e.getMessage(), e);
			throw new FileReadWriteExcpetion("Could not write file: " + file);
		}
	}

}
