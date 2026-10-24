package de.fred4jupiter.fredbet.web.validation;

import de.fred4jupiter.fredbet.domain.entity.Team;
import de.fred4jupiter.fredbet.web.matches.CreateEditMatchCommand;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ValidMatchValidator implements ConstraintValidator<ValidMatchConstraint, CreateEditMatchCommand> {

    private static final Logger LOG = LoggerFactory.getLogger(ValidMatchValidator.class);

    @Override
    public boolean isValid(CreateEditMatchCommand value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }

        if (hasSameTeamsPlayingAgainstEachOther(value)) {
            context.buildConstraintViolationWithTemplate("{msg.input.same.teams}").addPropertyNode("teamOne")
                .addConstraintViolation().disableDefaultConstraintViolation();
            LOG.error("The same teams cannot play against themself");
            return false;
        }

        return true;
    }

    private boolean hasSameTeamsPlayingAgainstEachOther(CreateEditMatchCommand value) {
        Team teamOne = value.getTeamOne();
        Team teamTwo = value.getTeamTwo();
        if (teamOne == null || teamTwo == null) {
            return false;
        }

        return teamOne.equals(teamTwo);
    }
}
