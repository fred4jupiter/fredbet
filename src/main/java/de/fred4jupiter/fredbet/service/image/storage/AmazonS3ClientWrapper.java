package de.fred4jupiter.fredbet.service.image.storage;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.Assert;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectSummary;

import de.fred4jupiter.fredbet.props.FredbetProperties;
import de.fred4jupiter.fredbet.service.image.BinaryImage;

/**
 * Wrapper class for the native Amazon S3 client.
 * 
 * @author michael
 *
 */
public class AmazonS3ClientWrapper {

	private static final String CONTENT_TYPE_TEXT = "text/plain";

	private static final String CONTENT_TYPE_IMAGE = "image/jpeg";

	private final AmazonS3 amazonS3;

	private final String bucketName;

	public AmazonS3ClientWrapper(String accessKey, String secretKey, String region, String bucketName) {
		Assert.notNull(region, "region must not be null");
		Assert.notNull(bucketName, "bucketName must not be null");
		this.bucketName = bucketName;

		AmazonS3ClientBuilder builder = AmazonS3ClientBuilder.standard().withRegion(region);
		if (StringUtils.isNotBlank(accessKey) && StringUtils.isNotBlank(secretKey)) {
			AWSCredentials credentials = new BasicAWSCredentials(accessKey, secretKey);
			AWSCredentialsProvider provider = new AWSStaticCredentialsProvider(credentials);
			builder.withCredentials(provider);
		}

		this.amazonS3 = builder.build();
	}

	public AmazonS3ClientWrapper(FredbetProperties fredbetProperties) {
		this(fredbetProperties.getAwsAccessKey(), fredbetProperties.getAwsSecretKey(), fredbetProperties.getAwsRegion(),
				fredbetProperties.getAwsS3bucketName());
	}

	public byte[] downloadFile(String key) {
		S3Object object = this.amazonS3.getObject(new GetObjectRequest(bucketName, key));
		try (ByteArrayOutputStream out = new ByteArrayOutputStream(); InputStream objectData = object.getObjectContent()) {
			IOUtils.copy(objectData, out);
			return out.toByteArray();
		} catch (IOException e) {
			throw new AwsS3AccessException(e.getMessage(), e);
		}
	}

	public void removeFile(String key) {
		this.amazonS3.deleteObject(bucketName, key);
	}

	public void uploadImageFile(String key, byte[] fileContent) {
		uploadFile(key, fileContent, CONTENT_TYPE_IMAGE);
	}

	public void uploadTextFile(String key, byte[] fileContent) {
		uploadFile(key, fileContent, CONTENT_TYPE_TEXT);
	}

	public void uploadFile(String key, byte[] fileContent, String contentType) {
		try (ByteArrayInputStream byteIn = new ByteArrayInputStream(fileContent)) {
			ObjectMetadata meta = new ObjectMetadata();
			meta.setContentLength(fileContent.length);
			meta.setContentType(contentType);
			this.amazonS3.putObject(bucketName, key, byteIn, meta);
		} catch (IOException e) {
			throw new AwsS3AccessException(e.getMessage(), e);
		}
	}

	public List<String> listFiles() {
		return listFiles(null);
	}

	public List<String> listFiles(String fileExtension) {
		List<String> keys = new ArrayList<>();
		ObjectListing objectListing = amazonS3.listObjects(bucketName);
		for (S3ObjectSummary s3ObjectSummary : objectListing.getObjectSummaries()) {
			String key = s3ObjectSummary.getKey();
			if (filter(key, fileExtension)) {
				keys.add(key);
			}
		}
		return keys;
	}

	private boolean filter(String key, String fileExtension) {
		if (fileExtension == null) {
			// no filtering
			return true;
		}

		if (key.endsWith(fileExtension)) {
			return true;
		}
		return false;
	}

	/**
	 * Prefix is like a directory name.
	 * 
	 * @param prefix
	 * @return
	 */
	public List<BinaryImage> downloadAllFiles(List<String> keys) {
		List<BinaryImage> resultList = new ArrayList<>();

		for (String key : keys) {
			byte[] downloadFile = downloadFile(key);
			if (downloadFile != null) {
				resultList.add(new BinaryImage(key, downloadFile));
			}
		}

		return resultList;
	}

}
