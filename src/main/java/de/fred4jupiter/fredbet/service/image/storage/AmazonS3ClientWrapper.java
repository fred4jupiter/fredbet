package de.fred4jupiter.fredbet.service.image.storage;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.ResourcePatternResolver;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;

/**
 * Wrapper class for the native Amazon S3 client.
 * 
 * @author michael
 *
 */
public class AmazonS3ClientWrapper {

	private static final Logger LOG = LoggerFactory.getLogger(AmazonS3ClientWrapper.class);

	private static final String S3_PREFIX = "s3://";

	private final AmazonS3 amazonS3;

	private final String bucketName;

	private final ResourceLoader resourceLoader;

	private final ResourcePatternResolver resourcePatternResolver;

	public AmazonS3ClientWrapper(AmazonS3 amazonS3, String bucketName, ResourceLoader resourceLoader,
			ResourcePatternResolver resourcePatternResolver) {
		this.amazonS3 = amazonS3;
		this.bucketName = bucketName;
		this.resourceLoader = resourceLoader;
		this.resourcePatternResolver = resourcePatternResolver;
	}

	public byte[] downloadFile(String key) {
		Resource resource = this.resourceLoader.getResource(S3_PREFIX + bucketName + "/" + key);

		try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
			IOUtils.copy(resource.getInputStream(), out);
			return out.toByteArray();
		} catch (IOException e) {
			throw new AwsS3AccessException(e.getMessage(), e);
		}
	}

	public void removeFile(String key) {
		this.amazonS3.deleteObject(bucketName, key);
	}

	public void uploadImageFile(String key, byte[] fileContent) {
		uploadFile(key, fileContent, "image/jpeg");
	}

	public void uploadFile(String key, byte[] fileContent, String contentType) {
		try (ByteArrayInputStream byteIn = new ByteArrayInputStream(fileContent)) {
			ObjectMetadata meta = new ObjectMetadata();
			meta.setContentLength(fileContent.length);
			meta.setContentType(contentType);
			this.amazonS3.putObject(bucketName, key, byteIn, meta);
			this.amazonS3.setObjectAcl(bucketName, key, CannedAccessControlList.PublicRead);
		} catch (IOException e) {
			throw new AwsS3AccessException(e.getMessage(), e);
		}
	}

	public List<Resource> readAllImagesInBucketWithPrefix(String prefix) {
		try {
			String locationPattern = S3_PREFIX + bucketName + "/**/" + prefix + "*";
			Resource[] resources = this.resourcePatternResolver.getResources(locationPattern);
			if (resources == null || resources.length == 0) {
				LOG.warn("Could not find any images at locationPattern={} in S3.", locationPattern);
				return Collections.emptyList();
			}
			return Arrays.asList(resources);
		} catch (IOException e) {
			LOG.error(e.getMessage(), e);
			return Collections.emptyList();
		}
	}

}
