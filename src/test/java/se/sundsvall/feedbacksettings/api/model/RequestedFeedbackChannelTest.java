package se.sundsvall.feedbacksettings.api.model;

import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanConstructor;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanEquals;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanHashCode;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanToString;
import static com.google.code.beanmatchers.BeanMatchers.hasValidGettersAndSetters;
import static com.google.code.beanmatchers.BeanMatchers.registerValueGenerator;
import static java.time.OffsetDateTime.now;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.MatcherAssert.assertThat;

import java.time.OffsetDateTime;
import java.util.Random;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import se.sundsvall.feedbacksettings.ContactMethod;

class RequestedFeedbackChannelTest {
	private static final String DESTINATION = "destination";
	private static final ContactMethod CONTACT_METHOD = ContactMethod.EMAIL;
	private static final Boolean SEND_FEEDBACK = Boolean.TRUE;
	
	@BeforeAll
	static void setup() {
		registerValueGenerator(() -> now().plusDays(new Random().nextInt()), OffsetDateTime.class);
	}

	@Test
	void testBean() {
		assertThat(RequestedFeedbackChannel.class, allOf(
			hasValidBeanConstructor(),
			hasValidGettersAndSetters(),
			hasValidBeanHashCode(),
			hasValidBeanEquals(),
			hasValidBeanToString()));
	}

	@Test
	void testCreatePattern() {
		RequestedFeedbackChannel channel = RequestedFeedbackChannel.create()
				.withContactMethod(CONTACT_METHOD)
				.withDestination(DESTINATION)
				.withSendFeedback(SEND_FEEDBACK);
		
		assertThat(channel.getContactMethod()).isEqualTo(CONTACT_METHOD);
		assertThat(channel.getDestination()).isEqualTo(DESTINATION);
		assertThat(channel.getSendFeedback()).isEqualTo(SEND_FEEDBACK);
	}

	@Test
	void testNoDirtOnCreatedBean() {
		assertThat(RequestedFeedbackChannel.create())
			.hasAllNullFieldsOrProperties();

		assertThat(new RequestedFeedbackChannel())
			.hasAllNullFieldsOrProperties();
	}
}
