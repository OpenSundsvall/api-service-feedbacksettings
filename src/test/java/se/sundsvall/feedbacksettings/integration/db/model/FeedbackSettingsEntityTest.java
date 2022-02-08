package se.sundsvall.feedbacksettings.integration.db.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

import org.junit.jupiter.api.Test;

class FeedbackSettingsEntityTest {

	private static final String ID = "id";
	private static final String PERSON_ID = "personId";
	private static final String ORGANIZATION_ID = "organizationId";
	private static final List<FeedbackChannelEmbeddable> FEEDBACK_CHANNELS = List.of(FeedbackChannelEmbeddable.create());
	private static final OffsetDateTime CREATED = OffsetDateTime.now().minusDays(5);
	private static final OffsetDateTime MODIFIED = OffsetDateTime.now();
	
	@Test
	void testSettersAndGetters() {
		FeedbackSettingsEntity entity = FeedbackSettingsEntity.create();
		
		assertThat(entity.getId()).isNull();
		assertThat(entity.getPersonId()).isNull();
		assertThat(entity.getOrganizationId()).isNull();
		assertThat(entity.getFeedbackChannels()).isNull();
		assertThat(entity.getCreated()).isNull();
		assertThat(entity.getModified()).isNull();

		entity.setId(ID);
		entity.setOrganizationId(ORGANIZATION_ID);
		entity.setPersonId(PERSON_ID);
		entity.setFeedbackChannels(FEEDBACK_CHANNELS);
		entity.setCreated(CREATED);
		entity.setModified(MODIFIED);
		
		assertThat(entity.getId()).isEqualTo(ID);
		assertThat(entity.getPersonId()).isEqualTo(PERSON_ID);
		assertThat(entity.getOrganizationId()).isEqualTo(ORGANIZATION_ID);
		assertThat(entity.getFeedbackChannels()).isEqualTo(FEEDBACK_CHANNELS);
		assertThat(entity.getCreated()).isEqualTo(CREATED);
		assertThat(entity.getModified()).isEqualTo(MODIFIED);
	}
	
	@Test
	void testEquals() {
		FeedbackSettingsEntity entityOne = FeedbackSettingsEntity.create()
				.withId(ID)
				.withPersonId(PERSON_ID)
				.withOrganizationId(ORGANIZATION_ID)
				.withFeedbackChannels(FEEDBACK_CHANNELS)
				.withCreated(CREATED)
				.withModified(MODIFIED);
		
		FeedbackSettingsEntity entityWithSameValuesAsOne = FeedbackSettingsEntity.create()
				.withId(ID)
				.withPersonId(PERSON_ID)
				.withOrganizationId(ORGANIZATION_ID)
				.withFeedbackChannels(FEEDBACK_CHANNELS)
				.withCreated(CREATED)
				.withModified(MODIFIED);

		FeedbackSettingsEntity entityTwo = FeedbackSettingsEntity.create()
				.withId(ID)
				.withPersonId(PERSON_ID)
				.withOrganizationId(ORGANIZATION_ID)
				.withFeedbackChannels(FEEDBACK_CHANNELS)
				.withCreated(OffsetDateTime.now())
				.withModified(OffsetDateTime.now());
		
		assertThat(entityOne)
			.isEqualTo(entityOne)
			.isEqualTo(entityWithSameValuesAsOne)
			.isNotEqualTo(null)
			.isNotEqualTo(new Object())
			.isNotEqualTo(entityTwo);
	}
	
	@Test
	void testHashCode() {
		FeedbackSettingsEntity entityOne = FeedbackSettingsEntity.create()
				.withId(ID)
				.withPersonId(PERSON_ID)
				.withOrganizationId(ORGANIZATION_ID)
				.withFeedbackChannels(FEEDBACK_CHANNELS)
				.withCreated(CREATED)
				.withModified(MODIFIED);
		
		FeedbackSettingsEntity entityWithSameValuesAsOne = FeedbackSettingsEntity.create()
				.withId(ID)
				.withPersonId(PERSON_ID)
				.withOrganizationId(ORGANIZATION_ID)
				.withFeedbackChannels(FEEDBACK_CHANNELS)
				.withCreated(CREATED)
				.withModified(MODIFIED);

		FeedbackSettingsEntity entityTwo = FeedbackSettingsEntity.create()
				.withId(ID)
				.withPersonId(PERSON_ID)
				.withOrganizationId(ORGANIZATION_ID)
				.withFeedbackChannels(FEEDBACK_CHANNELS)
				.withCreated(OffsetDateTime.now())
				.withModified(OffsetDateTime.now());
		
		assertThat(entityOne.hashCode())
			.isEqualTo(entityOne.hashCode())
			.isEqualTo(entityWithSameValuesAsOne.hashCode())
			.isNotEqualTo(null)
			.isNotEqualTo(new Object().hashCode())
			.isNotEqualTo(entityTwo.hashCode());
	}
	
	@Test
	void testToString() {
		FeedbackSettingsEntity entityOne = FeedbackSettingsEntity.create()
				.withId(ID)
				.withPersonId(PERSON_ID)
				.withOrganizationId(ORGANIZATION_ID)
				.withFeedbackChannels(FEEDBACK_CHANNELS)
				.withCreated(CREATED)
				.withModified(MODIFIED);
		
		FeedbackSettingsEntity entityWithSameValuesAsOne = FeedbackSettingsEntity.create()
				.withId(ID)
				.withPersonId(PERSON_ID)
				.withOrganizationId(ORGANIZATION_ID)
				.withFeedbackChannels(FEEDBACK_CHANNELS)
				.withCreated(CREATED)
				.withModified(MODIFIED);

		FeedbackSettingsEntity entityTwo = FeedbackSettingsEntity.create()
				.withId(ID)
				.withPersonId(PERSON_ID)
				.withOrganizationId(ORGANIZATION_ID)
				.withFeedbackChannels(FEEDBACK_CHANNELS)
				.withCreated(OffsetDateTime.now())
				.withModified(OffsetDateTime.now());
		
		assertThat(entityOne.toString())
			.isEqualTo(entityOne.toString())
			.isEqualTo(entityWithSameValuesAsOne.toString())
			.isNotEqualTo(null)
			.isNotEqualTo(new Object().toString())
			.isNotEqualTo(entityTwo.toString());
	}
	
	@Test
	void testCreatePattern() {
		FeedbackSettingsEntity entity = FeedbackSettingsEntity.create()
				.withId(ID)
				.withPersonId(PERSON_ID)
				.withOrganizationId(ORGANIZATION_ID)
				.withFeedbackChannels(FEEDBACK_CHANNELS)
				.withCreated(OffsetDateTime.now())
				.withModified(OffsetDateTime.now());

		assertThat(entity.getId()).isEqualTo(ID);
		assertThat(entity.getPersonId()).isEqualTo(PERSON_ID);
		assertThat(entity.getOrganizationId()).isEqualTo(ORGANIZATION_ID);
		assertThat(entity.getFeedbackChannels()).isEqualTo(FEEDBACK_CHANNELS);
		assertThat(entity.getCreated()).isCloseTo(OffsetDateTime.now(), within(1, ChronoUnit.SECONDS));
		assertThat(entity.getModified()).isCloseTo(OffsetDateTime.now(), within(1, ChronoUnit.SECONDS));
	}

	@Test
	void testPrePersist() {
		FeedbackSettingsEntity entity = FeedbackSettingsEntity.create();
		entity.prePersist();

		assertThat(entity).hasAllNullFieldsOrPropertiesExcept("created");
		assertThat(entity.getCreated()).isCloseTo(OffsetDateTime.now(), within(2, ChronoUnit.SECONDS));
	}

	@Test
	void testUpdate() {
		FeedbackSettingsEntity entity = FeedbackSettingsEntity.create();
		entity.preUpdate();

		assertThat(entity).hasAllNullFieldsOrPropertiesExcept("modified");
		assertThat(entity.getModified()).isCloseTo(OffsetDateTime.now(), within(2, ChronoUnit.SECONDS));
	}

	@Test
	void testNoDirtOnCreatedBean() {
		assertThat(new FeedbackSettingsEntity()).hasAllNullFieldsOrProperties();
		assertThat(FeedbackSettingsEntity.create()).hasAllNullFieldsOrProperties();
	}
}
