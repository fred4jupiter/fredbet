package de.fred4jupiter.fredbet.web.integration;

import org.springframework.web.multipart.MultipartFile;

public class FootballDataUploadCommand {

    private MultipartFile jsonFile;

    public MultipartFile getJsonFile() {
        return jsonFile;
    }

    public void setJsonFile(MultipartFile jsonFile) {
        this.jsonFile = jsonFile;
    }
}
