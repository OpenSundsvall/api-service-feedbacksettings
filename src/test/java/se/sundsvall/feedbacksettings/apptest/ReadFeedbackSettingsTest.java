package se.sundsvall.feedbacksettings.apptest;

import java.util.List;

import javax.ws.rs.HttpMethod;
import javax.ws.rs.core.Response.Status;

import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;
import net.javacrumbs.jsonunit.core.Option;

/**
 * Read feedback settings application tests
 * 
 * @see src/test/resources/db/testdata.sql for data setup.
 */
@QuarkusTest
class ReadFeedbackSettingsTest extends AbstractAppTest {
	private static final String PATH = "/settings";
	private static final String RESPONSE_FILE = "response.json";
	
	@Test
	void test1_readById() throws Exception { //NOSONAR
		final var id = "9a24743c-5c19-4774-954e-a3ad67a734e2";

		setupCall()
			.withServicePath(PATH.concat("/").concat(id))
			.withHttpMethod(HttpMethod.GET)
			.withOptions(List.of(Option.IGNORING_ARRAY_ORDER))
			.withExpectedResponseStatus(Status.OK)
			.withExpectedResponse(RESPONSE_FILE)
			.sendRequestAndVerifyResponse();
	}

	@Test
	void test2_readByNonExistingId() throws Exception { //NOSONAR
		final var id = "9a24743c-5c19-4774-954e-a3ad67a734e0";

		setupCall()
			.withServicePath(PATH.concat("/").concat(id))
			.withHttpMethod(HttpMethod.GET)
			.withOptions(List.of(Option.IGNORING_ARRAY_ORDER))
			.withExpectedResponseStatus(Status.NOT_FOUND)
			.withExpectedResponse(RESPONSE_FILE)
			.sendRequestAndVerifyResponse();
	}

	@Test
	void test3_queryByPersonId() throws Exception { //NOSONAR
		final var personId = "49a974ea-9137-419b-bcb9-ad74c81a1d3f";

		setupCall()
			.withServicePath(PATH
					.concat("?personId=").concat(personId))
			.withHttpMethod(HttpMethod.GET)
			.withOptions(List.of(Option.IGNORING_ARRAY_ORDER))
			.withExpectedResponseStatus(Status.OK)
			.withExpectedResponse(RESPONSE_FILE)
			.sendRequestAndVerifyResponse();
	}
	
	@Test
	void test4_queryByPersonAndOrganizationId() throws Exception { //NOSONAR
		final var personId = "49a974ea-9137-419b-bcb9-ad74c81a1d3f";
		final var organizationId = "15aee472-46ab-4f03-9605-68bd64ebc84a";

		setupCall()
			.withServicePath(PATH
					.concat("?personId=").concat(personId)
					.concat("&organizationId=").concat(organizationId))
			.withHttpMethod(HttpMethod.GET)
			.withOptions(List.of(Option.IGNORING_ARRAY_ORDER))
			.withExpectedResponseStatus(Status.OK)
			.withExpectedResponse(RESPONSE_FILE)
			.sendRequestAndVerifyResponse();
	}

	@Test
	void test5_queryByOrganizationId() throws Exception { //NOSONAR
		final var organizationId = "15aee472-46ab-4f03-9605-68bd64ebc84a";

		setupCall()
			.withServicePath(PATH
					.concat("?organizationId=").concat(organizationId))
			.withHttpMethod(HttpMethod.GET)
			.withOptions(List.of(Option.IGNORING_ARRAY_ORDER))
			.withExpectedResponseStatus(Status.OK)
			.withExpectedResponse(RESPONSE_FILE)
			.sendRequestAndVerifyResponse();
	}

	@Test
	void test6_queryWithNoMatches() throws Exception { //NOSONAR
		final var organizationId = "15aee472-46ab-4f03-9605-68bd64ebc84f";

		setupCall()
			.withServicePath(PATH
					.concat("?organizationId=").concat(organizationId))
			.withHttpMethod(HttpMethod.GET)
			.withOptions(List.of(Option.IGNORING_ARRAY_ORDER))
			.withExpectedResponseStatus(Status.OK)
			.withExpectedResponse(RESPONSE_FILE)
			.sendRequestAndVerifyResponse();
	}

	@Test
	void test7_queryWithNonDefaultPagingParams() throws Exception { //NOSONAR
		setupCall()
			.withServicePath(PATH
					.concat("?page=1")
					.concat("&limit=3"))
			.withHttpMethod(HttpMethod.GET)
			.withOptions(List.of(Option.IGNORING_ARRAY_ORDER))
			.withExpectedResponseStatus(Status.OK)
			.withExpectedResponse(RESPONSE_FILE)
			.sendRequestAndVerifyResponse();
	}
}
