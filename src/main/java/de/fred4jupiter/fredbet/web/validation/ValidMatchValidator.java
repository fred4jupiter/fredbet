package de.fred4jupiter.fredbet.web.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.fred4jupiter.fredbet.domain.Country;
import de.fred4jupiter.fredbet.util.Validator;
import de.fred4jupiter.fredbet.web.matches.CreateEditMatchCommand;

public class ValidMatchValidator implements ConstraintValidator<ValidMatchConstraint, CreateEditMatchCommand> {

	private static final Logger LOG = LoggerFactory.getLogger(ValidMatchValidator.class);

	@Override
	public void initialize(ValidMatchConstraint constraintAnnotation) {
	}

	@Override
	public boolean isValid(CreateEditMatchCommand value, ConstraintValidatorContext context) {
		if (value == null) {
			return true;
		}

		if (hasCountriesAndTeamNamesEntered(value)) {
			context.buildConstraintViolationWithTemplate("{msg.input.countries.and.teamNames}").addPropertyNode("teamNameOne")
					.addConstraintViolation().disableDefaultConstraintViolation();
			LOG.error("Countries and team names has been selected");
			return false;
		}

		if (isInvalidTeamSelection(value)) {
			context.buildConstraintViolationWithTemplate("{msg.input.teamOne.teamTwo}").addPropertyNode("countryTeamOne")
					.addConstraintViolation().disableDefaultConstraintViolation();
			LOG.error("Team names are empty");
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

	private boolean isInvalidTeamSelection(CreateEditMatchCommand value) {
		Country countryTeamOne = value.getCountryTeamOne();
		Country countryTeamTwo = value.getCountryTeamTwo();

		String teamNameOne = value.getTeamNameOne();
		String teamNameTwo = value.getTeamNameTwo();

		// nothing selected
		if ((Validator.isEmpty(countryTeamOne) && Validator.isEmpty(countryTeamTwo))
				&& (StringUtils.isBlank(teamNameOne) && StringUtils.isBlank(teamNameTwo))) {
			return true;
		}

		if (Validator.isEmpty(countryTeamOne) && Validator.isNotEmpty(countryTeamTwo)) {
			return true;
		}

		if (Validator.isNotEmpty(countryTeamOne) && Validator.isEmpty(countryTeamTwo)) {
			return true;
		}

		if (StringUtils.isBlank(teamNameOne) && StringUtils.isNotBlank(teamNameTwo)) {
			return true;
		}

		if (StringUtils.isNotBlank(teamNameOne) && StringUtils.isBlank(teamNameTwo)) {
			return true;
		}

		return false;
	}

	private boolean hasCountriesAndTeamNamesEntered(CreateEditMatchCommand value) {
		Country countryTeamOne = value.getCountryTeamOne();
		Country countryTeamTwo = value.getCountryTeamTwo();

		String teamNameOne = value.getTeamNameOne();
		String teamNameTwo = value.getTeamNameTwo();

		return ((Validator.isNotEmpty(countryTeamOne) || Validator.isNotEmpty(countryTeamTwo))
				&& (StringUtils.isNotBlank(teamNameOne) || StringUtils.isNotBlank(teamNameTwo)));
	}

	private boolean hasSameTeamsPlayingAgainstEachOther(CreateEditMatchCommand value) {
		Country countryTeamOne = value.getCountryTeamOne();
		Country countryTeamTwo = value.getCountryTeamTwo();

		String teamNameOne = value.getTeamNameOne();
		String teamNameTwo = value.getTeamNameTwo();

		if (Validator.isNotEmpty(countryTeamOne) && Validator.isNotEmpty(countryTeamTwo)) {
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
