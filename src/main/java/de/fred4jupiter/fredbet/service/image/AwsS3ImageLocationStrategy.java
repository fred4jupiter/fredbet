package de.fred4jupiter.fredbet.service.image;

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

public class AwsS3ImageLocationStrategy implements ImageLocationStrategy {

    private static final Logger LOG = LoggerFactory.getLogger(AwsS3ImageLocationStrategy.class);

    private final AmazonS3ClientWrapper amazonS3ClientWrapper;

    public AwsS3ImageLocationStrategy(AmazonS3ClientWrapper amazonS3ClientWrapper) {
        this.amazonS3ClientWrapper = amazonS3ClientWrapper;
    }

    @Override
    public void saveImage(String imageKey, String imageGroup, byte[] imageBinary, byte[] thumbImageBinary) {
    	amazonS3ClientWrapper.uploadFile(createKeyForImage(imageKey, imageGroup), imageBinary);
        amazonS3ClientWrapper.uploadFile(createKeyForThumbnail(imageKey, imageGroup), thumbImageBinary);
    }

    private String createKeyForThumbnail(String imageKey, String imageGroup) {
        return imageGroup + "/" + THUMBNAIL_PREFIX + imageKey;
    }

    private String createKeyForImage(String imageKey, String imageGroup) {
        return imageGroup + "/" + IMAGE_PREFIX + imageKey;
    }

    @Override
    public ImageData getImageDataByKey(String imageKey, String imageGroup) {
    	byte[] imageByte = amazonS3ClientWrapper.downloadFile(createKeyForImage(imageKey, imageGroup));
        byte[] thumbByte = amazonS3ClientWrapper.downloadFile(createKeyForThumbnail(imageKey, imageGroup));
        return new BinaryImageData(imageKey, imageByte, thumbByte);
    }

    @Override
    public List<ImageData> findAllImages() {
        List<ImageData> resultList = new ArrayList<>();

        List<Resource> allImagesInBucket = amazonS3ClientWrapper.readAllImagesInBucket();

        Map<String, byte[]> imagesMap = new HashMap<>();
        Map<String, byte[]> thumbsMap = new HashMap<>();

        readImages(allImagesInBucket, imagesMap, thumbsMap);

        for (String imageKey : imagesMap.keySet()) {
            resultList.add(new BinaryImageData(imageKey, imagesMap.get(imageKey), thumbsMap.get(imageKey)));
        }

        return resultList;
    }

    private void readImages(List<Resource> allImagesInBucket, Map<String, byte[]> imagesMap, Map<String, byte[]> thumbsMap) {
        for (Resource resource : allImagesInBucket) {
            String filename = resource.getFilename();
            String imageKey = toImageKey(filename);
            byte[] fileContent = toByteArray(resource);
            if (fileContent != null && fileContent.length > 0) {
                if (filename.startsWith(IMAGE_PREFIX)) {
                    imagesMap.put(imageKey, fileContent);
                } else if (filename.startsWith(THUMBNAIL_PREFIX)) {
                    thumbsMap.put(imageKey, fileContent);
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

    @Override
    public void deleteImage(String imageKey, String imageGroup) {
        amazonS3ClientWrapper.removeFile(createKeyForImage(imageKey, imageGroup));
        amazonS3ClientWrapper.removeFile(createKeyForThumbnail(imageKey, imageGroup));
    }

}
