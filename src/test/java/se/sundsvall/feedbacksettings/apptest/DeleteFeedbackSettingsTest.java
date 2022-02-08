package se.sundsvall.feedbacksettings.apptest;

import static org.assertj.core.api.Assertions.assertThat;

import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.HttpMethod;
import javax.ws.rs.core.Response.Status;

import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;
import se.sundsvall.feedbacksettings.integration.db.FeedbackSettingsRepository;

/**
 * Delete feedback settings application tests
 * 
 * @see src/test/resources/db/testdata.sql for data setup.
 */
@QuarkusTest
class DeleteFeedbackSettingsTest extends AbstractAppTest {
	private static final String PATH = "/settings/";
	private static final String RESPONSE_FILE = "response.json";

	private static final String RESET_SETTINGS = "INSERT INTO feedbacksettings.feedback_settings(id, person_id, created) VALUES ('9a24743c-5c19-4774-954e-a3ad67a734e1', '49a974ea-9137-419b-bcb9-ad74c81a1d1f', '2022-01-10 10:00:00.000');";
	private static final String RESET_CHANNELS = "INSERT INTO feedbacksettings.feedback_channels(setting_id, contact_method, destination, send_feedback) VALUES ('9a24743c-5c19-4774-954e-a3ad67a734e1', 'SMS', '0706100001', true);";

	@Inject
	FeedbackSettingsRepository repository;
	
	@Test
	@Transactional
	void test1_delete() throws Exception { //NOSONAR
		final var id = "9a24743c-5c19-4774-954e-a3ad67a734e1";
		
		try {
			setupCall()
				.withServicePath(PATH.concat(id))
				.withHttpMethod(HttpMethod.DELETE)
				.withExpectedResponseStatus(Status.NO_CONTENT)
				.withExpectedResponseBodyIsNullOrEmpty()
				.sendRequestAndVerifyResponse();
			
			assertThat(repository.findByIdOptional(id)).isEmpty();
		} finally {
			//Reset deleted post
			repository.getEntityManager().createNativeQuery(RESET_SETTINGS).executeUpdate();
			repository.getEntityManager().createNativeQuery(RESET_CHANNELS).executeUpdate();
		}
	}

	@Test
	void test2_deleteNonExistingId() throws Exception { //NOSONAR
		final var id = "9a24743c-5c19-4774-954e-a3ad67a734e0";

		setupCall()
		.withServicePath(PATH.concat(id))
		.withHttpMethod(HttpMethod.DELETE)
			.withExpectedResponseStatus(Status.NOT_FOUND)
			.withExpectedResponse(RESPONSE_FILE)
			.sendRequestAndVerifyResponse();
	}
}
