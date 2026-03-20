package de.fred4jupiter.fredbet.avatar;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Random;

@Service
public class AvatarService {

    public byte[] selectUserAvatarForNewUser() {
        return resourceToByteArray(getNumberFormated(randomNumber()));
    }

    private String getNumberFormated(int number) {
        if (number < 10) {
            return "0" + number;
        } else {
            return String.valueOf(number);
        }
    }

    private int randomNumber() {
        return new Random().nextInt(40) + 1;
    }

    private byte[] resourceToByteArray(String numberFormated) {
        try {
            String avatarResourcePath = "/content/user_avatars/user_avatar_%s.jpg".formatted(numberFormated);
            ClassPathResource classPathResource = new ClassPathResource(avatarResourcePath);
            return classPathResource.getContentAsByteArray();
        } catch (IOException e) {
            throw new IllegalStateException("Could not load user avatar. Cause: " + e.getMessage());
        }
    }
}
