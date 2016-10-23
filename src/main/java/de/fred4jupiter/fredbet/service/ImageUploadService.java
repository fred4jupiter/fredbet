package de.fred4jupiter.fredbet.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import de.fred4jupiter.fredbet.domain.FileStorage;
import de.fred4jupiter.fredbet.repository.FileStorageRepository;

@Service
@Transactional
public class ImageUploadService {

	@Autowired
	private FileStorageRepository fileStorageRepository;

	public void saveImageInDatabase(String fileName, byte[] binary) {
		FileStorage fileStorage = new FileStorage(fileName, binary);
		fileStorageRepository.save(fileStorage);
	}
}
