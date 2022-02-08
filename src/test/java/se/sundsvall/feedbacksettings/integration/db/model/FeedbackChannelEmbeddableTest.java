package se.sundsvall.feedbacksettings.integration.db.model;

import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanConstructor;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanEquals;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanHashCode;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanToString;
import static com.google.code.beanmatchers.BeanMatchers.hasValidGettersAndSetters;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.AllOf.allOf;

import org.junit.jupiter.api.Test;

import se.sundsvall.feedbacksettings.ContactMethod;

class FeedbackChannelEmbeddableTest {
	private static final ContactMethod CONTACT_METHOD = ContactMethod.EMAIL;
	private static final String DESTINATION = "destination";
	private static final boolean SEND_FEEDBACK = true;
	
	@Test
	void testBean() {
		assertThat(FeedbackChannelEmbeddable.class, allOf(
			hasValidBeanConstructor(),
			hasValidGettersAndSetters(),
			hasValidBeanHashCode(),
			hasValidBeanEquals(),
			hasValidBeanToString()));
	}

	@Test
	void testCreatePattern() {
		FeedbackChannelEmbeddable bean = FeedbackChannelEmbeddable.create()
				.withContactMethod(CONTACT_METHOD)
				.withDestination(DESTINATION)
				.withSendFeedback(SEND_FEEDBACK);
		
		assertThat(bean.getContactMethod()).isEqualTo(CONTACT_METHOD);
		assertThat(bean.getDestination()).isEqualTo(DESTINATION);
		assertThat(bean.isSendFeedback()).isEqualTo(SEND_FEEDBACK);
	}

	@Test
	void testNullValues() {
		assertThat(FeedbackChannelEmbeddable.create().withContactMethod(null).getContactMethod()).isNull();
	}
	
	@Test
	void testNoDirtOnCreatedBean() {
		assertThat(FeedbackChannelEmbeddable.create())
				.hasAllNullFieldsOrPropertiesExcept("sendFeedback")
				.hasFieldOrPropertyWithValue("sendFeedback", false);
		
		assertThat(new FeedbackChannelEmbeddable())
				.hasAllNullFieldsOrPropertiesExcept("sendFeedback")
				.hasFieldOrPropertyWithValue("sendFeedback", false);
	}
}
