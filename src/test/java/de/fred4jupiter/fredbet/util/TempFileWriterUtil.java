package de.fred4jupiter.fredbet.util;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;

public class TempFileWriterUtil {

    private static final Logger LOG = LoggerFactory.getLogger(TempFileWriterUtil.class);

    public static boolean writeToTempFolder(byte[] bytes, String filename) {
        String tempDir = System.getProperty("java.io.tmpdir");
        File generatedFile = new File(tempDir, filename);

        try {
            FileUtils.writeByteArrayToFile(generatedFile, bytes);
            LOG.info("Created file at: {}", generatedFile.getAbsolutePath());
            return generatedFile.exists();
        } catch (IOException e) {
            LOG.error(e.getMessage(), e);
            return false;
        }
    }
}
