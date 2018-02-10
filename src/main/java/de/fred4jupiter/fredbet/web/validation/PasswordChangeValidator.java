package de.fred4jupiter.fredbet.web.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import de.fred4jupiter.fredbet.web.profile.ChangePasswordCommand;

/**
 * Validates the new password against the password repeat.
 * 
 * @author michael
 *
 */
public class PasswordChangeValidator implements ConstraintValidator<PasswordChangeConstraint, ChangePasswordCommand> {

	@Override
	public void initialize(PasswordChangeConstraint passwordChangeConstraint) {
	}

	@Override
	public boolean isValid(ChangePasswordCommand value, ConstraintValidatorContext context) {
		if (value == null) {
			return true;
		}

		return value.getNewPassword().equals(value.getNewPasswordRepeat());
	}

}
