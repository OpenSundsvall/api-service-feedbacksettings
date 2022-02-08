package se.sundsvall.feedbacksettings.integration.db;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.groups.Tuple.tuple;

import java.util.List;

import javax.inject.Inject;

import org.junit.jupiter.api.Test;

import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.test.TestTransaction;
import io.quarkus.test.junit.QuarkusTest;
import se.sundsvall.feedbacksettings.integration.db.model.FeedbackSettingsEntity;

/**
 * Feedback repository tests.
 * 
 * @see src/test/resources/db/testdata.sql for data setup.
 */
@QuarkusTest
@TestTransaction
class FeedbackSettingsRepositoryTest {

	private static final String EXISTING_PRIVATE_PERSON_ID = "49a974ea-9137-419b-bcb9-ad74c81a1d2f";
	private static final String EXISTING_ORGANIZATION_REPRESENTATIVE_PERSON_ID = "49a974ea-9137-419b-bcb9-ad74c81a1d4f";
	private static final String EXISTING_ORGANIZATION_ID = "15aee472-46ab-4f03-9605-68bd64ebc84a";
	private static final String NON_EXISTING_PERSON_ID = "49a974ea-9137-419b-bcb9-ad74c81d9f5a7";
	private static final String NON_EXISTING_ORGANIZATION_ID = "3be40bbc-de14-4a30-be9a-f16774d443b3";
	
	@Inject
	FeedbackSettingsRepository feedbackRepository;

	@Test
	void customerSettingsExists() {
		assertThat(feedbackRepository.existsByPersonIdAndOrganizationId(EXISTING_PRIVATE_PERSON_ID, null)).isTrue();
		assertThat(feedbackRepository.existsByPersonIdAndOrganizationId(NON_EXISTING_PERSON_ID, null)).isFalse();
	}

	@Test
	void organizationRepresentativeSettingsExists() {
		assertThat(feedbackRepository.existsByPersonIdAndOrganizationId(EXISTING_ORGANIZATION_REPRESENTATIVE_PERSON_ID, EXISTING_ORGANIZATION_ID)).isTrue();
		assertThat(feedbackRepository.existsByPersonIdAndOrganizationId(EXISTING_PRIVATE_PERSON_ID, EXISTING_ORGANIZATION_ID)).isFalse();
	}

	@Test
	void findByPersonIdAndOrganizationIdWithNoFilters() {
		PanacheQuery<FeedbackSettingsEntity> query = feedbackRepository.findByPersonIdAndOrganizationId(null, null);
		List<FeedbackSettingsEntity> entities = query.list();

		assertThat(query.count()).isEqualTo(5);
		assertThat(entities)
			.hasSize(5)
			.extracting(FeedbackSettingsEntity::getPersonId, FeedbackSettingsEntity::getOrganizationId)
			.containsExactlyInAnyOrder(
					tuple("49a974ea-9137-419b-bcb9-ad74c81a1d1f", null),
					tuple("49a974ea-9137-419b-bcb9-ad74c81a1d2f", null),
					tuple("49a974ea-9137-419b-bcb9-ad74c81a1d3f", null),
					tuple("49a974ea-9137-419b-bcb9-ad74c81a1d3f", "15aee472-46ab-4f03-9605-68bd64ebc84a"),
					tuple("49a974ea-9137-419b-bcb9-ad74c81a1d4f", "15aee472-46ab-4f03-9605-68bd64ebc84a"));
	}

	@Test
	void findByPersonId() {
		PanacheQuery<FeedbackSettingsEntity> query = feedbackRepository.findByPersonIdAndOrganizationId(EXISTING_PRIVATE_PERSON_ID, null);
		List<FeedbackSettingsEntity> entities = query.list();
		
		assertThat(query.count()).isEqualTo(1);
		assertThat(entities)
				.hasSize(1)
				.extracting(FeedbackSettingsEntity::getPersonId, FeedbackSettingsEntity::getOrganizationId)
				.containsExactly(
						tuple("49a974ea-9137-419b-bcb9-ad74c81a1d2f", null));
	}

	@Test
	void findByOrganizationId() {
		PanacheQuery<FeedbackSettingsEntity> query = feedbackRepository.findByPersonIdAndOrganizationId(null, EXISTING_ORGANIZATION_ID);
		List<FeedbackSettingsEntity> entities = query.list();

		assertThat(query.count()).isEqualTo(2);
		assertThat(entities)
			.hasSize(2)
			.extracting(FeedbackSettingsEntity::getPersonId, FeedbackSettingsEntity::getOrganizationId)
			.containsExactlyInAnyOrder(
					tuple("49a974ea-9137-419b-bcb9-ad74c81a1d3f", "15aee472-46ab-4f03-9605-68bd64ebc84a"), 
					tuple("49a974ea-9137-419b-bcb9-ad74c81a1d4f", "15aee472-46ab-4f03-9605-68bd64ebc84a"));
	}

	@Test
	void findByPersonIdAndOrganizationIdWithAllFilters() {
		PanacheQuery<FeedbackSettingsEntity> query = feedbackRepository.findByPersonIdAndOrganizationId(EXISTING_ORGANIZATION_REPRESENTATIVE_PERSON_ID, EXISTING_ORGANIZATION_ID);

		List<FeedbackSettingsEntity> entities = query.list();
		
		assertThat(query.count()).isEqualTo(1);
		assertThat(entities)
				.hasSize(1)
				.extracting(FeedbackSettingsEntity::getPersonId, FeedbackSettingsEntity::getOrganizationId)
				.containsExactly(
						tuple("49a974ea-9137-419b-bcb9-ad74c81a1d4f", "15aee472-46ab-4f03-9605-68bd64ebc84a"));
	}

	@Test
	void findByCombinationThatResultsInNoMatches() {
		PanacheQuery<FeedbackSettingsEntity> query = feedbackRepository.findByPersonIdAndOrganizationId(EXISTING_PRIVATE_PERSON_ID, EXISTING_ORGANIZATION_ID);
		
		assertThat(query.count()).isZero();
		assertThat(query.list()).isEmpty();

		query = feedbackRepository.findByPersonIdAndOrganizationId(EXISTING_ORGANIZATION_REPRESENTATIVE_PERSON_ID, NON_EXISTING_ORGANIZATION_ID);

		assertThat(query.count()).isZero();
		assertThat(query.list()).isEmpty();
}
}
