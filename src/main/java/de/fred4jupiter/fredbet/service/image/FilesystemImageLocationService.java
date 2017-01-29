package de.fred4jupiter.fredbet.service.image;

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

public class FilesystemImageLocationService implements ImageLocationService {

	private static final String THUMBNAIL_PREFIX = "TN_";

	private static final String IMAGE_PREFIX = "IM_";

	private static final Logger LOG = LoggerFactory.getLogger(FilesystemImageLocationService.class);

	private final String basePath;

	public FilesystemImageLocationService() {
		this(null);
	}
	
	public FilesystemImageLocationService(String basePath) {
		if (StringUtils.isBlank(basePath)) {
			this.basePath = System.getProperty("java.io.tmpdir") + File.separator + "fredbet_images";
		} else {
			this.basePath = basePath;
		}
	}

	@Override
	public void saveImage(String imageKey, String imageGroup, byte[] imageBinary, byte[] thumbImageBinary) {
		writeByteArrayToFile(getImageFileForGroup(imageGroup, imageKey), imageBinary);
		writeByteArrayToFile(getThumbnailFileForGroup(imageGroup, imageKey), thumbImageBinary);
	}

	@Override
	public List<ImageData> findAllImages() {
		File imageFolder = new File(basePath);
		return readFilesToImageData(imageFolder);
	}

	@Override
	public ImageData getImageDataByKey(String imageKey, String imageGroup) {
		return readToBinaryImageData(imageKey, getImageFileForGroup(imageGroup, imageKey),
				getThumbnailFileForGroup(imageGroup, imageKey));
	}

	@Override
	public void deleteImage(String imageKey, String imageGroup) {
		getImageFileForGroup(imageGroup, imageKey).delete();
		getThumbnailFileForGroup(imageGroup, imageKey).delete();
	}

	private File getImageFileForGroup(String imageGroup, String imageKey) {
		return getFileFor(imageGroup, imageKey, IMAGE_PREFIX);
	}

	private File getThumbnailFileForGroup(String imageGroup, String imageKey) {
		return getFileFor(imageGroup, imageKey, THUMBNAIL_PREFIX);
	}

	private File getFileFor(String imageGroup, String imageKey, String filePrefix) {
		return new File(basePath + File.separator + imageGroup + File.separator + filePrefix + imageKey + ".jpg");
	}

	private List<ImageData> readFilesToImageData(File imageFolder) {
		List<ImageData> resultList = new ArrayList<>();

		Map<String, byte[]> imagesMap = new HashMap<>();
		Map<String, byte[]> thumbsMap = new HashMap<>();

		readImages(imageFolder, imagesMap, IMAGE_PREFIX);
		readImages(imageFolder, thumbsMap, THUMBNAIL_PREFIX);

		for (String imageKey : imagesMap.keySet()) {
			resultList.add(new BinaryImageData(imageKey, imagesMap.get(imageKey), thumbsMap.get(imageKey)));
		}

		return resultList;
	}

	private void readImages(File imageFolder, Map<String, byte[]> someMap, String filePrefix) {
		try {
			Files.walkFileTree(Paths.get(imageFolder.getAbsolutePath()), new SimpleFileVisitor<Path>() {
				@Override
				public FileVisitResult visitFile(Path path, BasicFileAttributes attrs) throws IOException {
					final File file = path.toFile();
					if (!attrs.isDirectory() && file.getName().startsWith(filePrefix)) {
						String imageKey = toImageKey(file.getName());
						byte[] imageBytes = readToByteArray(file);
						someMap.put(imageKey, imageBytes);
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
		String fileNameWithoutExtension = FilenameUtils.removeExtension(fileName);
		return fileNameWithoutExtension.substring(3);
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

	private BinaryImageData readToBinaryImageData(String imageKey, File imageFile, File thumbFile) {
		byte[] imageBytes = readToByteArray(imageFile);
		byte[] thumbBytes = readToByteArray(thumbFile);
		return new BinaryImageData(imageKey, imageBytes, thumbBytes);
	}

}
