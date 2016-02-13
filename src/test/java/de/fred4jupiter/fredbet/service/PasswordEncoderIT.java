package de.fred4jupiter.fredbet.service;

import static org.junit.Assert.*;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;

import de.fred4jupiter.fredbet.AbstractIntegrationTest;

public class PasswordEncoderIT extends AbstractIntegrationTest {

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
