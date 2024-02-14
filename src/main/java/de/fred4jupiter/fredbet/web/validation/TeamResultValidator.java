package de.fred4jupiter.fredbet.web.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import de.fred4jupiter.fredbet.web.matches.MatchResultCommand;

/**
 * Validator that checks if you enter both goal results.
 * 
 * @author michael
 *
 */
public class TeamResultValidator implements ConstraintValidator<TeamResultConstraint, MatchResultCommand> {

	@Override
	public void initialize(TeamResultConstraint constraintAnnotation) {
	}

	@Override
	public boolean isValid(MatchResultCommand value, ConstraintValidatorContext context) {
		if (value == null) {
			return true;
		}

		Integer teamResultOne = value.getTeamResultOne();
		Integer teamResultTwo = value.getTeamResultTwo();
		
		if (teamResultOne == null && teamResultTwo == null) {
			return true;
		}
		
		return (teamResultOne != null && teamResultOne >= 0) && (teamResultTwo != null && teamResultTwo >= 0);
	}

}
