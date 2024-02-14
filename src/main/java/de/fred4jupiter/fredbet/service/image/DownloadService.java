package de.fred4jupiter.fredbet.service.image;

import de.fred4jupiter.fredbet.props.FredbetConstants;
import de.fred4jupiter.fredbet.service.image.storage.ImageLocationStrategy;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

@Service
public class DownloadService {

    private static final Logger LOG = LoggerFactory.getLogger(DownloadService.class);

    private final ImageLocationStrategy imageLocationService;

    public DownloadService(ImageLocationStrategy imageLocationService) {
        this.imageLocationService = imageLocationService;
    }

    public byte[] downloadAllImagesAsZipFile() {
        List<BinaryImage> allImages = imageLocationService.findAllImages();
        if (allImages.isEmpty()) {
            return null;
        }

        return compressToZipFile(allImages);
    }

    byte[] compressToZipFile(List<BinaryImage> allImages) {
        try (ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
             ZipArchiveOutputStream zipOutput = new ZipArchiveOutputStream(byteOut)) {

            zipOutput.setEncoding("UTF-8");

            for (BinaryImage image : allImages) {
                String fileName = createEntryFileName(image);
                ZipArchiveEntry entry = new ZipArchiveEntry(fileName);
                entry.setSize(image.imageBinary().length);
                zipOutput.putArchiveEntry(entry);
                copyToOutputStream(zipOutput, image.imageBinary());
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

    private String createEntryFileName(BinaryImage image) {
        String fileName = FilenameUtils.getName(image.key());
        if (!FilenameUtils.isExtension(fileName, FredbetConstants.IMAGE_JPG_EXTENSION)) {
            return fileName + FredbetConstants.IMAGE_JPG_EXTENSION_WITH_DOT;
        }
        return fileName;
    }

}
