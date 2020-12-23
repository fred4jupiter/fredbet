package de.fred4jupiter.fredbet.web.image;

public class ImageUploadCommand {

    private String galleryGroup;

    private String description;

    private String myFileBase64;

    public String getGalleryGroup() {
        return galleryGroup;
    }

    public void setGalleryGroup(String galleryGroup) {
        this.galleryGroup = galleryGroup;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getMyFileBase64() {
        return myFileBase64;
    }

    public void setMyFileBase64(String myFileBase64) {
        this.myFileBase64 = myFileBase64;
    }
}
