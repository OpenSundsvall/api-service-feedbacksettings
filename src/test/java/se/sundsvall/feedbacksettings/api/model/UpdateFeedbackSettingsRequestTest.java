package se.sundsvall.feedbacksettings.api.model;

import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanConstructor;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanEquals;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanHashCode;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanToString;
import static com.google.code.beanmatchers.BeanMatchers.hasValidGettersAndSetters;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.List;

import org.junit.jupiter.api.Test;

class UpdateFeedbackSettingsRequestTest {

	private static final List<RequestedFeedbackChannel> CHANNEL_LIST = List.of(RequestedFeedbackChannel.create());
	
	@Test
	void testBean() {
		assertThat(UpdateFeedbackSettingsRequest.class, allOf(
			hasValidBeanConstructor(),
			hasValidGettersAndSetters(),
			hasValidBeanHashCode(),
			hasValidBeanEquals(),
			hasValidBeanToString()));
	}
	
	@Test
	void testCreatePattern() {
		UpdateFeedbackSettingsRequest request = UpdateFeedbackSettingsRequest.create()
				.withChannels(CHANNEL_LIST);
		
		assertThat(request.getChannels()).isEqualTo(CHANNEL_LIST);
	}
	
	@Test
	void testNoDirtOnCreatedBean() {
		assertThat(UpdateFeedbackSettingsRequest.create()).hasAllNullFieldsOrProperties();
		assertThat(new UpdateFeedbackSettingsRequest()).hasAllNullFieldsOrProperties();
	}
}
