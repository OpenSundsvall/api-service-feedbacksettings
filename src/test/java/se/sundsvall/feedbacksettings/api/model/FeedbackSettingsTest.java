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
import java.util.List;
import java.util.Random;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class FeedbackSettingsTest {
	private static final List<FeedbackChannel> CHANNELS = List.of(FeedbackChannel.create());
	private static final String ID = "uuid";
	private static final String ORGANIZATION_ID = "organizationId";
	private static final String PERSON_ID = "personId";
	private static final OffsetDateTime CREATED = OffsetDateTime.now().minusDays(1);
	private static final OffsetDateTime MODIFIED = OffsetDateTime.now();

	@BeforeAll
	static void setup() {
		registerValueGenerator(() -> now().plusDays(new Random().nextInt()), OffsetDateTime.class);
	}

	@Test
	void testBean() {
		assertThat(FeedbackSettings.class, allOf(
			hasValidBeanConstructor(),
			hasValidGettersAndSetters(),
			hasValidBeanHashCode(),
			hasValidBeanEquals(),
			hasValidBeanToString()));
	}

	@Test
	void testCreatePattern() {
		FeedbackSettings settings = FeedbackSettings.create()
				.withChannels(CHANNELS)
				.withId(ID)
				.withOrganizationId(ORGANIZATION_ID)
				.withPersonId(PERSON_ID)
				.withCreated(CREATED)
				.withModified(MODIFIED);
		
		assertThat(settings.getChannels()).isEqualTo(CHANNELS);
		assertThat(settings.getId()).isEqualTo(ID);
		assertThat(settings.getOrganizationId()).isEqualTo(ORGANIZATION_ID);
		assertThat(settings.getPersonId()).isEqualTo(PERSON_ID);
		assertThat(settings.getCreated()).isEqualTo(CREATED);
		assertThat(settings.getModified()).isEqualTo(MODIFIED);
	}

	@Test
	void testNoDirtOnCreatedBean() {
		assertThat(FeedbackSettings.create()).hasAllNullFieldsOrProperties();
		assertThat(new FeedbackSettings()).hasAllNullFieldsOrProperties();
	}
}
