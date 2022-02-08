package se.sundsvall.feedbacksettings.service;

import static java.util.Objects.isNull;
import static se.sundsvall.feedbacksettings.service.ServiceConstants.SETTINGS_ALREADY_EXISTS_FOR_ORGANIZATION_REPRESENTATIVE;
import static se.sundsvall.feedbacksettings.service.ServiceConstants.SETTINGS_ALREADY_EXISTS_FOR_PERSONID;
import static se.sundsvall.feedbacksettings.service.ServiceConstants.SETTINGS_NOT_FOUND_FOR_ID;
import static se.sundsvall.feedbacksettings.service.mapper.FeedbackSettingsMapper.mergeFeedbackSettings;
import static se.sundsvall.feedbacksettings.service.mapper.FeedbackSettingsMapper.toFeedbackSettings;
import static se.sundsvall.feedbacksettings.service.mapper.FeedbackSettingsMapper.toFeedbackSettingsEntity;

import java.util.Collections;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.core.Response.Status;

import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.panache.common.Page;
import se.sundsvall.feedbacksettings.api.exception.ServiceException;
import se.sundsvall.feedbacksettings.api.model.CreateFeedbackSettingsRequest;
import se.sundsvall.feedbacksettings.api.model.FeedbackSettings;
import se.sundsvall.feedbacksettings.api.model.MetaData;
import se.sundsvall.feedbacksettings.api.model.SearchResult;
import se.sundsvall.feedbacksettings.api.model.UpdateFeedbackSettingsRequest;
import se.sundsvall.feedbacksettings.integration.db.FeedbackSettingsRepository;
import se.sundsvall.feedbacksettings.integration.db.model.FeedbackSettingsEntity;

@ApplicationScoped
public class FeedbackSettingsService {
    @Inject
    FeedbackSettingsRepository feedbackSettingsRepository;

	@Transactional
	public FeedbackSettings createFeedbackSettings(CreateFeedbackSettingsRequest feedbackSettings) throws ServiceException {
		verifyNonExistingSettings(feedbackSettings.getPersonId(), feedbackSettings.getOrganizationId());

		FeedbackSettingsEntity entity = toFeedbackSettingsEntity(feedbackSettings);
		feedbackSettingsRepository.persist(entity);
		
		return toFeedbackSettings(entity);
	}

	@Transactional
	public FeedbackSettings updateFeedbackSettings(String id, UpdateFeedbackSettingsRequest feedbackSettings) throws ServiceException {
		FeedbackSettingsEntity entity = feedbackSettingsRepository.findByIdOptional(id)
				.orElseThrow(() -> ServiceException.create(String.format(SETTINGS_NOT_FOUND_FOR_ID, id), Status.NOT_FOUND, Status.NOT_FOUND));

		//Merge and persist incoming changes to existing entity 
		mergeFeedbackSettings(entity, feedbackSettings);
		feedbackSettingsRepository.persistAndFlush(entity);

		return toFeedbackSettings(entity);
	}

	public FeedbackSettings getFeedbackSettingsById(String id) throws ServiceException {
		FeedbackSettingsEntity entity = feedbackSettingsRepository.findByIdOptional(id)
				.orElseThrow(() -> ServiceException.create(String.format(SETTINGS_NOT_FOUND_FOR_ID, id), Status.NOT_FOUND, Status.NOT_FOUND));

		return toFeedbackSettings(entity);
	}

	public SearchResult getFeedbackSettings(String personId, String organizationId, int page, int limit) {
		PanacheQuery<FeedbackSettingsEntity> entities = feedbackSettingsRepository
				.findByPersonIdAndOrganizationId(personId, organizationId)
				.page(Page.ofSize(limit));
		
		// If page larger than last page is requested, a empty list is returned otherwise the current page
		List<FeedbackSettings> settings = entities.pageCount() < page ? Collections.emptyList() 
				: toFeedbackSettings(entities.page(page-1, limit).list()); // Paging with PanacheQuery starts with 0, API paging starts with 1 - hence the subtraction of 1
		
		return SearchResult.create()
				.withMetaData(MetaData.create()
						.withPage(page)
						.withTotalPages(entities.pageCount())
						.withTotalRecords(entities.count())
						.withCount(settings.size())
						.withLimit(limit))
				.withFeedbackSettings(settings); 
	}

	@Transactional
	public void deleteFeedbackSettings(String id) throws ServiceException {
		// Do a fetch to validate a setting for sent in id exists
		getFeedbackSettingsById(id);

		feedbackSettingsRepository.deleteById(id);
	}

	/**
	 * Method checks for existing settings for combination of sent in personId and organizationId
	 * @param personId personId
	 * @param organizationId organizationId
	 * @throws ServiceException if an existing match for the sent in parameters is found in the database
	 */
	private void verifyNonExistingSettings(String personId, String organizationId) throws ServiceException {
		if (feedbackSettingsRepository.existsByPersonIdAndOrganizationId(personId, organizationId)) {
			throw ServiceException.create(
					isNull(organizationId) ? 
							String.format(SETTINGS_ALREADY_EXISTS_FOR_PERSONID, personId) :
							String.format(SETTINGS_ALREADY_EXISTS_FOR_ORGANIZATION_REPRESENTATIVE, personId, organizationId),
					Status.BAD_REQUEST, Status.CONFLICT);
		}
	}
}
