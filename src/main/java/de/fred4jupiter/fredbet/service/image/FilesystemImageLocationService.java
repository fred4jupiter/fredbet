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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FilesystemImageLocationService implements ImageLocationService {

	private static final Logger LOG = LoggerFactory.getLogger(FilesystemImageLocationService.class);

	private final String basePath = System.getProperty("java.io.tmpdir") + File.separator + "fredbet";

	@Override
	public void saveImage(String imageKey, String imageGroup, byte[] imageBinary, byte[] thumbImageBinary) {
		writeByteArrayToFile(getImageFileForGroup(imageGroup, imageKey), imageBinary);
		writeByteArrayToFile(getThumbnailFileForGroup(imageGroup, imageKey), thumbImageBinary);
	}

	private File getImageFileForGroup(String imageGroup, String imageKey) {
		return new File(basePath + File.separator + imageGroup + File.separator + "IM_" + imageKey + ".jpg");
	}

	private File getThumbnailFileForGroup(String imageGroup, String imageKey) {
		return new File(basePath + File.separator + imageGroup + File.separator + "TN_" + imageKey + ".jpg");
	}

	@Override
	public List<ImageData> findImagesInGroup(String imageGroup) {
		File imageFolder = new File(basePath + File.separator + imageGroup);
		return readFilesToImageData(imageFolder);
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

	private List<ImageData> readFilesToImageData(File imageFolder) {
		List<ImageData> resultList = new ArrayList<>();

		Map<String, byte[]> imagesMap = new HashMap<>();
		Map<String, byte[]> thumbsMap = new HashMap<>();

		readImages(imageFolder, imagesMap, "IM_");
		readImages(imageFolder, thumbsMap, "TN_");

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
					if (!attrs.isDirectory() && path.toFile().getName().startsWith(filePrefix)) {
						String imageKey = toImageKey(path.toFile().getName());
						byte[] imageBytes = readToByteArray(path.toFile());
						someMap.put(imageKey, imageBytes);
					}
					return FileVisitResult.CONTINUE;
				}
			});
		} catch (IOException e) {
			LOG.error(e.getMessage(), e);
			throw new RuntimeException(e.getMessage(), e);
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
			throw new IllegalArgumentException("Could not read file: " + file);
		}
	}

	private void writeByteArrayToFile(File file, byte[] byteArray) {
		try {
			FileUtils.writeByteArrayToFile(file, byteArray);
			LOG.debug("Written file: {}", file);
		} catch (IOException e) {
			LOG.error(e.getMessage(), e);
			throw new IllegalArgumentException("Could not write file: " + file);
		}
	}

	private BinaryImageData readToBinaryImageData(String imageKey, File imageFile, File thumbFile) {
		byte[] imageBytes = readToByteArray(imageFile);
		byte[] thumbBytes = readToByteArray(thumbFile);
		return new BinaryImageData(imageKey, imageBytes, thumbBytes);
	}

}
