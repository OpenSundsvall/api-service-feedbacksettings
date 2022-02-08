package se.sundsvall.feedbacksettings.service.mapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;
import static org.assertj.core.groups.Tuple.tuple;

import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;

import se.sundsvall.feedbacksettings.ContactMethod;
import se.sundsvall.feedbacksettings.api.exception.ServiceException;
import se.sundsvall.feedbacksettings.api.model.CreateFeedbackSettingsRequest;
import se.sundsvall.feedbacksettings.api.model.FeedbackChannel;
import se.sundsvall.feedbacksettings.api.model.RequestedFeedbackChannel;
import se.sundsvall.feedbacksettings.api.model.UpdateFeedbackSettingsRequest;
import se.sundsvall.feedbacksettings.integration.db.model.FeedbackChannelEmbeddable;
import se.sundsvall.feedbacksettings.integration.db.model.FeedbackSettingsEntity;

class FeedbackSettingsMapperTest {
	private static final String FEEDBACK_SETTING_ID = "settingId";
	private static final String EMAIL_ADDRESS = "emailAddress";
	private static final String MOBILE_NUMBER = "mobileNumber";
	private static final String PERSON_ID = "personId";
	private static final String ORGANIZATION_ID = "organizationId";
	private static final Boolean SEND_FEEDBACK = Boolean.TRUE;
	private static final OffsetDateTime CREATED = OffsetDateTime.now().minusDays(5L);
	private static final OffsetDateTime MODIFIED = OffsetDateTime.now().minusDays(2L);
	
	@Test
	void toFeedbackSettingsEntityFromCreateRequest() {
		final var postRequest = CreateFeedbackSettingsRequest.create()
				.withPersonId(PERSON_ID)
				.withOrganizationId(ORGANIZATION_ID)
				.withChannels(List.of(
						RequestedFeedbackChannel.create()
								.withContactMethod(ContactMethod.EMAIL)
								.withDestination(EMAIL_ADDRESS)
								.withSendFeedback(SEND_FEEDBACK),
						RequestedFeedbackChannel.create()
								.withContactMethod(ContactMethod.SMS)
								.withDestination(MOBILE_NUMBER)
								.withSendFeedback(SEND_FEEDBACK)));
		
		final var entity = FeedbackSettingsMapper.toFeedbackSettingsEntity(postRequest);
		
		assertThat(entity).isNotNull();
		assertThat(entity.getId()).isNull();
		assertThat(entity.getCreated()).isNull();
		assertThat(entity.getModified()).isNull();
		assertThat(entity.getPersonId()).isEqualTo(PERSON_ID);
		assertThat(entity.getOrganizationId()).isEqualTo(ORGANIZATION_ID);
		assertThat(entity.getFeedbackChannels())
		.hasSize(2)
		.extracting(FeedbackChannelEmbeddable::getContactMethod, FeedbackChannelEmbeddable::getDestination, FeedbackChannelEmbeddable::isSendFeedback)
		.containsExactlyInAnyOrder(
				tuple(ContactMethod.SMS, MOBILE_NUMBER, SEND_FEEDBACK), 
				tuple(ContactMethod.EMAIL, EMAIL_ADDRESS, SEND_FEEDBACK));
	}

	@Test
	void toFeedbackSettingsEntityFromNullPostRequest() {
		final var entity = FeedbackSettingsMapper.toFeedbackSettingsEntity(null);
		
		assertThat(entity).isNull();
	}

	@Test
	void toFeedbackSettingsFromEntity() {
		final var entity = generateEntity();
		final var settings = FeedbackSettingsMapper.toFeedbackSettings(entity);
		
		assertThat(settings).isNotNull();
		assertThat(settings.getId()).isEqualTo(FEEDBACK_SETTING_ID);
		assertThat(settings.getPersonId()).isEqualTo(PERSON_ID);
		assertThat(settings.getOrganizationId()).isEqualTo(ORGANIZATION_ID);
		
		assertThat(settings.getChannels())
		.hasSize(2)
		.extracting(FeedbackChannel::getContactMethod, FeedbackChannel::getDestination, FeedbackChannel::isSendFeedback)
		.containsExactlyInAnyOrder(
				tuple(ContactMethod.SMS, MOBILE_NUMBER, SEND_FEEDBACK), 
				tuple(ContactMethod.EMAIL, EMAIL_ADDRESS, SEND_FEEDBACK));
		assertThat(settings.getCreated()).isEqualTo(CREATED);
		assertThat(settings.getModified()).isEqualTo(MODIFIED);
	}

	@Test
	void toFeedbackSettingsFromNullEntity() {
		final var settings = FeedbackSettingsMapper.toFeedbackSettings((FeedbackSettingsEntity)null);
		
		assertThat(settings).isNull();
	}

	@Test
	void toFeedbackSettingsFromEntities() {
		final var entities = List.of(generateEntity(), generateEntity(), generateEntity());
		final var settings = FeedbackSettingsMapper.toFeedbackSettings(entities);
		
		assertThat(settings).isNotNull().hasSize(3);
		
		for (int i=0; i<settings.size(); i++) {
			assertThat(settings.get(i).getId()).isEqualTo(FEEDBACK_SETTING_ID);
			assertThat(settings.get(i).getPersonId()).isEqualTo(PERSON_ID);
			assertThat(settings.get(i).getOrganizationId()).isEqualTo(ORGANIZATION_ID);
			assertThat(settings.get(i).getChannels())
			.hasSize(2)
			.extracting(FeedbackChannel::getContactMethod, FeedbackChannel::getDestination, FeedbackChannel::isSendFeedback)
			.containsExactlyInAnyOrder(
					tuple(ContactMethod.SMS, MOBILE_NUMBER, SEND_FEEDBACK), 
					tuple(ContactMethod.EMAIL, EMAIL_ADDRESS, SEND_FEEDBACK));
			assertThat(settings.get(i).getCreated()).isEqualTo(CREATED);
			assertThat(settings.get(i).getModified()).isEqualTo(MODIFIED);
		}
	}
	
	@Test
	void toFeedbackSettingsFromNullEntities() {
		final var settings = FeedbackSettingsMapper.toFeedbackSettings((List<FeedbackSettingsEntity>)null);
		
		assertThat(settings).isNotNull().isEmpty();
	}

	@Test
	void toFeedbackSettingsFromEmptyList() {
		final var settings = FeedbackSettingsMapper.toFeedbackSettings(Collections.emptyList());
		
		assertThat(settings).isNotNull().isEmpty();
	}
	
	@Test
	void mergeFeedbackSettingsWithExistingChannelsChanged() throws ServiceException {
		final var entity = generateEntity();

		final var request = UpdateFeedbackSettingsRequest.create()
				.withChannels(List.of(
						RequestedFeedbackChannel.create()
								.withContactMethod(ContactMethod.EMAIL)
								.withDestination(EMAIL_ADDRESS.concat("updated"))
								.withSendFeedback(!SEND_FEEDBACK),
						RequestedFeedbackChannel.create()
								.withContactMethod(ContactMethod.SMS)
								.withDestination(MOBILE_NUMBER.concat("updated"))
								.withSendFeedback(!SEND_FEEDBACK)));
		
		FeedbackSettingsMapper.mergeFeedbackSettings(entity, request) ;
		
		//Only feedback attributes in Entity should be changed (plus modified date)
		assertThat(entity.getId()).isEqualTo(FEEDBACK_SETTING_ID); 
		assertThat(entity.getPersonId()).isEqualTo(PERSON_ID);
		assertThat(entity.getOrganizationId()).isEqualTo(ORGANIZATION_ID);
		assertThat(entity.getCreated()).isEqualTo(CREATED); 
		assertThat(entity.getModified()).isNotEqualTo(MODIFIED);
		assertThat(entity.getModified()).isCloseTo(OffsetDateTime.now(), within(2, ChronoUnit.SECONDS));
		assertThat(entity.getFeedbackChannels())
		.hasSize(2)
		.extracting(FeedbackChannelEmbeddable::getContactMethod, FeedbackChannelEmbeddable::getDestination, FeedbackChannelEmbeddable::isSendFeedback)
		.containsExactlyInAnyOrder(
				tuple(ContactMethod.SMS, MOBILE_NUMBER.concat("updated"), !SEND_FEEDBACK), 
				tuple(ContactMethod.EMAIL, EMAIL_ADDRESS.concat("updated"), !SEND_FEEDBACK));
	}

	@Test
	void mergeFeedbackSettingsWithRemovedChannel() throws ServiceException {
		final var entity = generateEntity();

		final var request = UpdateFeedbackSettingsRequest.create()
				.withChannels(List.of(
						RequestedFeedbackChannel.create()
								.withContactMethod(ContactMethod.EMAIL)
								.withDestination(EMAIL_ADDRESS)
								.withSendFeedback(SEND_FEEDBACK)));
		
		FeedbackSettingsMapper.mergeFeedbackSettings(entity, request) ;
		
		//Only feedback attributes in Entity should be changed (plus modified date)
		assertThat(entity.getId()).isEqualTo(FEEDBACK_SETTING_ID); 
		assertThat(entity.getPersonId()).isEqualTo(PERSON_ID);
		assertThat(entity.getOrganizationId()).isEqualTo(ORGANIZATION_ID);
		assertThat(entity.getCreated()).isEqualTo(CREATED); 
		assertThat(entity.getModified()).isNotEqualTo(MODIFIED);
		assertThat(entity.getModified()).isCloseTo(OffsetDateTime.now(), within(2, ChronoUnit.SECONDS));
		assertThat(entity.getFeedbackChannels())
		.hasSize(1)
		.extracting(FeedbackChannelEmbeddable::getContactMethod, FeedbackChannelEmbeddable::getDestination, FeedbackChannelEmbeddable::isSendFeedback)
		.containsExactly(
				tuple(ContactMethod.EMAIL, EMAIL_ADDRESS, SEND_FEEDBACK));
	}

	@Test
	void mergeFeedbackSettingsWithAddedChannel() throws ServiceException {
		final var entity = generateEntity();

		final var request = UpdateFeedbackSettingsRequest.create()
				.withChannels(List.of(
						RequestedFeedbackChannel.create()
								.withContactMethod(ContactMethod.EMAIL)
								.withDestination(EMAIL_ADDRESS)
								.withSendFeedback(SEND_FEEDBACK),
						RequestedFeedbackChannel.create()
								.withContactMethod(ContactMethod.SMS)
								.withDestination(MOBILE_NUMBER)
								.withSendFeedback(SEND_FEEDBACK),
						RequestedFeedbackChannel.create()
								.withContactMethod(ContactMethod.SMS)
								.withDestination(MOBILE_NUMBER.concat("2"))
								.withSendFeedback(SEND_FEEDBACK)));
		
		FeedbackSettingsMapper.mergeFeedbackSettings(entity, request) ;
		
		//Only feedback attributes in Entity should be changed (plus modified date)
		assertThat(entity.getId()).isEqualTo(FEEDBACK_SETTING_ID); 
		assertThat(entity.getPersonId()).isEqualTo(PERSON_ID);
		assertThat(entity.getOrganizationId()).isEqualTo(ORGANIZATION_ID);
		assertThat(entity.getCreated()).isEqualTo(CREATED); 
		assertThat(entity.getModified()).isNotEqualTo(MODIFIED);
		assertThat(entity.getModified()).isCloseTo(OffsetDateTime.now(), within(2, ChronoUnit.SECONDS));
		assertThat(entity.getFeedbackChannels())
		.hasSize(3)
		.extracting(FeedbackChannelEmbeddable::getContactMethod, FeedbackChannelEmbeddable::getDestination, FeedbackChannelEmbeddable::isSendFeedback)
		.containsExactly(
				tuple(ContactMethod.EMAIL, EMAIL_ADDRESS, SEND_FEEDBACK),
				tuple(ContactMethod.SMS, MOBILE_NUMBER, SEND_FEEDBACK),
				tuple(ContactMethod.SMS, MOBILE_NUMBER.concat("2"), SEND_FEEDBACK));
	}

	@Test
	void mergeFeedbackSettingsWithNoChanges() throws ServiceException {
		final var entity = generateEntity();

		final var request = UpdateFeedbackSettingsRequest.create()
				.withChannels(List.of(
						RequestedFeedbackChannel.create()
								.withContactMethod(ContactMethod.SMS)
								.withDestination(MOBILE_NUMBER)
								.withSendFeedback(SEND_FEEDBACK),
 						RequestedFeedbackChannel.create()
								.withContactMethod(ContactMethod.EMAIL)
								.withDestination(EMAIL_ADDRESS)
								.withSendFeedback(SEND_FEEDBACK)));
		
		FeedbackSettingsMapper.mergeFeedbackSettings(entity, request) ;
		
		//Entity should be untouched
		assertThat(entity.getId()).isEqualTo(FEEDBACK_SETTING_ID); 
		assertThat(entity.getPersonId()).isEqualTo(PERSON_ID);
		assertThat(entity.getOrganizationId()).isEqualTo(ORGANIZATION_ID);
		assertThat(entity.getCreated()).isEqualTo(CREATED); 
		assertThat(entity.getModified()).isEqualTo(MODIFIED);
		assertThat(entity.getFeedbackChannels())
		.hasSize(2)
		.extracting(FeedbackChannelEmbeddable::getContactMethod, FeedbackChannelEmbeddable::getDestination, FeedbackChannelEmbeddable::isSendFeedback)
		.containsExactlyInAnyOrder(
				tuple(ContactMethod.SMS, MOBILE_NUMBER, SEND_FEEDBACK),
				tuple(ContactMethod.EMAIL, EMAIL_ADDRESS, SEND_FEEDBACK));
	}

	@Test
	void mergeFeedbackSettingsFromNullSettings() throws ServiceException {
		final var entity = generateEntity();
		FeedbackSettingsMapper.mergeFeedbackSettings(entity, null) ;

		//Entity should be untouched
		assertThat(entity.getId()).isEqualTo(FEEDBACK_SETTING_ID); 
		assertThat(entity.getPersonId()).isEqualTo(PERSON_ID);
		assertThat(entity.getOrganizationId()).isEqualTo(ORGANIZATION_ID);
		assertThat(entity.getCreated()).isEqualTo(CREATED); 
		assertThat(entity.getModified()).isEqualTo(MODIFIED);
		assertThat(entity.getFeedbackChannels())
		.hasSize(2)
		.extracting(FeedbackChannelEmbeddable::getContactMethod, FeedbackChannelEmbeddable::getDestination, FeedbackChannelEmbeddable::isSendFeedback)
		.containsExactlyInAnyOrder(
				tuple(ContactMethod.SMS, MOBILE_NUMBER, SEND_FEEDBACK),
				tuple(ContactMethod.EMAIL, EMAIL_ADDRESS, SEND_FEEDBACK));
	}

	@Test
	void mergeFeedbackSettingsFromNullList() throws ServiceException {
		final var entity = generateEntity();
		FeedbackSettingsMapper.mergeFeedbackSettings(entity, UpdateFeedbackSettingsRequest.create()) ;

		//Entity should be untouched
		assertThat(entity.getId()).isEqualTo(FEEDBACK_SETTING_ID); 
		assertThat(entity.getPersonId()).isEqualTo(PERSON_ID);
		assertThat(entity.getOrganizationId()).isEqualTo(ORGANIZATION_ID);
		assertThat(entity.getCreated()).isEqualTo(CREATED); 
		assertThat(entity.getModified()).isEqualTo(MODIFIED);
		assertThat(entity.getFeedbackChannels())
		.hasSize(2)
		.extracting(FeedbackChannelEmbeddable::getContactMethod, FeedbackChannelEmbeddable::getDestination, FeedbackChannelEmbeddable::isSendFeedback)
		.containsExactlyInAnyOrder(
				tuple(ContactMethod.SMS, MOBILE_NUMBER, SEND_FEEDBACK),
				tuple(ContactMethod.EMAIL, EMAIL_ADDRESS, SEND_FEEDBACK));
	}

	private FeedbackSettingsEntity generateEntity() {
		return FeedbackSettingsEntity.create()
				.withId(FEEDBACK_SETTING_ID)
				.withCreated(CREATED)
				.withModified(MODIFIED)
				.withPersonId(PERSON_ID)
				.withOrganizationId(ORGANIZATION_ID)
				.withFeedbackChannels(new ArrayList<FeedbackChannelEmbeddable>(List.of(
					FeedbackChannelEmbeddable.create()
							.withContactMethod(ContactMethod.EMAIL)
							.withDestination(EMAIL_ADDRESS)
							.withSendFeedback(SEND_FEEDBACK),
					FeedbackChannelEmbeddable.create()
							.withContactMethod(ContactMethod.SMS)
							.withDestination(MOBILE_NUMBER)
							.withSendFeedback(SEND_FEEDBACK)
				)));
	}
}