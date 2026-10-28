package de.fred4jupiter.fredbet.web.login;

import org.springframework.web.multipart.MultipartFile;

public class LoginLogoCommand {

    private MultipartFile imageFile;

    public MultipartFile getImageFile() {
        return imageFile;
    }

    public void setImageFile(MultipartFile imageFile) {
        this.imageFile = imageFile;
    }
}
