package de.fred4jupiter.fredbet.service.calendar;

public class IcsFile {

    private final byte[] binary;

    private final String fileName;

    public IcsFile(byte[] binary, String fileName) {
        this.binary = binary;
        this.fileName = fileName;
    }

    public byte[] getBinary() {
        return binary;
    }

    public String getFileName() {
        return fileName;
    }
}
