package de.fred4jupiter.fredbet.admin;

import de.fred4jupiter.fredbet.domain.entity.ImageBinary;
import de.fred4jupiter.fredbet.image.BinaryImage;
import de.fred4jupiter.fredbet.image.ImageBinaryRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
public class LoginLogoService {

    private static final String LOGIN_LOGO_KEY = "LOGIN_LOGO";

    private final ImageBinaryRepository imageBinaryRepository;

    public LoginLogoService(ImageBinaryRepository imageBinaryRepository) {
        this.imageBinaryRepository = imageBinaryRepository;
    }

    public void saveImage(byte[] binary) {
        try {
            ImageBinary imageBinary = imageBinaryRepository.getReferenceById(LOGIN_LOGO_KEY);
            imageBinary.setImageBinary(binary);
            imageBinaryRepository.save(imageBinary);
        } catch (EntityNotFoundException e) {
            ImageBinary newImage = new ImageBinary(LOGIN_LOGO_KEY, binary);
            imageBinaryRepository.save(newImage);
        }
    }

    public Optional<BinaryImage> loadLoginLogo() {
        try {
            ImageBinary imageBinary = imageBinaryRepository.getReferenceById(LOGIN_LOGO_KEY);
            return Optional.of(new BinaryImage(LOGIN_LOGO_KEY, imageBinary.getImageBinary()));
        } catch (EntityNotFoundException e) {
            return Optional.empty();
        }
    }

    public void deleteLoginLogo() {
        imageBinaryRepository.deleteById(LOGIN_LOGO_KEY);
    }
}
