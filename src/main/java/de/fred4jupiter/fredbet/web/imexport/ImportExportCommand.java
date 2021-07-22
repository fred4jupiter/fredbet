package de.fred4jupiter.fredbet.web.imexport;

import org.springframework.web.multipart.MultipartFile;

public class ImportExportCommand {

    private MultipartFile jsonFile;

    public MultipartFile getJsonFile() {
        return jsonFile;
    }

    public void setJsonFile(MultipartFile jsonFile) {
        this.jsonFile = jsonFile;
    }
}
