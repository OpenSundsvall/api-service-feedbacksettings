package se.sundsvall.feedbacksettings.api.validation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

import se.sundsvall.feedbacksettings.api.validation.impl.UniqueFeedbackChannelsConstraintValidator;

@Documented
@Target({ ElementType.FIELD, ElementType.CONSTRUCTOR, ElementType.PARAMETER, ElementType.TYPE_USE })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = UniqueFeedbackChannelsConstraintValidator.class)
public @interface UniqueFeedbackChannels {

	String message() default "the collection contains two or more elements with equal contactMethod and destination, these values must be unique";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};
}
