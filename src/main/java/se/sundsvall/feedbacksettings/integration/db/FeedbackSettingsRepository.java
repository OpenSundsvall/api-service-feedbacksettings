package se.sundsvall.feedbacksettings.integration.db;

import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static se.sundsvall.feedbacksettings.integration.db.model.FeedbackSettingsEntity.FEEDBACK_SETTINGS_ORGANIZATION_ID_FILTER;
import static se.sundsvall.feedbacksettings.integration.db.model.FeedbackSettingsEntity.FEEDBACK_SETTINGS_PERSON_ID_FILTER;

import java.util.Map;
import static java.util.Objects.isNull;
import javax.enterprise.context.ApplicationScoped;

import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import se.sundsvall.feedbacksettings.integration.db.model.FeedbackSettingsEntity;

@ApplicationScoped
public class FeedbackSettingsRepository implements PanacheRepositoryBase<FeedbackSettingsEntity, String> {
	public PanacheQuery<FeedbackSettingsEntity> findByPersonIdAndOrganizationId(String personId, String organizationId) {
		var query = findAll();
		
		if (isNotBlank(organizationId)) {
			query.filter(FEEDBACK_SETTINGS_ORGANIZATION_ID_FILTER, Map.of("organizationIdFilter", organizationId));
		}
		if (isNotBlank(personId)) {
			query.filter(FEEDBACK_SETTINGS_PERSON_ID_FILTER, Map.of("personIdFilter", personId));
		}
		
		return query;
	}
	
	public boolean existsByPersonIdAndOrganizationId(String personId, String organizationId) {
		if (isNull(organizationId)) { 
			return count("person_id = ?1 and organization_id is null", personId) > 0; // Query for matching personal settings
		}
		return count("person_id = ?1 and organization_id = ?2", personId, organizationId) > 0; // Query for matching organization representative settings
	}
}
