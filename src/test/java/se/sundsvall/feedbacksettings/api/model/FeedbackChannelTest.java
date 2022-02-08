package se.sundsvall.feedbacksettings.api.model;

import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanConstructor;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanEquals;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanHashCode;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanToString;
import static com.google.code.beanmatchers.BeanMatchers.hasValidGettersAndSetters;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.jupiter.api.Test;

import se.sundsvall.feedbacksettings.ContactMethod;

class FeedbackChannelTest {
	private static final String DESTINATION = "destination";
	private static final ContactMethod CONTACT_METHOD = ContactMethod.EMAIL;
	private static final boolean SEND_FEEDBACK = true;
	
	@Test
	void testBean() {
		assertThat(FeedbackChannel.class, allOf(
			hasValidBeanConstructor(),
			hasValidGettersAndSetters(),
			hasValidBeanHashCode(),
			hasValidBeanEquals(),
			hasValidBeanToString()));
	}

	@Test
	void testCreatePattern() {
		FeedbackChannel channel = FeedbackChannel.create()
				.withContactMethod(CONTACT_METHOD)
				.withDestination(DESTINATION)
				.withSendFeedback(SEND_FEEDBACK);
		
		assertThat(channel.getContactMethod()).isEqualTo(CONTACT_METHOD);
		assertThat(channel.getDestination()).isEqualTo(DESTINATION);
		assertThat(channel.isSendFeedback()).isEqualTo(SEND_FEEDBACK);
	}

	@Test
	void testNoDirtOnCreatedBean() {
		assertThat(FeedbackChannel.create())
			.hasAllNullFieldsOrPropertiesExcept("sendFeedback")
			.hasFieldOrPropertyWithValue("sendFeedback", false);

		assertThat(new FeedbackChannel())
			.hasAllNullFieldsOrPropertiesExcept("sendFeedback")
			.hasFieldOrPropertyWithValue("sendFeedback", false);
	}
}
