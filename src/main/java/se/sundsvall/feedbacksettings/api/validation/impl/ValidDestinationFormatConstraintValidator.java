package se.sundsvall.feedbacksettings.api.validation.impl;

import static org.apache.commons.lang3.StringUtils.isNoneBlank; 
import java.util.Map;
import java.util.Optional;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;import org.hibernate.validator.internal.constraintvalidators.bv.EmailValidator;

import se.sundsvall.feedbacksettings.ContactMethod;
import se.sundsvall.feedbacksettings.api.model.RequestedFeedbackChannel;
import se.sundsvall.feedbacksettings.api.validation.ValidDestinationFormat;

public class ValidDestinationFormatConstraintValidator implements ConstraintValidator<ValidDestinationFormat, RequestedFeedbackChannel> {
	private static final Map<ContactMethod, String> VALIDATION_MESSAGES = Map.of(
			ContactMethod.EMAIL, "destination must be a well-formed email address when provided contact method is EMAIL",
			ContactMethod.SMS, "destination must match pattern 07[02369]nnnnnnn when provided contact method is SMS");
	private static final Map<ContactMethod, ConstraintValidator<?, CharSequence>> VALIDATORS = Map.of(
			ContactMethod.EMAIL, new EmailValidator(),
			ContactMethod.SMS, new PatternValidator<>("^07[02369]\\d{7}$"));
	
	@Override
	public boolean isValid(final RequestedFeedbackChannel channel, final ConstraintValidatorContext context) {
		try {
			boolean valid = isNoneBlank(channel.getDestination()) &&  Optional.of(VALIDATORS.get(channel.getContactMethod()))
					.map(validator -> validator.isValid(channel.getDestination(), context))
					.orElse(true); //If no validator is connected to the contactMethod, it is considered to be valid
			
			if (!valid) {
				useCustomMessageForValidation(context, Optional.ofNullable(VALIDATION_MESSAGES.get(channel.getContactMethod())));
			}
			
			return valid;
		} catch (Exception e) {
			return false;
		}
	}

    private void useCustomMessageForValidation(ConstraintValidatorContext constraintContext, Optional<String> message) {
    	if (message.isPresent()) {
        	constraintContext.disableDefaultConstraintViolation();
        	constraintContext.buildConstraintViolationWithTemplate(message.get()).addConstraintViolation();
    	}
    }
}
