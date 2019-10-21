package de.fred4jupiter.fredbet.web.validation;

import de.fred4jupiter.fredbet.web.registration.RegistrationCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class RegistrationCommandValidator implements ConstraintValidator<PasswordRepeatConstraint, RegistrationCommand> {

    private static final Logger LOG = LoggerFactory.getLogger(RegistrationCommandValidator.class);

    @Override
    public boolean isValid(RegistrationCommand value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }

        if (!value.getPassword().equals(value.getPasswordRepeat())) {
            context.buildConstraintViolationWithTemplate("{msg.registration.passwordMismatch}")
                    .addPropertyNode("password").addConstraintViolation().disableDefaultConstraintViolation();
            LOG.error("password and passwordRepeat are different");
            return false;
        }

        return true;
    }
}
