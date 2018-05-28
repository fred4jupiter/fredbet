package de.fred4jupiter.fredbet.service.image.storage;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.springframework.core.io.Resource;

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

	private final AmazonS3 amazonS3;

	private final String bucketName;

	public AmazonS3ClientWrapper(String accessKey, String secretKey, String region, String bucketName) {
		this.bucketName = bucketName;
		AWSCredentials credentials = new BasicAWSCredentials(accessKey, secretKey);
		AWSCredentialsProvider provider = new AWSStaticCredentialsProvider(credentials);
		this.amazonS3 = AmazonS3ClientBuilder.standard().withRegion(region).withCredentials(provider).build();
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
		TransferManager transferManager = TransferManagerBuilder.standard().withS3Client(amazonS3).build();
		File downloadDir = new File(System.getProperty("user.home"));
		MultipleFileDownload multipleFileDownload = transferManager.downloadDirectory(bucketName, prefix, downloadDir);

		while (multipleFileDownload.isDone()) {
			// TODO: read files
		}

		return Collections.emptyList();

		// try {
		// String locationPattern = S3_PREFIX + bucketName + "/**/" + prefix +
		// "*";
		// Resource[] resources =
		// this.resourcePatternResolver.getResources(locationPattern);
		// if (resources == null || resources.length == 0) {
		// LOG.warn("Could not find any images at locationPattern={} in S3.",
		// locationPattern);
		// return Collections.emptyList();
		// }
		// return Arrays.asList(resources);
		// } catch (IOException e) {
		// LOG.error(e.getMessage(), e);
		// return Collections.emptyList();
		// }
	}

}
