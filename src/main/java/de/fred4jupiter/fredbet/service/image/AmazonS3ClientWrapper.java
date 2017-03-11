package de.fred4jupiter.fredbet.service.image;

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
import com.amazonaws.services.s3.transfer.TransferManager;

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

	public void uploadFile(String key, byte[] fileContent)  {
		TransferManager transferManager = new TransferManager(this.amazonS3);

		ObjectMetadata objectMetadata = new ObjectMetadata();
		objectMetadata.setContentLength(fileContent.length);

		try (ByteArrayInputStream byteIn = new ByteArrayInputStream(fileContent)) {
			transferManager.upload(bucketName, key, byteIn, objectMetadata);
		}
		catch (IOException e) {
			throw new AwsS3AccessException(e.getMessage(), e);
		}
	}

	public void uploadFile(String key, byte[] fileContent, String contentType)  {
		try (ByteArrayInputStream byteIn = new ByteArrayInputStream(fileContent)) {
			ObjectMetadata meta = new ObjectMetadata();
			meta.setContentLength(fileContent.length);
			meta.setContentType(contentType);
			this.amazonS3.putObject(bucketName, key, byteIn, meta);
			this.amazonS3.setObjectAcl(bucketName, key, CannedAccessControlList.PublicRead);
		}
		catch (IOException e) {
			throw new AwsS3AccessException(e.getMessage(), e);
		}
	}

	public List<Resource> readAllImagesInBucket() {
		try {
			Resource[] resources = this.resourcePatternResolver.getResources(S3_PREFIX + bucketName + "/**/*.jpg");
			return resources != null ? Arrays.asList(resources) : Collections.emptyList();
		} catch (IOException e) {
			LOG.error(e.getMessage(), e);
			return Collections.emptyList();
		}
	}

	// public List<PutObjectResult> upload(MultipartFile[] multipartFiles) {
	//
	// List<PutObjectResult> putObjectResults = new ArrayList<>();
	// Stream<MultipartFile> filter = Arrays.stream(multipartFiles)
	// .filter(multipartFile ->
	// !StringUtils.isEmpty(multipartFile.getOriginalFilename()));
	// filter.forEach(multipartFile -> {
	// try {
	// putObjectResults.add(upload(multipartFile.getInputStream(),
	// multipartFile.getOriginalFilename()));
	// } catch (IOException e) {
	// LOG.error(e.getMessage(), e);
	// }
	// });
	//
	// return putObjectResults;
	// }

	// public ResponseEntity<byte[]> download(String key) throws IOException {
	// GetObjectRequest getObjectRequest = new GetObjectRequest(bucketName,
	// key);
	//
	// S3Object s3Object = amazonS3.getObject(getObjectRequest);
	//
	// S3ObjectInputStream objectInputStream = s3Object.getObjectContent();
	//
	// byte[] bytes = IOUtils.toByteArray(objectInputStream);
	//
	// String fileName = URLEncoder.encode(key, "UTF-8").replaceAll("\\+",
	// "%20");
	//
	// HttpHeaders httpHeaders = new HttpHeaders();
	// httpHeaders.setContentType(MediaType.APPLICATION_OCTET_STREAM);
	// httpHeaders.setContentLength(bytes.length);
	// httpHeaders.setContentDispositionFormData("attachment", fileName);
	//
	// return new ResponseEntity<>(bytes, httpHeaders, HttpStatus.OK);
	// }

	// public List<S3ObjectSummary> list() {
	// ObjectListing objectListing = amazonS3.listObjects(new
	// ListObjectsRequest().withBucketName(bucketName));
	// return objectListing.getObjectSummaries();
	// }

	// private PutObjectResult upload(String filePath, String uploadKey) throws
	// FileNotFoundException {
	// return upload(new FileInputStream(filePath), uploadKey);
	// }
	//
	// private PutObjectResult upload(InputStream inputStream, String uploadKey)
	// {
	// PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName,
	// uploadKey, inputStream, new ObjectMetadata());
	//
	// putObjectRequest.setCannedAcl(CannedAccessControlList.PublicRead);
	//
	// PutObjectResult putObjectResult = amazonS3.putObject(putObjectRequest);
	//
	// IOUtils.closeQuietly(inputStream);
	//
	// return putObjectResult;
	// }
}
