package de.fred4jupiter.fredbet.web.validation;

import de.fred4jupiter.fredbet.common.UnitTest;
import de.fred4jupiter.fredbet.web.matches.MatchResultCommand;
import de.fred4jupiter.fredbet.web.profile.ChangePasswordCommand;
import de.fred4jupiter.fredbet.web.registration.RegistrationCommand;
import jakarta.validation.ConstraintValidatorContext;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@UnitTest
public class WebValidatorsUT {

    private final ChangePasswordCommandValidator changePasswordCommandValidator = new ChangePasswordCommandValidator();

    private final RegistrationCommandValidator registrationCommandValidator = new RegistrationCommandValidator();

    private final TeamResultValidator teamResultValidator = new TeamResultValidator();

    @Mock
    private ConstraintValidatorContext constraintValidatorContext;

    @Mock
    private ConstraintValidatorContext.ConstraintViolationBuilder constraintViolationBuilder;

    @Mock
    private ConstraintValidatorContext.ConstraintViolationBuilder.NodeBuilderCustomizableContext nodeBuilderCustomizableContext;

    @Test
    public void changePasswordValidatorRejectsMismatchingNewPasswords() {
        ChangePasswordCommand command = new ChangePasswordCommand();
        command.setOldPassword("old");
        command.setNewPassword("new");
        command.setNewPasswordRepeat("different");

        mockViolationBuilder("{msg.passwordChange.passwordMismatch}", "newPassword");

        boolean valid = changePasswordCommandValidator.isValid(command, constraintValidatorContext);

        assertThat(valid).isFalse();
        verify(constraintValidatorContext).disableDefaultConstraintViolation();
    }

    @Test
    public void changePasswordValidatorRejectsSameOldAndNewPassword() {
        ChangePasswordCommand command = new ChangePasswordCommand();
        command.setOldPassword("same");
        command.setNewPassword("same");
        command.setNewPasswordRepeat("same");

        mockViolationBuilder("{msg.passwordChange.oldAndNewPasswordAreSame}", "newPassword");

        boolean valid = changePasswordCommandValidator.isValid(command, constraintValidatorContext);

        assertThat(valid).isFalse();
        verify(constraintValidatorContext).disableDefaultConstraintViolation();
    }

    @Test
    public void changePasswordValidatorAcceptsMatchingNewPasswords() {
        ChangePasswordCommand command = new ChangePasswordCommand();
        command.setOldPassword("old");
        command.setNewPassword("new");
        command.setNewPasswordRepeat("new");

        assertThat(changePasswordCommandValidator.isValid(command, constraintValidatorContext)).isTrue();
    }

    @Test
    public void registrationValidatorRejectsMismatchingPasswords() {
        RegistrationCommand command = new RegistrationCommand();
        command.setPassword("one");
        command.setPasswordRepeat("two");

        mockViolationBuilder("{msg.registration.passwordMismatch}", "password");

        boolean valid = registrationCommandValidator.isValid(command, constraintValidatorContext);

        assertThat(valid).isFalse();
        verify(constraintValidatorContext).disableDefaultConstraintViolation();
    }

    @Test
    public void registrationValidatorAcceptsMatchingPasswords() {
        RegistrationCommand command = new RegistrationCommand();
        command.setPassword("one");
        command.setPasswordRepeat("one");

        assertThat(registrationCommandValidator.isValid(command, constraintValidatorContext)).isTrue();
    }

    @Test
    public void teamResultValidatorAcceptsBothResultsMissing() {
        MatchResultCommand command = new MatchResultCommand();

        assertThat(teamResultValidator.isValid(command, constraintValidatorContext)).isTrue();
    }

    @Test
    public void teamResultValidatorRejectsPartiallyMissingOrNegativeResults() {
        MatchResultCommand missingSecond = new MatchResultCommand();
        missingSecond.setTeamResultOne(1);

        MatchResultCommand negative = new MatchResultCommand();
        negative.setTeamResultOne(-1);
        negative.setTeamResultTwo(0);

        assertThat(teamResultValidator.isValid(missingSecond, constraintValidatorContext)).isFalse();
        assertThat(teamResultValidator.isValid(negative, constraintValidatorContext)).isFalse();
    }

    @Test
    public void teamResultValidatorAcceptsNonNegativePair() {
        MatchResultCommand command = new MatchResultCommand();
        command.setTeamResultOne(1);
        command.setTeamResultTwo(0);

        assertThat(teamResultValidator.isValid(command, constraintValidatorContext)).isTrue();
    }

    private void mockViolationBuilder(String messageTemplate, String propertyName) {
        when(constraintValidatorContext.buildConstraintViolationWithTemplate(messageTemplate)).thenReturn(constraintViolationBuilder);
        when(constraintViolationBuilder.addPropertyNode(propertyName)).thenReturn(nodeBuilderCustomizableContext);
        when(nodeBuilderCustomizableContext.addConstraintViolation()).thenReturn(constraintValidatorContext);
    }
}

