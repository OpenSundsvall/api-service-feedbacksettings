package se.sundsvall.feedbacksettings.api.validation.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import javax.validation.ConstraintValidatorContext;
import javax.validation.ConstraintValidatorContext.ConstraintViolationBuilder;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import se.sundsvall.feedbacksettings.ContactMethod;
import se.sundsvall.feedbacksettings.api.model.RequestedFeedbackChannel;

@ExtendWith(MockitoExtension.class)
class ValidDestinationFormatConstraintValidatorTest {

	@Mock
	private ConstraintValidatorContext constraintValidatorContextMock;

	@Mock
	private ConstraintViolationBuilder constraintViolationBuilderMock;
	
	@InjectMocks
	private ValidDestinationFormatConstraintValidator validator;

	@Test
	void validChannelForCorrectEmailDestination() {
		RequestedFeedbackChannel channel = RequestedFeedbackChannel.create()
			.withContactMethod(ContactMethod.EMAIL)
			.withDestination("valid@email")
			.withSendFeedback(false);
		
		assertThat(validator.isValid(channel, constraintValidatorContextMock)).isTrue();
	}

	@Test
	void validChannelForCorrectSmsDestination() {
		RequestedFeedbackChannel channel = RequestedFeedbackChannel.create()
			.withContactMethod(ContactMethod.SMS)
			.withDestination("0701234567")
			.withSendFeedback(false);
		
		assertThat(validator.isValid(channel, constraintValidatorContextMock)).isTrue();
	}

	@ParameterizedTest
	@ValueSource(strings = {"", " "}) 
	void invalidChannelForBlankDestinations(String blankValue) {
		RequestedFeedbackChannel channel = RequestedFeedbackChannel.create().withDestination(blankValue);
		
		assertThat(validator.isValid(channel, constraintValidatorContextMock)).isFalse();
	}

	@Test
	void invalidChannelForNullDestination() {
		RequestedFeedbackChannel channel = RequestedFeedbackChannel.create();
		
		assertThat(validator.isValid(channel, constraintValidatorContextMock)).isFalse();
	}

	@Test
	void invalidChannelForEmail() {
		when(constraintValidatorContextMock.buildConstraintViolationWithTemplate(any())).thenReturn(constraintViolationBuilderMock);
		
		RequestedFeedbackChannel channel = RequestedFeedbackChannel.create()
			.withContactMethod(ContactMethod.EMAIL)
			.withDestination("not-valid")
			.withSendFeedback(false);

		assertThat(validator.isValid(channel, constraintValidatorContextMock)).isFalse();
		
		verify(constraintValidatorContextMock).disableDefaultConstraintViolation();
		verify(constraintValidatorContextMock).buildConstraintViolationWithTemplate("destination must be a well-formed email address when provided contact method is EMAIL");
		verify(constraintViolationBuilderMock).addConstraintViolation();
	}

	@Test
	void invalidChannelForSms() {
		when(constraintValidatorContextMock.buildConstraintViolationWithTemplate(any())).thenReturn(constraintViolationBuilderMock);

		RequestedFeedbackChannel channel = RequestedFeedbackChannel.create()
			.withContactMethod(ContactMethod.SMS)
			.withDestination("not-valid")
			.withSendFeedback(false);
		
		assertThat(validator.isValid(channel, constraintValidatorContextMock)).isFalse();

		verify(constraintValidatorContextMock).disableDefaultConstraintViolation();
		verify(constraintValidatorContextMock).buildConstraintViolationWithTemplate("destination must match pattern 07[02369]nnnnnnn when provided contact method is SMS");
		verify(constraintViolationBuilderMock).addConstraintViolation();
	}

	@Test
	void invalidChannelForNull() {
		assertThat(validator.isValid(null, constraintValidatorContextMock)).isFalse();
	}
}
