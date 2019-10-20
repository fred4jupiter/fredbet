package de.fred4jupiter.fredbet.web.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = RegistrationCommandValidator.class)
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface PasswordRepeatConstraint {

    String message() default "Password mismatch!";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
