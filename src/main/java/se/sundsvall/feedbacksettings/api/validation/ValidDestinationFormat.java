package se.sundsvall.feedbacksettings.api.validation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

import se.sundsvall.feedbacksettings.api.validation.impl.ValidDestinationFormatConstraintValidator;

@Documented
@Target({ ElementType.FIELD, ElementType.CONSTRUCTOR, ElementType.PARAMETER, ElementType.TYPE_USE })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ValidDestinationFormatConstraintValidator.class)
public @interface ValidDestinationFormat {

	String message() default "format for destination is not compliable with provided contact method";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};
}
