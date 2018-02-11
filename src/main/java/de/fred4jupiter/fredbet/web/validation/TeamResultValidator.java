package de.fred4jupiter.fredbet.web.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

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
		
		return (teamResultOne != null && teamResultOne.intValue() >= 0) && (teamResultTwo != null && teamResultTwo.intValue() >= 0);
	}

}
