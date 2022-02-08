package se.sundsvall.feedbacksettings.apptest;

import static java.util.Objects.nonNull;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.HttpMethod;
import javax.ws.rs.core.Response.Status;

import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;
import net.javacrumbs.jsonunit.core.Option;
import se.sundsvall.feedbacksettings.api.model.FeedbackSettings;
import se.sundsvall.feedbacksettings.integration.db.FeedbackSettingsRepository;

/**
 * Create feedback settings application tests
 * 
 * @see src/test/resources/db/testdata.sql for data setup.
 */
@QuarkusTest
class CreateFeedbackSettingsTest extends AbstractAppTest {
	private static final String PATH = "/settings";
	private static final String REQUEST_FILE = "request.json";
	private static final String RESPONSE_FILE = "response.json";
	private static final String HEADER_LOCATION = "location";
	
	// Regexp matching 'http[s]://[any string larger than 1 char]/settings/[valid UUID format]'
	private static final String UUID_REGEXP = "https?:\\/\\/\\S+\\/settings\\/[0-9a-f]{8}-[0-9a-f]{4}-[0-5][0-9a-f]{3}-[089ab][0-9a-f]{3}-[0-9a-f]{12}";

	@Inject
	FeedbackSettingsRepository repository;
	
	@Test
	@Transactional
	void test1_createPersonalSetting() throws Exception { //NOSONAR
		FeedbackSettings response = null;
		
		try {
			response = setupCall()
				.withServicePath(PATH)
				.withHttpMethod(HttpMethod.POST)
				.withRequest(REQUEST_FILE)
				.withOptions(List.of(Option.IGNORING_ARRAY_ORDER))
				.withExpectedResponseStatus(Status.CREATED)
				.withExpectedResponseHeader(HEADER_LOCATION, List.of(UUID_REGEXP))
				.withExpectedResponse(RESPONSE_FILE)
				.sendRequestAndVerifyResponse()
				.andReturnBody(FeedbackSettings.class);
			
			assertThat(repository.findByIdOptional(response.getId())).isPresent();
		} finally {
			//Clean up after test
			if (nonNull(response)) {
				repository.deleteById(response.getId());
			}
		}
	}

	@Test
	@Transactional
	void test2_createRepresentativeSetting() throws Exception { //NOSONAR
		FeedbackSettings response = null;

		try {
			response = setupCall()
				.withServicePath(PATH)
				.withHttpMethod(HttpMethod.POST)
				.withRequest(REQUEST_FILE)
				.withOptions(List.of(Option.IGNORING_ARRAY_ORDER))
				.withExpectedResponseStatus(Status.CREATED)
				.withExpectedResponseHeader(HEADER_LOCATION, List.of(UUID_REGEXP))
				.withExpectedResponse(RESPONSE_FILE)
				.sendRequestAndVerifyResponse()
				.andReturnBody(FeedbackSettings.class);
			
			assertThat(repository.findByIdOptional(response.getId())).isPresent();
		} finally {
			//Clean up after test
			if (nonNull(response)) {
				repository.deleteById(response.getId());
			}
		}
	}
	
	@Test
	@Transactional
	void test3_createWithoutChannels() throws Exception { //NOSONAR
		FeedbackSettings response = null;

		try {
			response = setupCall()
				.withServicePath(PATH)
				.withHttpMethod(HttpMethod.POST)
				.withRequest(REQUEST_FILE)
				.withOptions(List.of(Option.IGNORING_ARRAY_ORDER))
				.withExpectedResponseStatus(Status.CREATED)
				.withExpectedResponseHeader(HEADER_LOCATION, List.of(UUID_REGEXP))
				.withExpectedResponse(RESPONSE_FILE)
				.sendRequestAndVerifyResponse()
				.andReturnBody(FeedbackSettings.class);
			
			assertThat(repository.findByIdOptional(response.getId())).isPresent();
		} finally {
			//Clean up after test
			if (nonNull(response)) {
				repository.deleteById(response.getId());
			}
		}
	}
	
	@Test
	void test4_createForExistingId() throws Exception { //NOSONAR

		setupCall()
			.withServicePath(PATH)
			.withHttpMethod(HttpMethod.POST)
			.withRequest(REQUEST_FILE)
			.withOptions(List.of(Option.IGNORING_ARRAY_ORDER))
			.withExpectedResponseStatus(Status.BAD_REQUEST)
			.withExpectedResponse(RESPONSE_FILE)
			.sendRequestAndVerifyResponse();
	}
}
