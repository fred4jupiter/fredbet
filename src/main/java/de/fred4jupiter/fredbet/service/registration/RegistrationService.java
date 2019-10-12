package de.fred4jupiter.fredbet.service.registration;

import org.springframework.stereotype.Service;

@Service
public class RegistrationService {

    public boolean isTokenValid(String token) {
        return true;
    }
}
