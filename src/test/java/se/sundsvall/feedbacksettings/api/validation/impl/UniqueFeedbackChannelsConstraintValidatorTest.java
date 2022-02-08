package se.sundsvall.feedbacksettings.api.validation.impl;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Collections;
import java.util.List;

import javax.validation.ConstraintValidatorContext;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import se.sundsvall.feedbacksettings.ContactMethod;
import se.sundsvall.feedbacksettings.api.model.RequestedFeedbackChannel;

@ExtendWith(MockitoExtension.class)
class UniqueFeedbackChannelsConstraintValidatorTest {

	private static final String DESTINATION_1 = "destination1";
	private static final String DESTINATION_2 = "destination2";
	private static final ContactMethod CONTACT_METHOD_1 = ContactMethod.EMAIL;
	private static final ContactMethod CONTACT_METHOD_2 = ContactMethod.SMS;

	@Mock
	private ConstraintValidatorContext constraintValidatorContextMock;

	@InjectMocks
	private UniqueFeedbackChannelsConstraintValidator validator;

	@Test
	void sameDestinationDifferentContactMethod() {
		List<RequestedFeedbackChannel> list = List.of(
				generateElement(CONTACT_METHOD_1, DESTINATION_1, Boolean.TRUE),
				generateElement(CONTACT_METHOD_2, DESTINATION_1, Boolean.FALSE));
		
		assertThat(validator.isValid(list, constraintValidatorContextMock)).isTrue();
	}

	@Test
	void sameContactMethodDifferentDestination() {
		List<RequestedFeedbackChannel> list = List.of(
				generateElement(CONTACT_METHOD_1, DESTINATION_1, Boolean.TRUE),
				generateElement(CONTACT_METHOD_1, DESTINATION_2, Boolean.FALSE));
		
		assertThat(validator.isValid(list, constraintValidatorContextMock)).isTrue();
	}

	@Test
	void differentContactMethodDifferentDestination() {
		List<RequestedFeedbackChannel> list = List.of(
				generateElement(CONTACT_METHOD_1, DESTINATION_1, Boolean.TRUE),
				generateElement(CONTACT_METHOD_2, DESTINATION_2, Boolean.TRUE));
		
		assertThat(validator.isValid(list, constraintValidatorContextMock)).isTrue();
	}

	@Test
	void sameContactMethodSameDestination() {
		List<RequestedFeedbackChannel> list = List.of(
				generateElement(CONTACT_METHOD_1, DESTINATION_1, Boolean.TRUE),
				generateElement(CONTACT_METHOD_1, DESTINATION_1, Boolean.FALSE));
		
		assertThat(validator.isValid(list, constraintValidatorContextMock)).isFalse();
	}

	@Test
	void singleElement() {
		assertThat(validator.isValid(List.of(generateElement(CONTACT_METHOD_1, DESTINATION_1, Boolean.TRUE)), constraintValidatorContextMock)).isTrue();
	}

	@Test
	void emptyList() {
		assertThat(validator.isValid(Collections.emptyList(), constraintValidatorContextMock)).isTrue();
	}

	@Test
	void nullValue() {
		assertThat(validator.isValid(null, constraintValidatorContextMock)).isTrue();
	}

	private RequestedFeedbackChannel generateElement(ContactMethod contactMethod, String destination, Boolean sendFeedback) {
		return RequestedFeedbackChannel.create()
				.withContactMethod(contactMethod)
				.withDestination(destination)
				.withSendFeedback(sendFeedback);
	}
}
