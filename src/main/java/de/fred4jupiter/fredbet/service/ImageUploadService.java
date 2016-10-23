package de.fred4jupiter.fredbet.service;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

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

	public List<String> fetchAllImagesAsBase64() {
		List<FileStorage> allSavedImages = fileStorageRepository.findAll();

		List<String> allImagesAsBase64 = new ArrayList<>();

		for (FileStorage fileStorage : allSavedImages) {
			byte[] imageBinary = fileStorage.getImageBinary();
			String encodeToString = Base64.getEncoder().encodeToString(imageBinary);
			allImagesAsBase64.add(encodeToString);
		}

		return allImagesAsBase64;
	}
}
