package de.fred4jupiter.fredbet.web.validation;

import de.fred4jupiter.fredbet.domain.Country;
import de.fred4jupiter.fredbet.util.Validator;
import de.fred4jupiter.fredbet.web.matches.CreateEditMatchCommand;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ValidMatchValidator implements ConstraintValidator<ValidMatchConstraint, CreateEditMatchCommand> {

    private static final Logger LOG = LoggerFactory.getLogger(ValidMatchValidator.class);

    @Override
    public boolean isValid(CreateEditMatchCommand value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }

        if (oneTeamNotSelected(value)) {
            context.buildConstraintViolationWithTemplate("{msg.input.countries.and.teamNames}").addPropertyNode("teamNameOne")
                    .addConstraintViolation().disableDefaultConstraintViolation();
            LOG.error("Country or team name has been selected");
            return false;
        }

        if (hasSameTeamsPlayingAgainstEachOther(value)) {
            context.buildConstraintViolationWithTemplate("{msg.input.same.teams}").addPropertyNode("countryTeamOne")
                    .addConstraintViolation().disableDefaultConstraintViolation();
            LOG.error("The same teams cannot play against themself");
            return false;
        }

        return true;
    }

    private boolean oneTeamNotSelected(CreateEditMatchCommand value) {
        Country countryTeamOne = value.getCountryTeamOne();
        Country countryTeamTwo = value.getCountryTeamTwo();

        String teamNameOne = value.getTeamNameOne();
        String teamNameTwo = value.getTeamNameTwo();

        return (Validator.isNull(countryTeamOne) && StringUtils.isBlank(teamNameOne))
               || (Validator.isNotNull(countryTeamOne) && StringUtils.isNotBlank(teamNameOne))
               || (Validator.isNull(countryTeamTwo) && StringUtils.isBlank(teamNameTwo))
               || (Validator.isNotNull(countryTeamTwo) && StringUtils.isNotBlank(teamNameTwo));
    }

    private boolean hasSameTeamsPlayingAgainstEachOther(CreateEditMatchCommand value) {
        Country countryTeamOne = value.getCountryTeamOne();
        Country countryTeamTwo = value.getCountryTeamTwo();

        String teamNameOne = value.getTeamNameOne();
        String teamNameTwo = value.getTeamNameTwo();

        if (Validator.isNotNull(countryTeamOne) && Validator.isNotNull(countryTeamTwo)) {
            if (countryTeamOne.equals(countryTeamTwo)) {
                return true;
            }
        }

        if (StringUtils.isNotBlank(teamNameOne) && StringUtils.isNotBlank(teamNameTwo)) {
            if (teamNameOne.equals(teamNameTwo)) {
                return true;
            }
        }

        return false;
    }
}
