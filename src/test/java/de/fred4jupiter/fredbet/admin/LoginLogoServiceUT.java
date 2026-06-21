package de.fred4jupiter.fredbet.admin;

import de.fred4jupiter.fredbet.common.UnitTest;
import de.fred4jupiter.fredbet.domain.entity.ImageBinary;
import de.fred4jupiter.fredbet.image.BinaryImage;
import de.fred4jupiter.fredbet.image.ImageBinaryRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@UnitTest
public class LoginLogoServiceUT {

    @InjectMocks
    private LoginLogoService loginLogoService;

    @Mock
    private ImageBinaryRepository imageBinaryRepository;

    @Test
    public void saveImageUpdatesExistingBinary() {
        byte[] binary = {1, 2, 3};
        ImageBinary imageBinary = new ImageBinary("LOGIN_LOGO", new byte[]{9});
        when(imageBinaryRepository.getReferenceById("LOGIN_LOGO")).thenReturn(imageBinary);

        loginLogoService.saveImage(binary);

        assertThat(imageBinary.getImageBinary()).isEqualTo(binary);
        verify(imageBinaryRepository).save(imageBinary);
    }

    @Test
    public void saveImageCreatesNewBinaryWhenNoExistingEntryIsFound() {
        byte[] binary = {4, 5};
        when(imageBinaryRepository.getReferenceById("LOGIN_LOGO")).thenThrow(new EntityNotFoundException());

        loginLogoService.saveImage(binary);

        verify(imageBinaryRepository).save(any(ImageBinary.class));
    }

    @Test
    public void loadLoginLogoReturnsBinaryImage() {
        byte[] binary = {7, 8};
        when(imageBinaryRepository.getReferenceById("LOGIN_LOGO")).thenReturn(new ImageBinary("LOGIN_LOGO", binary));

        Optional<BinaryImage> loginLogo = loginLogoService.loadLoginLogo();

        assertThat(loginLogo).isPresent();
        assertThat(loginLogo.orElseThrow().key()).isEqualTo("LOGIN_LOGO");
        assertThat(loginLogo.orElseThrow().imageBinary()).isEqualTo(binary);
    }

    @Test
    public void loadLoginLogoReturnsEmptyWhenNotFound() {
        when(imageBinaryRepository.getReferenceById("LOGIN_LOGO")).thenThrow(new EntityNotFoundException());

        assertThat(loginLogoService.loadLoginLogo()).isEmpty();
    }

    @Test
    public void deleteLoginLogoDeletesByFixedKey() {
        loginLogoService.deleteLoginLogo();

        verify(imageBinaryRepository).deleteById("LOGIN_LOGO");
    }
}

