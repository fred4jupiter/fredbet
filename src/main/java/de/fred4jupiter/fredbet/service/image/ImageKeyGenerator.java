package de.fred4jupiter.fredbet.service.image;

import java.util.UUID;

import org.springframework.stereotype.Component;

@Component
public class ImageKeyGenerator {

	public String generateKey() {
		return UUID.randomUUID().toString();
	}
}
