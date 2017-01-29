package de.fred4jupiter.fredbet.service.image;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import de.fred4jupiter.fredbet.FredbetProperties;
import de.fred4jupiter.fredbet.ImageLocation;
import de.fred4jupiter.fredbet.repository.ImageBinaryRepository;

@Component
public class ImageLocationServiceFactory implements FactoryBean<ImageLocationService>, InitializingBean {

	private static final Logger LOG = LoggerFactory.getLogger(ImageLocationServiceFactory.class);

	private ImageLocationService imageLocationService;

	@Autowired
	private ImageBinaryRepository imageBinaryRepository;

	@Autowired
	private FredbetProperties fredbetProperties;

	@Override
	public ImageLocationService getObject() throws Exception {
		return imageLocationService;
	}

	@Override
	public Class<?> getObjectType() {
		return ImageLocationService.class;
	}

	@Override
	public boolean isSingleton() {
		return true;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		ImageLocation imageLocation = fredbetProperties.getImageLocation();
		LOG.info("Using image location: {}", imageLocation);
		switch (imageLocation) {
		case FILE_SYSTEM:
			LOG.info("Storing image in file system at: {}", fredbetProperties.getImageFileSytemBaseFolder());
			this.imageLocationService = new FilesystemImageLocationService(
					fredbetProperties.getImageFileSytemBaseFolder());
			break;
		case DATABASE:
			this.imageLocationService = new DatabaseImageLocationService(imageBinaryRepository);
			break;
		default:
			throw new IllegalStateException("Unsupported image location: " + imageLocation);
		}
	}

}
