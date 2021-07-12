package de.fred4jupiter.fredbet.service;

import de.fred4jupiter.fredbet.common.IntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@IntegrationTest
public class PasswordEncoderIT {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    public void encodePassword() {
        final String password = "mega";
        String encoded = passwordEncoder.encode(password);
        assertNotNull(encoded);
        assertTrue(passwordEncoder.matches(password, encoded));
    }
}
