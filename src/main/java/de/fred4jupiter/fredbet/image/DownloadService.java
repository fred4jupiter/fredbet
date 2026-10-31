package de.fred4jupiter.fredbet.image;

import de.fred4jupiter.fredbet.domain.entity.ImageBinary;
import de.fred4jupiter.fredbet.props.FredbetConstants;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.List;

@Service
public class DownloadService {

    private final ImageBinaryRepository imageBinaryRepository;

    DownloadService(ImageBinaryRepository imageBinaryRepository) {
        this.imageBinaryRepository = imageBinaryRepository;
    }

    public byte[] downloadAllImagesAsZipFile() {
        List<ImageBinary> images = imageBinaryRepository.findAll();
        if (images.isEmpty()) {
            return null;
        }

        return compressToZipFile(images);
    }

    byte[] compressToZipFile(List<ImageBinary> allImages) {
        try (ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
             ZipArchiveOutputStream zipOutput = new ZipArchiveOutputStream(byteOut)) {

            zipOutput.setEncoding("UTF-8");

            for (ImageBinary image : allImages) {
                String fileName = createEntryFileName(image);
                ZipArchiveEntry entry = new ZipArchiveEntry(fileName);
                entry.setSize(image.getImageBinary().length);
                zipOutput.putArchiveEntry(entry);
                copyToOutputStream(zipOutput, image.getImageBinary());
                zipOutput.closeArchiveEntry();
            }
            zipOutput.close();
            return byteOut.toByteArray();
        } catch (IOException e) {
            throw new UncheckedIOException("Could not create zip file of images", e);
        }
    }

    private void copyToOutputStream(ZipArchiveOutputStream zipOutput, byte[] data) throws IOException {
        try (ByteArrayInputStream entryInputStream = new ByteArrayInputStream(data)) {
            IOUtils.copy(entryInputStream, zipOutput);
        }
    }

    private String createEntryFileName(ImageBinary image) {
        String fileName = FilenameUtils.getName(image.getKey());
        if (!FilenameUtils.isExtension(fileName, FredbetConstants.IMAGE_JPG_EXTENSION)) {
            return fileName + FredbetConstants.IMAGE_JPG_EXTENSION_WITH_DOT;
        }
        return fileName;
    }

}
