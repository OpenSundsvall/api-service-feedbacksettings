package se.sundsvall.feedbacksettings.api.validation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

import se.sundsvall.feedbacksettings.api.validation.impl.ValidUuidConstraintValidator;

@Documented
@Target({ ElementType.FIELD, ElementType.CONSTRUCTOR, ElementType.PARAMETER, ElementType.TYPE_USE })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ValidUuidConstraintValidator.class)
public @interface ValidUuid {

	String message() default "must be a valid UUID";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};
}
