package de.fred4jupiter.fredbet.service.image.storage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;

import de.fred4jupiter.fredbet.service.image.BinaryImage;

public class AwsS3ImageLocationStrategy implements ImageLocationStrategy {

	private static final Logger LOG = LoggerFactory.getLogger(AwsS3ImageLocationStrategy.class);

	private final AmazonS3ClientWrapper amazonS3ClientWrapper;

	public AwsS3ImageLocationStrategy(AmazonS3ClientWrapper amazonS3ClientWrapper) {
		this.amazonS3ClientWrapper = amazonS3ClientWrapper;
	}

	@Override
	public void saveImage(String imageKey, String imageGroup, byte[] imageBinary, byte[] thumbImageBinary) {
		LOG.debug("saving image in S3. imageKey={}, imageGroup={}", imageKey, imageGroup);
		amazonS3ClientWrapper.uploadImageFile(createKeyForImage(imageKey, imageGroup), imageBinary);
		amazonS3ClientWrapper.uploadImageFile(createKeyForThumbnail(imageKey, imageGroup), thumbImageBinary);
	}

	private String createKeyForThumbnail(String imageKey, String imageGroup) {
		return imageGroup + "/" + THUMBNAIL_PREFIX + imageKey;
	}

	private String createKeyForImage(String imageKey, String imageGroup) {
		return imageGroup + "/" + IMAGE_PREFIX + imageKey;
	}

	@Override
	public BinaryImage getImageByKey(String imageKey, String imageGroup) {
		LOG.debug("loading image from S3. imageKey={}, imageGroup={}", imageKey, imageGroup);
		byte[] imageByte = amazonS3ClientWrapper.downloadFile(createKeyForImage(imageKey, imageGroup));
		return new BinaryImage(imageKey, imageByte);
	}

	@Override
	public BinaryImage getThumbnailByKey(String imageKey, String imageGroup) {
		LOG.debug("loading thumbnail from S3. imageKey={}, imageGroup={}", imageKey, imageGroup);
		byte[] imageByte = amazonS3ClientWrapper.downloadFile(createKeyForThumbnail(imageKey, imageGroup));
		return new BinaryImage(imageKey, imageByte);
	}

	@Override
	public List<BinaryImage> findAllImages() {
		LOG.debug("loading all images from S3.");
		List<BinaryImage> resultList = new ArrayList<>();

		List<Resource> allImagesInBucket = amazonS3ClientWrapper.readAllImagesInBucket();

		Map<String, byte[]> imagesMap = new HashMap<>();

		readImages(allImagesInBucket, imagesMap);

		for (String imageKey : imagesMap.keySet()) {
			resultList.add(new BinaryImage(imageKey, imagesMap.get(imageKey)));
		}

		return resultList;
	}

	@Override
	public void deleteImage(String imageKey, String imageGroup) {
		LOG.debug("deleteting image and thumbnail for imageKey={}, imageGroup={}", imageKey, imageGroup);
		amazonS3ClientWrapper.removeFile(createKeyForImage(imageKey, imageGroup));
		amazonS3ClientWrapper.removeFile(createKeyForThumbnail(imageKey, imageGroup));
	}
	
	private void readImages(List<Resource> allImagesInBucket, Map<String, byte[]> imagesMap) {
		for (Resource resource : allImagesInBucket) {
			String filename = resource.getFilename();
			String imageKey = toImageKey(filename);
			byte[] fileContent = toByteArray(resource);
			if (fileContent != null && fileContent.length > 0) {
				if (filename.startsWith(IMAGE_PREFIX)) {
					imagesMap.put(imageKey, fileContent);
				}
			}
		}
	}

	private byte[] toByteArray(Resource resource) {
		try {
			return IOUtils.toByteArray(resource.getInputStream());
		} catch (IOException e) {
			LOG.error(e.getMessage(), e);
			return null;
		}
	}

	private String toImageKey(String fileName) {
		String fileNameWithoutExtension = FilenameUtils.removeExtension(fileName);
		return fileNameWithoutExtension.substring(3);
	}

	

}
