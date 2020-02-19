package de.fred4jupiter.fredbet.web.user;

import org.springframework.web.multipart.MultipartFile;

public class UserImportExportCommand {

    private MultipartFile jsonFile;

    public MultipartFile getJsonFile() {
        return jsonFile;
    }

    public void setJsonFile(MultipartFile jsonFile) {
        this.jsonFile = jsonFile;
    }
}
