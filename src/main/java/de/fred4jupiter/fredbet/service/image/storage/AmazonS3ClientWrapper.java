package de.fred4jupiter.fredbet.service.image.storage;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.transfer.MultipleFileDownload;
import com.amazonaws.services.s3.transfer.TransferManager;
import com.amazonaws.services.s3.transfer.TransferManagerBuilder;

import de.fred4jupiter.fredbet.props.FredbetProperties;

/**
 * Wrapper class for the native Amazon S3 client.
 * 
 * @author michael
 *
 */
public class AmazonS3ClientWrapper {

	private static final String CONTENT_TYPE_TEXT = "text/plain";

	private static final String CONTENT_TYPE_IMAGE = "image/jpeg";

	private static final Logger LOG = LoggerFactory.getLogger(AmazonS3ClientWrapper.class);

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
			this.amazonS3.setObjectAcl(bucketName, key, CannedAccessControlList.PublicRead);
		} catch (IOException e) {
			throw new AwsS3AccessException(e.getMessage(), e);
		}
	}

	public List<File> readAllImagesInBucketWithPrefix(String prefix) {
		TransferManager transferManager = TransferManagerBuilder.standard().withS3Client(amazonS3).build();
		File tempDir = new File(System.getProperty("java.io.tmpdir") + File.separator + UUID.randomUUID().toString());
		MultipleFileDownload multipleFileDownload = transferManager.downloadDirectory(bucketName, prefix, tempDir);

		LOG.debug("Downloading files to {}", tempDir.getAbsolutePath());
		while (!multipleFileDownload.isDone()) {
			// LOG.debug("Downloading... {}",
			// multipleFileDownload.getProgress().getPercentTransferred());
		}

		Path path = Paths.get(tempDir.getAbsolutePath());
		File[] files = path.toFile().listFiles(file -> file.isFile() && file.getName().endsWith(".jpg"));
		if (files == null || files.length == 0) {
			return Collections.emptyList();
		}
		return Arrays.asList(files);
	}

}
