package de.fred4jupiter.fredbet.web.validation;

import de.fred4jupiter.fredbet.common.IntegrationTest;
import de.fred4jupiter.fredbet.web.matches.CreateEditMatchCommand;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@IntegrationTest
public class ValidMatchValidatorIT {

    private static final Logger LOG = LoggerFactory.getLogger(ValidMatchValidatorIT.class);

    @Autowired
    private Validator validator;

    @Test
    void validateCreateEditMatchCommand() {
        CreateEditMatchCommand createEditMatchCommand = new CreateEditMatchCommand();
        Set<ConstraintViolation<CreateEditMatchCommand>> violations = validator.validate(createEditMatchCommand);
        assertThat(violations).isNotEmpty();

        violations.forEach(violation -> {
            LOG.debug("violation: {}", violation.getMessage());
        });

    }
}
