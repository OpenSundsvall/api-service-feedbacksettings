package se.sundsvall.feedbacksettings.api.validation.impl;

import static java.util.Objects.nonNull;
import java.util.UUID;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import se.sundsvall.feedbacksettings.api.validation.ValidUuid;

public class ValidUuidConstraintValidator implements ConstraintValidator<ValidUuid, String> {

	@Override
	public boolean isValid(final String value, final ConstraintValidatorContext context) {
		if (nonNull(value)) {
			try {
				UUID.fromString(value);
			} catch (Exception e) {
				return false;
			}
		}
		
		return true;
	}
}
