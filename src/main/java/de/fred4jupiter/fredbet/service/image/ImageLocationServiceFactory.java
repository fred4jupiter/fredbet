package de.fred4jupiter.fredbet.service.image;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import de.fred4jupiter.fredbet.repository.ImageBinaryRepository;

@Component
public class ImageLocationServiceFactory implements FactoryBean<ImageLocationService>, InitializingBean {

	private ImageLocationService imageLocationService;

	@Autowired
	private ImageBinaryRepository imageBinaryRepository;

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
		// TODO: make configurable
//		this.imageLocationService = new DatabaseImageLocationService(imageBinaryRepository);
		this.imageLocationService = new FilesystemImageLocationService();
	}

}
