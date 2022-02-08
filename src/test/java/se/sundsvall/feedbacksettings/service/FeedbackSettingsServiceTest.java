package se.sundsvall.feedbacksettings.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.groups.Tuple.tuple;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static se.sundsvall.feedbacksettings.service.mapper.FeedbackSettingsMapper.toFeedbackSettings;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.panache.common.Page;
import se.sundsvall.feedbacksettings.ContactMethod;
import se.sundsvall.feedbacksettings.api.exception.ServiceException;
import se.sundsvall.feedbacksettings.api.model.CreateFeedbackSettingsRequest;
import se.sundsvall.feedbacksettings.api.model.FeedbackChannel;
import se.sundsvall.feedbacksettings.api.model.FeedbackSettings;
import se.sundsvall.feedbacksettings.api.model.RequestedFeedbackChannel;
import se.sundsvall.feedbacksettings.api.model.SearchResult;
import se.sundsvall.feedbacksettings.api.model.UpdateFeedbackSettingsRequest;
import se.sundsvall.feedbacksettings.integration.db.FeedbackSettingsRepository;
import se.sundsvall.feedbacksettings.integration.db.model.FeedbackChannelEmbeddable;
import se.sundsvall.feedbacksettings.integration.db.model.FeedbackSettingsEntity;

@ExtendWith(MockitoExtension.class)
class FeedbackSettingsServiceTest {

	private static final String FEEDBACK_SETTINGS_ID = "settingsId";
	private static final String MOBILE_NBR = "mobileNbr";
	private static final String EMAIL_ADDRESS = "emailAddress";
	private static final String PERSON_ID = "personId";
	private static final String ORGANIZATION_ID = "organizationId";
	private static final boolean SEND_FEEDBACK = true;
	private static final OffsetDateTime CREATED = OffsetDateTime.now().minusDays(1);
	private static final OffsetDateTime MODIFIED = OffsetDateTime.now();
	
	@Mock
	private FeedbackSettingsRepository repositoryMock;
	
	@Mock
	private FeedbackSettingsEntity entityMock;
	
	@Mock
	private PanacheQuery<FeedbackSettingsEntity> panacheQueryMock;
	
	@InjectMocks
	private FeedbackSettingsService service;
	
	@Captor
	private ArgumentCaptor<FeedbackSettingsEntity> entityCaptor;
	
	@Test
	void createFeedbackSettings() throws ServiceException {
		List<RequestedFeedbackChannel> channels = generateChannels();
		CreateFeedbackSettingsRequest request = CreateFeedbackSettingsRequest.create()
				.withPersonId(PERSON_ID)
				.withOrganizationId(ORGANIZATION_ID)
				.withChannels(channels);
				
		FeedbackSettings response = service.createFeedbackSettings(request);
		
		verify(repositoryMock).existsByPersonIdAndOrganizationId(PERSON_ID, ORGANIZATION_ID);
		verify(repositoryMock).persist(entityCaptor.capture());
		verifyNoMoreInteractions(repositoryMock);
		
		assertThat(entityCaptor.getValue().getFeedbackChannels())
			.hasSize(2)
			.extracting(FeedbackChannelEmbeddable::getContactMethod, FeedbackChannelEmbeddable::getDestination, FeedbackChannelEmbeddable::isSendFeedback)
			.containsExactlyInAnyOrder(
					tuple(ContactMethod.SMS, MOBILE_NBR, SEND_FEEDBACK), 
					tuple(ContactMethod.EMAIL, EMAIL_ADDRESS, SEND_FEEDBACK));
		assertThat(entityCaptor.getValue().getPersonId()).isEqualTo(PERSON_ID);
		assertThat(entityCaptor.getValue().getOrganizationId()).isEqualTo(ORGANIZATION_ID);
		
		assertThat(response.getPersonId()).isEqualTo(PERSON_ID);
		assertThat(response.getOrganizationId()).isEqualTo(ORGANIZATION_ID);
		assertThat(response.getChannels())
			.hasSize(2)
			.extracting(FeedbackChannel::getContactMethod, FeedbackChannel::getDestination, FeedbackChannel::isSendFeedback)
			.containsExactlyInAnyOrder(
					tuple(ContactMethod.SMS, MOBILE_NBR, SEND_FEEDBACK), 
					tuple(ContactMethod.EMAIL, EMAIL_ADDRESS, SEND_FEEDBACK));
	}

	@Test
	void createFeedbackSettingsForExistingId() {
		when(repositoryMock.existsByPersonIdAndOrganizationId(PERSON_ID, null)).thenReturn(true);
		
		CreateFeedbackSettingsRequest request = CreateFeedbackSettingsRequest.create().withPersonId(PERSON_ID);
		
		final var exception = assertThrows(ServiceException.class, 
				() -> service.createFeedbackSettings(request));

		assertThat(exception.getMessage()).isEqualTo("Settings already exist for personId 'personId'");
		
		verify(repositoryMock).existsByPersonIdAndOrganizationId(PERSON_ID, null);
		verifyNoMoreInteractions(repositoryMock);
	}
	
	@Test
	void updateFeedbackSettings() throws ServiceException {
		when(repositoryMock.findByIdOptional(FEEDBACK_SETTINGS_ID)).thenReturn(Optional.of(entityMock));
		when(entityMock.getId()).thenReturn(FEEDBACK_SETTINGS_ID);
		when(entityMock.getPersonId()).thenReturn(PERSON_ID);
		when(entityMock.getOrganizationId()).thenReturn(ORGANIZATION_ID);

		UpdateFeedbackSettingsRequest request = UpdateFeedbackSettingsRequest.create().withChannels(generateChannels());
		FeedbackSettings response = service.updateFeedbackSettings(FEEDBACK_SETTINGS_ID, request);
		
		verify(repositoryMock).findByIdOptional(FEEDBACK_SETTINGS_ID);
		verify(repositoryMock).persistAndFlush(entityCaptor.capture());
		verifyNoMoreInteractions(repositoryMock);
		
		assertThat(entityCaptor.getValue().getId()).isEqualTo(FEEDBACK_SETTINGS_ID);
		assertThat(entityCaptor.getValue().getPersonId()).isEqualTo(PERSON_ID);
		assertThat(entityCaptor.getValue().getOrganizationId()).isEqualTo(ORGANIZATION_ID);

		assertThat(response.getId()).isEqualTo(FEEDBACK_SETTINGS_ID);
		assertThat(response.getPersonId()).isEqualTo(PERSON_ID);
		assertThat(response.getOrganizationId()).isEqualTo(ORGANIZATION_ID);
	}

	@Test
	void updateFeedbackSettingsForNonExistingId() {
		UpdateFeedbackSettingsRequest request = UpdateFeedbackSettingsRequest.create();
		
		final var exception = assertThrows(ServiceException.class, 
				() -> service.updateFeedbackSettings(FEEDBACK_SETTINGS_ID, request));
		
		verify(repositoryMock).findByIdOptional(FEEDBACK_SETTINGS_ID);
		verifyNoMoreInteractions(repositoryMock);

		assertThat(exception.getMessage()).isEqualTo("No settings matching id 'settingsId' were found");
	}
	
	@Test
	void getFeedbackSettingsById() throws ServiceException {
		when(repositoryMock.findByIdOptional(FEEDBACK_SETTINGS_ID)).thenReturn(Optional.of(entityMock));
		when(entityMock.getId()).thenReturn(FEEDBACK_SETTINGS_ID);
		when(entityMock.getPersonId()).thenReturn(PERSON_ID);
		when(entityMock.getOrganizationId()).thenReturn(ORGANIZATION_ID);
		when(entityMock.getCreated()).thenReturn(CREATED);
		when(entityMock.getModified()).thenReturn(MODIFIED);

		FeedbackSettings response = service.getFeedbackSettingsById(FEEDBACK_SETTINGS_ID);
		
		verify(repositoryMock).findByIdOptional(FEEDBACK_SETTINGS_ID);
		verifyNoMoreInteractions(repositoryMock);
		
		assertThat(response).isEqualTo(toFeedbackSettings(entityMock));
	}

	@Test
	void getFeedbackSettingsByIdForNonExistingId() {
		when(repositoryMock.findByIdOptional(FEEDBACK_SETTINGS_ID)).thenReturn(Optional.empty());
		
		final var exception = assertThrows(ServiceException.class, 
				() -> service.getFeedbackSettingsById(FEEDBACK_SETTINGS_ID));

		verify(repositoryMock).findByIdOptional(FEEDBACK_SETTINGS_ID);
		verifyNoMoreInteractions(repositoryMock);

		assertThat(exception.getMessage()).isEqualTo("No settings matching id 'settingsId' were found");
	}
	
	@Test
	void getFeedbackSettingsForPersonId() {
		when(entityMock.getId()).thenReturn(FEEDBACK_SETTINGS_ID);
		when(entityMock.getPersonId()).thenReturn(PERSON_ID);
		when(repositoryMock.findByPersonIdAndOrganizationId(PERSON_ID, null)).thenReturn(panacheQueryMock);
		when(panacheQueryMock.page(any(Page.class))).thenReturn(panacheQueryMock);
		when(panacheQueryMock.page(anyInt(), anyInt())).thenReturn(panacheQueryMock);
		when(panacheQueryMock.list()).thenReturn(List.of(entityMock));
		when(panacheQueryMock.pageCount()).thenReturn(1);
		when(panacheQueryMock.count()).thenReturn(1L);
		
		SearchResult response = service.getFeedbackSettings(PERSON_ID, null, 1, 10);
		
		verify(repositoryMock).findByPersonIdAndOrganizationId(PERSON_ID, null);
		verify(panacheQueryMock).page(any(Page.class));
		verify(panacheQueryMock).page(0, 10);
		verifyNoMoreInteractions(repositoryMock);

		assertThat(response.getMetaData().getCount()).isEqualTo(1L);
		assertThat(response.getMetaData().getLimit()).isEqualTo(10);
		assertThat(response.getMetaData().getPage()).isEqualTo(1);
		assertThat(response.getMetaData().getTotalPages()).isEqualTo(1);
		assertThat(response.getMetaData().getTotalRecords()).isEqualTo(1L);
		assertThat(response.getFeedbackSettings()).containsExactly(toFeedbackSettings(entityMock));
	}

	@Test
	void deleteFeedbackSettings() throws ServiceException {
		when(repositoryMock.findByIdOptional(FEEDBACK_SETTINGS_ID)).thenReturn(Optional.of(entityMock));
		service.deleteFeedbackSettings(FEEDBACK_SETTINGS_ID);
		
		verify(repositoryMock).findByIdOptional(FEEDBACK_SETTINGS_ID);
		verify(repositoryMock).deleteById(FEEDBACK_SETTINGS_ID);
		
		verifyNoMoreInteractions(repositoryMock);
	}

	@Test
	void deleteFeedbackSettingsForNonExistingId() {
		when(repositoryMock.findByIdOptional(FEEDBACK_SETTINGS_ID)).thenReturn(Optional.empty());

		final var exception = assertThrows(ServiceException.class, 
				() -> service.deleteFeedbackSettings(FEEDBACK_SETTINGS_ID));

		verify(repositoryMock).findByIdOptional(FEEDBACK_SETTINGS_ID);
		verifyNoMoreInteractions(repositoryMock);

		assertThat(exception.getMessage()).isEqualTo("No settings matching id 'settingsId' were found");
		
	}
	
	private List<RequestedFeedbackChannel> generateChannels() {
		return List.of(
				RequestedFeedbackChannel.create()
						.withContactMethod(ContactMethod.SMS)
						.withDestination(MOBILE_NBR)
						.withSendFeedback(SEND_FEEDBACK),
				RequestedFeedbackChannel.create()
						.withContactMethod(ContactMethod.EMAIL)
						.withDestination(EMAIL_ADDRESS)
						.withSendFeedback(SEND_FEEDBACK));
	}
}
