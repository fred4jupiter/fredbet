package de.fred4jupiter.fredbet.service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.fred4jupiter.fredbet.domain.Image;
import de.fred4jupiter.fredbet.repository.ImageRepository;

@Service
public class DownloadService {

	private static final Logger log = LoggerFactory.getLogger(DownloadService.class);

	@Autowired
	private ImageRepository imageRepository;

	public byte[] downloadAllImagesAsZipFile() {
		List<Image> images = imageRepository.findAll();
		return compressToZipFile(images);
	}

	byte[] compressToZipFile(List<Image> images) {
		try (ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
				ZipArchiveOutputStream zipOutput = new ZipArchiveOutputStream(byteOut);) {

			zipOutput.setEncoding("UTF-8");

			for (int i = 0; i < images.size(); i++) {
				Image image = images.get(i);
				ZipArchiveEntry entry = new ZipArchiveEntry(createEntryFileName(image, i + 1));
				entry.setSize(image.getImageBinary().length);
				zipOutput.putArchiveEntry(entry);
				copyToOutputStream(zipOutput, image);
				zipOutput.closeArchiveEntry();
			}
			zipOutput.close();
			return byteOut.toByteArray();
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return null;
		}
	}

	private void copyToOutputStream(ZipArchiveOutputStream zipOutput, Image image) throws IOException {
		try (ByteArrayInputStream entryInputStream = new ByteArrayInputStream(image.getImageBinary())) {
			IOUtils.copy(entryInputStream, zipOutput);
		}
	}

	private String createEntryFileName(Image image, int index) {
		return getGroupNameNoSpaces(image) + "_" + index + ".jpg";
	}

	private String getGroupNameNoSpaces(Image image) {
		String groupName = image.getImageGroup().getName();
		return groupName.replaceAll(" ", "_");
	}

}
