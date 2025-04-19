package de.fred4jupiter.fredbet.image;

import de.fred4jupiter.fredbet.props.FredbetProperties;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class DefaultProfileImageLoader {

    public static final String DEFAULT_USER_PROFILE_KEY = "1";

    public static final String DEFAULT_USER_PROFILE_THUMB_KEY = "1t";

    private final FredbetProperties fredbetProperties;

    public DefaultProfileImageLoader(FredbetProperties fredbetProperties) {
        this.fredbetProperties = fredbetProperties;
    }

    public BinaryImage getDefaultProfileImage() {
        return new BinaryImage(DEFAULT_USER_PROFILE_KEY, resourceToByteArray(fredbetProperties.defaultImages().profileImageResource()));
    }

    public BinaryImage getDefaultThumbProfileImage() {
        return new BinaryImage(DEFAULT_USER_PROFILE_THUMB_KEY, resourceToByteArray(fredbetProperties.defaultImages().thumbProfileImageResource()));
    }

    private byte[] resourceToByteArray(Resource resource) {
        try {
            return resource.getContentAsByteArray();
        } catch (IOException e) {
            throw new IllegalStateException("Default user profile image could not be loaded. Cause: " + e.getMessage());
        }
    }
}


