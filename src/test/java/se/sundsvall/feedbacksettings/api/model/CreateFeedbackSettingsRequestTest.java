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

class CreateFeedbackSettingsRequestTest {

	private static final String PERSON_ID = "personId";
	private static final String ORGANIZATION_ID = "organizationId";
	private static final List<RequestedFeedbackChannel> CHANNEL_LIST = List.of(RequestedFeedbackChannel.create());
	
	@Test
	void testBean() {
		assertThat(CreateFeedbackSettingsRequest.class, allOf(
			hasValidBeanConstructor(),
			hasValidGettersAndSetters(),
			hasValidBeanHashCode(),
			hasValidBeanEquals(),
			hasValidBeanToString()));
	}

	@Test
	void testCreatePattern() {
		CreateFeedbackSettingsRequest request = CreateFeedbackSettingsRequest.create()
				.withPersonId(PERSON_ID)
				.withOrganizationId(ORGANIZATION_ID)
				.withChannels(CHANNEL_LIST);
		
		assertThat(request.getPersonId()).isEqualTo(PERSON_ID);
		assertThat(request.getOrganizationId()).isEqualTo(ORGANIZATION_ID);
		assertThat(request.getChannels()).isEqualTo(CHANNEL_LIST);
	}
	
	@Test
	void testNoDirtOnCreatedBean() {
		assertThat(CreateFeedbackSettingsRequest.create()).hasAllNullFieldsOrProperties();
		assertThat(new CreateFeedbackSettingsRequest()).hasAllNullFieldsOrProperties();
	}
}
