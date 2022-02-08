package se.sundsvall.feedbacksettings.apptest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

import javax.inject.Inject;
import javax.ws.rs.HttpMethod;
import javax.ws.rs.core.Response.Status;

import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;
import net.javacrumbs.jsonunit.core.Option;
import se.sundsvall.feedbacksettings.integration.db.FeedbackSettingsRepository;

/**
 * Update feedback settings application tests
 * 
 * @see src/test/resources/db/testdata.sql for data setup.
 */
@QuarkusTest
class UpdateFeedbackSettingsTest extends AbstractAppTest {
	private static final String PATH = "/settings/";
	private static final String REQUEST_FILE = "request.json";
	private static final String RESPONSE_FILE = "response.json";

	@Inject
	FeedbackSettingsRepository repository;

	@Test
	void test1_updatePersonalSetting() throws Exception { //NOSONAR
		final var id = "9a24743c-5c19-4774-954e-a3ad67a734e2";
		
		setupCall()
			.withServicePath(PATH.concat(id))
			.withHttpMethod(HttpMethod.PATCH)
			.withRequest(REQUEST_FILE)
			.withOptions(List.of(Option.IGNORING_ARRAY_ORDER))
			.withExpectedResponseStatus(Status.OK)
			.withExpectedResponse(RESPONSE_FILE)
			.sendRequestAndVerifyResponse();
		
		assertThat(repository.findById(id).getModified()).isCloseTo(OffsetDateTime.now(), within(2,  ChronoUnit.SECONDS));
	}

	@Test
	void test2_updateRepresentativeSetting() throws Exception { //NOSONAR
		final var id = "9a24743c-5c19-4774-954e-a3ad67a734e5";

		setupCall()
			.withServicePath(PATH.concat(id))
			.withHttpMethod(HttpMethod.PATCH)
			.withRequest(REQUEST_FILE)
			.withOptions(List.of(Option.IGNORING_ARRAY_ORDER))
			.withExpectedResponseStatus(Status.OK)
			.withExpectedResponse(RESPONSE_FILE)
			.sendRequestAndVerifyResponse();
		
		assertThat(repository.findById(id).getModified()).isCloseTo(OffsetDateTime.now(), within(2,  ChronoUnit.SECONDS));
	}
	
	@Test
	void test3_addContactChannel() throws Exception { //NOSONAR
		final var id = "9a24743c-5c19-4774-954e-a3ad67a734e2";
		
		setupCall()
			.withServicePath(PATH.concat(id))
			.withHttpMethod(HttpMethod.PATCH)
			.withRequest(REQUEST_FILE)
			.withOptions(List.of(Option.IGNORING_ARRAY_ORDER))
			.withExpectedResponseStatus(Status.OK)
			.withExpectedResponse(RESPONSE_FILE)
			.sendRequestAndVerifyResponse();
		
		assertThat(repository.findById(id).getModified()).isCloseTo(OffsetDateTime.now(), within(2,  ChronoUnit.SECONDS));
	}
	
	@Test
	void test4_removeContactChannel() throws Exception { //NOSONAR
		final var id = "9a24743c-5c19-4774-954e-a3ad67a734e2";

		setupCall()
		.withServicePath(PATH.concat(id))
			.withHttpMethod(HttpMethod.PATCH)
			.withRequest(REQUEST_FILE)
			.withExpectedResponseStatus(Status.OK)
			.withExpectedResponse(RESPONSE_FILE)
			.sendRequestAndVerifyResponse();
		
		assertThat(repository.findById(id).getModified()).isCloseTo(OffsetDateTime.now(), within(2,  ChronoUnit.SECONDS));
	}
}
