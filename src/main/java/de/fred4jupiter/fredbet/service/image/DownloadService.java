package de.fred4jupiter.fredbet.service.image;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.fred4jupiter.fredbet.service.image.storage.ImageLocationStrategy;

@Service
public class DownloadService {

	private static final Logger LOG = LoggerFactory.getLogger(DownloadService.class);

	@Autowired
	private ImageLocationStrategy imageLocationService;

	public byte[] downloadAllImagesAsZipFile() {
		List<BinaryImage> allImages = imageLocationService.findAllImages();
		if (allImages.isEmpty()) {
			return null;
		}

		return compressToZipFile(allImages);
	}

	byte[] compressToZipFile(List<BinaryImage> allImages) {
		try (ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
				ZipArchiveOutputStream zipOutput = new ZipArchiveOutputStream(byteOut);) {

			zipOutput.setEncoding("UTF-8");

			for (int i = 0; i < allImages.size(); i++) {
				BinaryImage image = allImages.get(i);
				String fileName = createEntryFileName(image, i + 1);
				ZipArchiveEntry entry = new ZipArchiveEntry(fileName);
				entry.setSize(image.getImageBinary().length);
				zipOutput.putArchiveEntry(entry);
				copyToOutputStream(zipOutput, image.getImageBinary());
				zipOutput.closeArchiveEntry();
			}
			zipOutput.close();
			return byteOut.toByteArray();
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
			return null;
		}
	}

	private void copyToOutputStream(ZipArchiveOutputStream zipOutput, byte[] data) throws IOException {
		try (ByteArrayInputStream entryInputStream = new ByteArrayInputStream(data)) {
			IOUtils.copy(entryInputStream, zipOutput);
		}
	}

	private String createEntryFileName(BinaryImage image, int index) {
		return FilenameUtils.getName(image.getKey());
	}

}
