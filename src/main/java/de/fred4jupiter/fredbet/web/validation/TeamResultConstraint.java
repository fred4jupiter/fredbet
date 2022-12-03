package de.fred4jupiter.fredbet.web.validation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

@Documented
@Constraint(validatedBy = TeamResultValidator.class)
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface TeamResultConstraint {

	String message() default "You entered result is invalid!";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};
	
	String value() default "";
}
