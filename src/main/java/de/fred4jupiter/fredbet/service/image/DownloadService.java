package de.fred4jupiter.fredbet.service.image;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.fred4jupiter.fredbet.domain.ImageMetaData;

@Service
public class DownloadService {

	private static final Logger LOG = LoggerFactory.getLogger(DownloadService.class);

	@Autowired
	private ImageAdministrationService imageAdministrationService;

	public byte[] downloadAllImagesAsZipFile() {
		List<DownloadDto> allImages = new ArrayList<>();
		imageAdministrationService.fetchAllImages((ImageMetaData imageMetaData, ImageData imageData) -> {
			allImages.add(new DownloadDto(imageMetaData.getImageGroup().getName(), imageData.getBinary()));
		});

		return compressToZipFile(allImages);
	}

	byte[] compressToZipFile(List<DownloadDto> allImages) {
		try (ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
				ZipArchiveOutputStream zipOutput = new ZipArchiveOutputStream(byteOut);) {

			zipOutput.setEncoding("UTF-8");

			for (int i = 0; i < allImages.size(); i++) {
				DownloadDto image = allImages.get(i);
				ZipArchiveEntry entry = new ZipArchiveEntry(createEntryFileName(image, i + 1));
				entry.setSize(image.getBinary().length);
				zipOutput.putArchiveEntry(entry);
				copyToOutputStream(zipOutput, image);
				zipOutput.closeArchiveEntry();
			}
			zipOutput.close();
			return byteOut.toByteArray();
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
			return null;
		}
	}

	private void copyToOutputStream(ZipArchiveOutputStream zipOutput, DownloadDto image) throws IOException {
		try (ByteArrayInputStream entryInputStream = new ByteArrayInputStream(image.getBinary())) {
			IOUtils.copy(entryInputStream, zipOutput);
		}
	}

	private String createEntryFileName(DownloadDto image, int index) {
		return getGroupNameNoSpaces(image) + "_" + index + ".jpg";
	}

	private String getGroupNameNoSpaces(DownloadDto image) {
		String groupName = image.getGroupName();
		return groupName.replaceAll(" ", "_");
	}

	static class DownloadDto {
		private String groupName;

		private byte[] binary;

		public DownloadDto(String groupName, byte[] binary) {
			super();
			this.groupName = groupName;
			this.binary = binary;
		}

		public String getGroupName() {
			return groupName;
		}

		public byte[] getBinary() {
			return binary;
		}
	}

}
