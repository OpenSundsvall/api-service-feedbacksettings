package se.sundsvall.feedbacksettings.api;

import static io.restassured.RestAssured.given;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.Response.Status.BAD_REQUEST;
import static javax.ws.rs.core.Response.Status.NOT_FOUND;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.UUID;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response.Status;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.mockito.InjectMock;
import se.sundsvall.feedbacksettings.ContactMethod;
import se.sundsvall.feedbacksettings.api.exception.ServiceException;
import se.sundsvall.feedbacksettings.api.exception.model.ServiceErrorResponse;
import se.sundsvall.feedbacksettings.api.exception.model.TechnicalDetails;
import se.sundsvall.feedbacksettings.api.model.CreateFeedbackSettingsRequest;
import se.sundsvall.feedbacksettings.api.model.RequestedFeedbackChannel;
import se.sundsvall.feedbacksettings.api.model.UpdateFeedbackSettingsRequest;
import se.sundsvall.feedbacksettings.service.FeedbackSettingsService;

@QuarkusTest
class FeedbackSettingsResourceFailuresTest {
	private static final String MOBILE_NBR = "0701234567";
	private static final String PERSON_ID = UUID.randomUUID().toString();
	private static final Boolean SEND_FEEDBACK = Boolean.TRUE;
	private static final String ID = UUID.randomUUID().toString();

	@ConfigProperty(name = "quarkus.application.name")
	String applicationName;
	
	@InjectMock
	FeedbackSettingsService feedbackSettingsServiceMock;

	// POST failure tests
	@Test
	void postMissingBody() {
		final var response = given()
				.contentType(APPLICATION_JSON)
                .when()
                .post("/settings")
                .then().assertThat()
                .statusCode(BAD_REQUEST.getStatusCode())
				.contentType(APPLICATION_JSON)
                .extract().as(ServiceErrorResponse.class);

		assertThat(response).isNotNull();
		assertThat(response.getHttpCode()).isEqualTo(BAD_REQUEST.getStatusCode());
		assertThat(response.getMessage()).isEqualTo("Request validation failed");
		assertThat(response.getTechnicalDetails()).isEqualTo(TechnicalDetails.create()
				.withRootCode(BAD_REQUEST.getStatusCode())
				.withRootCause("Constraint violation")
				.withServiceId(applicationName)
				.withDetails(List.of(
						"body: must not be null",
						"Request: /settings")));

		verifyNoInteractions(feedbackSettingsServiceMock);	
	}

	@Test
	void postEmptyBody() {
		final var body = CreateFeedbackSettingsRequest.create(); //Empty body
		
		final var response = given()
				.contentType(APPLICATION_JSON)
				.body(body)
                .when()
                .post("/settings")
                .then().assertThat()
                .statusCode(BAD_REQUEST.getStatusCode())
				.contentType(APPLICATION_JSON)
                .extract().as(ServiceErrorResponse.class);

		assertThat(response).isNotNull();
		assertThat(response.getHttpCode()).isEqualTo(BAD_REQUEST.getStatusCode());
		assertThat(response.getMessage()).isEqualTo("Request validation failed");
		assertThat(response.getTechnicalDetails()).isEqualTo(TechnicalDetails.create()
				.withRootCode(BAD_REQUEST.getStatusCode())
				.withRootCause("Constraint violation")
				.withServiceId(applicationName)
				.withDetails(List.of(
						"body.personId: must not be null",
						"Request: /settings")));

		verifyNoInteractions(feedbackSettingsServiceMock);	
	}
	
	@Test
	void postNullSettings() {
		final var body = generateCreateRequest(null, null, null, null, null);
		
		final var response = given()
				.contentType(APPLICATION_JSON)
                .body(body)
                .when()
                .post("/settings")
                .then().assertThat()
                .statusCode(BAD_REQUEST.getStatusCode())
				.contentType(APPLICATION_JSON)
                .extract().as(ServiceErrorResponse.class);

		assertThat(response).isNotNull();
		assertThat(response.getHttpCode()).isEqualTo(BAD_REQUEST.getStatusCode());
		assertThat(response.getMessage()).isEqualTo("Request validation failed");
		assertThat(response.getTechnicalDetails()).isEqualTo(TechnicalDetails.create()
				.withRootCode(BAD_REQUEST.getStatusCode())
				.withRootCause("Constraint violation")
				.withServiceId(applicationName)
				.withDetails(List.of(
						"body.channels[0].contactMethod: must not be null",
						"body.channels[0].sendFeedback: must not be null", 
						"body.channels[0]: format for destination is not compliable with provided contact method",
						"body.personId: must not be null",
						"Request: /settings")));
		
		verifyNoInteractions(feedbackSettingsServiceMock);	
	}

	@Test
	void postInvalidSettings() {
		final var body = generateCreateRequest("not-valid", "not-valid", null, "not-valid", null);
		
		final var response = given()
				.contentType(APPLICATION_JSON)
                .body(body)
                .when()
                .post("/settings")
                .then().assertThat()
                .statusCode(BAD_REQUEST.getStatusCode())
				.contentType(APPLICATION_JSON)
                .extract().as(ServiceErrorResponse.class);

		assertThat(response).isNotNull();
		assertThat(response.getHttpCode()).isEqualTo(BAD_REQUEST.getStatusCode());
		assertThat(response.getMessage()).isEqualTo("Request validation failed");
		assertThat(response.getTechnicalDetails()).isEqualTo(TechnicalDetails.create()
				.withRootCode(BAD_REQUEST.getStatusCode())
				.withRootCause("Constraint violation")
				.withServiceId(applicationName)
				.withDetails(List.of(
						"body.channels[0].contactMethod: must not be null",
						"body.channels[0].sendFeedback: must not be null",
						"body.channels[0]: format for destination is not compliable with provided contact method",
						"body.organizationId: must be a valid UUID",
						"body.personId: must be a valid UUID",
						"Request: /settings")));
		
		verifyNoInteractions(feedbackSettingsServiceMock);	
	}
	
	@ParameterizedTest
	@ValueSource(strings = {"", " ", "070-1234567", "070123456", "07012345678", "0711234567", "0741234567", "0751234567", "0771234567", "0781234567", "46701234567"}) 
	void postInvalidMobileNumbers(String invalidMobileNumber) {
		final var body = generateCreateRequest(PERSON_ID, null, ContactMethod.SMS, invalidMobileNumber, SEND_FEEDBACK);
		
		final var response = given()
				.contentType(APPLICATION_JSON)
                .body(body)
                .when()
                .post("/settings")
                .then().assertThat()
                .statusCode(BAD_REQUEST.getStatusCode())
				.contentType(APPLICATION_JSON)
                .extract().as(ServiceErrorResponse.class);

		assertThat(response).isNotNull();
		assertThat(response.getHttpCode()).isEqualTo(BAD_REQUEST.getStatusCode());
		assertThat(response.getMessage()).isEqualTo("Request validation failed");
		assertThat(response.getTechnicalDetails()).isEqualTo(TechnicalDetails.create()
				.withRootCode(BAD_REQUEST.getStatusCode())
				.withRootCause("Constraint violation")
				.withServiceId(applicationName)
				.withDetails(List.of(
						"body.channels[0]: destination must match pattern 07[02369]nnnnnnn when provided contact method is SMS",
						"Request: /settings")));
		
		verifyNoInteractions(feedbackSettingsServiceMock);	
	}

	@ParameterizedTest
	@ValueSource(strings = {"", " ", "invalid", "invalid@value.", ".invalid@value", "invalid @value"}) 
	void postInvalidEmails(String invalidEmail) {
		final var body = generateCreateRequest(PERSON_ID, null, ContactMethod.EMAIL, invalidEmail, SEND_FEEDBACK);
		
		final var response = given()
				.contentType(APPLICATION_JSON)
                .body(body)
                .when()
                .post("/settings")
                .then().assertThat()
                .statusCode(BAD_REQUEST.getStatusCode())
				.contentType(APPLICATION_JSON)
                .extract().as(ServiceErrorResponse.class);

		assertThat(response).isNotNull();
		assertThat(response.getHttpCode()).isEqualTo(BAD_REQUEST.getStatusCode());
		assertThat(response.getMessage()).isEqualTo("Request validation failed");
		assertThat(response.getTechnicalDetails()).isEqualTo(TechnicalDetails.create()
				.withRootCode(BAD_REQUEST.getStatusCode())
				.withRootCause("Constraint violation")
				.withServiceId(applicationName)
				.withDetails(List.of(
						"body.channels[0]: destination must be a well-formed email address when provided contact method is EMAIL",
						"Request: /settings")));
		
		verifyNoInteractions(feedbackSettingsServiceMock);	
	}

	@Test
	void postNonUniqueMembers() throws Exception {
		final var body = CreateFeedbackSettingsRequest.create()
				.withPersonId(PERSON_ID)
				.withChannels(List.of(
					RequestedFeedbackChannel.create()
						.withContactMethod(ContactMethod.SMS)
						.withDestination(MOBILE_NBR)
						.withSendFeedback(true),
					RequestedFeedbackChannel.create()
						.withContactMethod(ContactMethod.SMS)
						.withDestination(MOBILE_NBR)
						.withSendFeedback(false)));
		
		final var response = given()
				.contentType(APPLICATION_JSON)
                .body(body)
                .when()
                .post("/settings")
                .then().assertThat()
                .statusCode(BAD_REQUEST.getStatusCode())
				.contentType(APPLICATION_JSON)
                .extract().as(ServiceErrorResponse.class);

		assertThat(response.getHttpCode()).isEqualTo(BAD_REQUEST.getStatusCode());
		assertThat(response.getMessage()).isEqualTo("Request validation failed");
		assertThat(response.getTechnicalDetails()).isEqualTo(TechnicalDetails.create()
				.withRootCode(BAD_REQUEST.getStatusCode())
				.withRootCause("Constraint violation")
				.withServiceId(applicationName)
				.withDetails(List.of(
						"body.channels: the collection contains two or more elements with equal contactMethod and destination, these values must be unique",
						"Request: /settings")));
		
		verifyNoInteractions(feedbackSettingsServiceMock);	
	}

	@Test
	void postForExistingEntity() throws Exception {
		final var body = generateCreateRequest(PERSON_ID, null, ContactMethod.SMS, MOBILE_NBR, SEND_FEEDBACK);
		
		when(feedbackSettingsServiceMock.createFeedbackSettings(body)).thenThrow(ServiceException.create("A resource already exists with the same personId: " + PERSON_ID, Status.BAD_REQUEST));

		final var response = given()
				.contentType(APPLICATION_JSON)
                .body(body)
                .when()
                .post("/settings")
                .then().assertThat()
                .statusCode(BAD_REQUEST.getStatusCode())
				.contentType(APPLICATION_JSON)
                .extract().as(ServiceErrorResponse.class);

		assertThat(response).isNotNull();
		assertThat(response.getHttpCode()).isEqualTo(BAD_REQUEST.getStatusCode());
		assertThat(response.getMessage()).isEqualTo("A resource already exists with the same personId: " + PERSON_ID);
		assertThat(response.getTechnicalDetails()).isEqualTo(TechnicalDetails.create()
				.withRootCode(BAD_REQUEST.getStatusCode())
				.withServiceId(applicationName)
				.withDetails(List.of(
						"Request: /settings")));
		
		verify(feedbackSettingsServiceMock).createFeedbackSettings(body);
	}

	// PATCH failure tests
	@Test
	void patchMissingBody() {
		final var response = given()
				.contentType(APPLICATION_JSON)
                .pathParam("id", ID)
                .when()
                .patch("/settings/{id}")
                .then().assertThat()
                .statusCode(BAD_REQUEST.getStatusCode())
				.contentType(APPLICATION_JSON)
                .extract().as(ServiceErrorResponse.class);

		assertThat(response).isNotNull();
		assertThat(response.getHttpCode()).isEqualTo(BAD_REQUEST.getStatusCode());
		assertThat(response.getMessage()).isEqualTo("Request validation failed");
		assertThat(response.getTechnicalDetails()).isEqualTo(TechnicalDetails.create()
				.withRootCode(BAD_REQUEST.getStatusCode())
				.withRootCause("Constraint violation")
				.withServiceId(applicationName)
				.withDetails(List.of(
						"body: must not be null",
						"Request: /settings/" + ID)));
		
		verifyNoInteractions(feedbackSettingsServiceMock);	
	}

	@Test
	void patchNullSettings() {
		final var body = generateUpdateRequest(null, null, null);
		
		final var response = given()
				.contentType(APPLICATION_JSON)
                .pathParam("id", ID)
                .body(body)
                .when()
                .patch("/settings/{id}")
                .then().assertThat()
                .statusCode(BAD_REQUEST.getStatusCode())
				.contentType(APPLICATION_JSON)
                .extract().as(ServiceErrorResponse.class);

		assertThat(response).isNotNull();
		assertThat(response.getHttpCode()).isEqualTo(BAD_REQUEST.getStatusCode());
		assertThat(response.getMessage()).isEqualTo("Request validation failed");
		assertThat(response.getTechnicalDetails()).isEqualTo(TechnicalDetails.create()
				.withRootCode(BAD_REQUEST.getStatusCode())
				.withRootCause("Constraint violation")
				.withServiceId(applicationName)
				.withDetails(List.of(
						"body.channels[0].contactMethod: must not be null", 
						"body.channels[0].sendFeedback: must not be null",
						"body.channels[0]: format for destination is not compliable with provided contact method",
						"Request: /settings/" + ID)));
		
		verifyNoInteractions(feedbackSettingsServiceMock);	
	}

	@Test
	void patchInvalidSettings() {
		final var body = generateUpdateRequest(null, "not-valid", null);
		
		final var response = given()
				.contentType(APPLICATION_JSON)
                .pathParam("id", ID)
                .body(body)
                .when()
                .patch("/settings/{id}")
                .then().assertThat()
                .statusCode(BAD_REQUEST.getStatusCode())
				.contentType(APPLICATION_JSON)
                .extract().as(ServiceErrorResponse.class);

		assertThat(response).isNotNull();
		assertThat(response.getHttpCode()).isEqualTo(BAD_REQUEST.getStatusCode());
		assertThat(response.getMessage()).isEqualTo("Request validation failed");
		assertThat(response.getTechnicalDetails()).isEqualTo(TechnicalDetails.create()
				.withRootCode(BAD_REQUEST.getStatusCode())
				.withRootCause("Constraint violation")
				.withServiceId(applicationName)
				.withDetails(List.of(
						"body.channels[0].contactMethod: must not be null", 
						"body.channels[0].sendFeedback: must not be null",
						"body.channels[0]: format for destination is not compliable with provided contact method",
						"Request: /settings/" + ID)));
		
		verifyNoInteractions(feedbackSettingsServiceMock);	
	}
	
	@ParameterizedTest
	@ValueSource(strings = {"", " ", "070-1234567", "070123456", "07012345678", "0711234567", "0741234567", "0751234567", "0771234567", "0781234567", "46701234567"}) 
	void patchInvalidMobileNumbers(String invalidMobileNumber) {
		final var body = generateUpdateRequest(ContactMethod.SMS, invalidMobileNumber, SEND_FEEDBACK);
		
		final var response = given()
				.contentType(APPLICATION_JSON)
                .pathParam("id", ID)
                .body(body)
                .when()
                .patch("/settings/{id}")
                .then().assertThat()
                .statusCode(BAD_REQUEST.getStatusCode())
				.contentType(APPLICATION_JSON)
                .extract().as(ServiceErrorResponse.class);

		assertThat(response).isNotNull();
		assertThat(response.getHttpCode()).isEqualTo(BAD_REQUEST.getStatusCode());
		assertThat(response.getMessage()).isEqualTo("Request validation failed");
		assertThat(response.getTechnicalDetails()).isEqualTo(TechnicalDetails.create()
				.withRootCode(BAD_REQUEST.getStatusCode())
				.withRootCause("Constraint violation")
				.withServiceId(applicationName)
				.withDetails(List.of(
						"body.channels[0]: destination must match pattern 07[02369]nnnnnnn when provided contact method is SMS", 
						"Request: /settings/" + ID)));
		
		verifyNoInteractions(feedbackSettingsServiceMock);	
	}
	
	@ParameterizedTest
	@ValueSource(strings = {"", " ", "invalid", "invalid@value.", ".invalid@value", "invalid @value"}) 
	void patchInvalidEmails(String invalidEmail) {
		final var body = generateUpdateRequest(ContactMethod.EMAIL, invalidEmail, SEND_FEEDBACK);
		
		final var response = given()
				.contentType(APPLICATION_JSON)
                .pathParam("id", ID)
                .body(body)
                .when()
                .patch("/settings/{id}")
                .then().assertThat()
                .statusCode(BAD_REQUEST.getStatusCode())
				.contentType(APPLICATION_JSON)
                .extract().as(ServiceErrorResponse.class);

		assertThat(response).isNotNull();
		assertThat(response.getHttpCode()).isEqualTo(BAD_REQUEST.getStatusCode());
		assertThat(response.getMessage()).isEqualTo("Request validation failed");
		assertThat(response.getTechnicalDetails()).isEqualTo(TechnicalDetails.create()
				.withRootCode(BAD_REQUEST.getStatusCode())
				.withRootCause("Constraint violation")
				.withServiceId(applicationName)
				.withDetails(List.of(
						"body.channels[0]: destination must be a well-formed email address when provided contact method is EMAIL",
						"Request: /settings/" + ID)));
		
		verifyNoInteractions(feedbackSettingsServiceMock);	
	}
	
	@Test
	void patchNonUniqueMembers() throws Exception {
		final var body = UpdateFeedbackSettingsRequest.create()
				.withChannels(List.of(
					RequestedFeedbackChannel.create()
						.withContactMethod(ContactMethod.SMS)
						.withDestination(MOBILE_NBR)
						.withSendFeedback(true),
					RequestedFeedbackChannel.create()
						.withContactMethod(ContactMethod.SMS)
						.withDestination(MOBILE_NBR)
						.withSendFeedback(false)));
		
		final var response = given()
				.contentType(APPLICATION_JSON)
                .pathParam("id", ID)
                .body(body)
                .when()
                .patch("/settings/{id}")
                .then().assertThat()
                .statusCode(BAD_REQUEST.getStatusCode())
				.contentType(APPLICATION_JSON)
                .extract().as(ServiceErrorResponse.class);

		assertThat(response.getHttpCode()).isEqualTo(BAD_REQUEST.getStatusCode());
		assertThat(response.getMessage()).isEqualTo("Request validation failed");
		assertThat(response.getTechnicalDetails()).isEqualTo(TechnicalDetails.create()
				.withRootCode(BAD_REQUEST.getStatusCode())
				.withRootCause("Constraint violation")
				.withServiceId(applicationName)
				.withDetails(List.of(
						"body.channels: the collection contains two or more elements with equal contactMethod and destination, these values must be unique",
						"Request: /settings/" + ID)));
		
		verifyNoInteractions(feedbackSettingsServiceMock);	
	}
	
	@Test
	void patchWithNonValidUUID() throws Exception {
		final var body = generateUpdateRequest(ContactMethod.SMS, MOBILE_NBR, SEND_FEEDBACK);
		
		final var response = given()
				.contentType(APPLICATION_JSON)
                .pathParam("id", "not-valid")
                .body(body)
                .when()
                .patch("/settings/{id}")
                .then().assertThat()
                .statusCode(BAD_REQUEST.getStatusCode())
				.contentType(APPLICATION_JSON)
                .extract().as(ServiceErrorResponse.class);

		assertThat(response).isNotNull();
		assertThat(response.getHttpCode()).isEqualTo(BAD_REQUEST.getStatusCode());
		assertThat(response.getMessage()).isEqualTo("Request validation failed");
		assertThat(response.getTechnicalDetails()).isEqualTo(TechnicalDetails.create()
				.withRootCode(BAD_REQUEST.getStatusCode())
				.withRootCause("Constraint violation")
				.withServiceId(applicationName)
				.withDetails(List.of(
						"id: must be a valid UUID",
						"Request: /settings/not-valid")));

		verifyNoInteractions(feedbackSettingsServiceMock);	
	}

	@Test
	void patchForNonExistingEntity() throws Exception {
		final var body = generateUpdateRequest(ContactMethod.SMS, MOBILE_NBR, SEND_FEEDBACK);
		
		when(feedbackSettingsServiceMock.updateFeedbackSettings(ID, body)).thenThrow(ServiceException.create(Status.NOT_FOUND.getReasonPhrase(), Status.NOT_FOUND));

		final var response = given()
				.contentType(APPLICATION_JSON)
                .pathParam("id", ID)
                .body(body)
                .when()
                .patch("/settings/{id}")
                .then().assertThat()
                .statusCode(NOT_FOUND.getStatusCode())
				.contentType(APPLICATION_JSON)
                .extract().as(ServiceErrorResponse.class);

		assertThat(response).isNotNull();
		assertThat(response.getHttpCode()).isEqualTo(NOT_FOUND.getStatusCode());
		assertThat(response.getMessage()).isEqualTo(Status.NOT_FOUND.getReasonPhrase());
		assertThat(response.getTechnicalDetails()).isEqualTo(TechnicalDetails.create()
				.withRootCode(NOT_FOUND.getStatusCode())
				.withServiceId(applicationName)
				.withDetails(List.of(
						"Request: /settings/" + ID)));
		
		verify(feedbackSettingsServiceMock).updateFeedbackSettings(ID, body);
	}
	
	// DELETE failure tests
	@Test
	void deleteWithNonValidUUID() throws Exception {
		final var response = given()
				.contentType(APPLICATION_JSON)
                .pathParam("id", "not-valid")
                .when()
                .delete("/settings/{id}")
                .then().assertThat()
                .statusCode(BAD_REQUEST.getStatusCode())
				.contentType(APPLICATION_JSON)
                .extract().as(ServiceErrorResponse.class);

		assertThat(response).isNotNull();
		assertThat(response.getHttpCode()).isEqualTo(BAD_REQUEST.getStatusCode());
		assertThat(response.getMessage()).isEqualTo("Request validation failed");
		assertThat(response.getTechnicalDetails()).isEqualTo(TechnicalDetails.create()
				.withRootCode(BAD_REQUEST.getStatusCode())
				.withRootCause("Constraint violation")
				.withServiceId(applicationName)
				.withDetails(List.of(
						"id: must be a valid UUID",
						"Request: /settings/not-valid")));

		verifyNoInteractions(feedbackSettingsServiceMock);	
	}

	@Test
	void deleteForNonExistingEntity() throws Exception {
		doThrow(ServiceException.create(Status.NOT_FOUND.getReasonPhrase(), Status.NOT_FOUND)).when(feedbackSettingsServiceMock).deleteFeedbackSettings(ID);

		final var response = given()
				.contentType(APPLICATION_JSON)
                .pathParam("id", ID)
                .when()
                .delete("/settings/{id}")
                .then().assertThat()
                .statusCode(NOT_FOUND.getStatusCode())
				.contentType(APPLICATION_JSON)
                .extract().as(ServiceErrorResponse.class);

		assertThat(response).isNotNull();
		assertThat(response.getHttpCode()).isEqualTo(NOT_FOUND.getStatusCode());
		assertThat(response.getMessage()).isEqualTo(Status.NOT_FOUND.getReasonPhrase());
		assertThat(response.getTechnicalDetails()).isEqualTo(TechnicalDetails.create()
				.withRootCode(NOT_FOUND.getStatusCode())
				.withServiceId(applicationName)
				.withDetails(List.of(
						"Request: /settings/" + ID)));
		
		verify(feedbackSettingsServiceMock).deleteFeedbackSettings(ID);
	}

	// GET failure tests
	@Test
	void getByIdForNonValidUUID() throws Exception {
		final var response = given()
				.contentType(APPLICATION_JSON)
                .pathParam("id", "not-valid")
                .when()
                .get("/settings/{id}")
                .then().assertThat()
                .statusCode(BAD_REQUEST.getStatusCode())
				.contentType(APPLICATION_JSON)
                .extract().as(ServiceErrorResponse.class);

		assertThat(response).isNotNull();
		assertThat(response.getHttpCode()).isEqualTo(BAD_REQUEST.getStatusCode());
		assertThat(response.getMessage()).isEqualTo("Request validation failed");
		assertThat(response.getTechnicalDetails()).isEqualTo(TechnicalDetails.create()
				.withRootCode(BAD_REQUEST.getStatusCode())
				.withRootCause("Constraint violation")
				.withServiceId(applicationName)
				.withDetails(List.of(
						"id: must be a valid UUID",
						"Request: /settings/not-valid")));

		verifyNoInteractions(feedbackSettingsServiceMock);	
	}

	@Test
	void getByIdForNonExistingEntity() throws Exception {
		when(feedbackSettingsServiceMock.getFeedbackSettingsById(ID)).thenThrow(ServiceException.create(Status.NOT_FOUND.getReasonPhrase(), Status.NOT_FOUND));

		final var response = given()
				.contentType(APPLICATION_JSON)
                .pathParam("id", ID)
                .when()
                .get("/settings/{id}")
                .then().assertThat()
                .statusCode(NOT_FOUND.getStatusCode())
				.contentType(APPLICATION_JSON)
                .extract().as(ServiceErrorResponse.class);

		assertThat(response).isNotNull();
		assertThat(response.getHttpCode()).isEqualTo(NOT_FOUND.getStatusCode());
		assertThat(response.getMessage()).isEqualTo(Status.NOT_FOUND.getReasonPhrase());
		assertThat(response.getTechnicalDetails()).isEqualTo(TechnicalDetails.create()
				.withRootCode(NOT_FOUND.getStatusCode())
				.withServiceId(applicationName)
				.withDetails(List.of(
						"Request: /settings/" + ID)));
		
		verify(feedbackSettingsServiceMock).getFeedbackSettingsById(ID);
	}
	
	@Test
	void getByQueryWithNonValidUUID() {
		final var response = given()
				.contentType(MediaType.APPLICATION_JSON)
				.queryParam("personId", "non-valid")
				.queryParam("organizationId", "non-valid")
                .when()
                .get("/settings")
                .then().assertThat()
                .statusCode(BAD_REQUEST.getStatusCode())
				.contentType(APPLICATION_JSON)
                .extract().as(ServiceErrorResponse.class);

		assertThat(response.getTechnicalDetails()).isEqualTo(TechnicalDetails.create()
				.withRootCode(BAD_REQUEST.getStatusCode())
				.withRootCause("Constraint violation")
				.withServiceId(applicationName)
				.withDetails(List.of(
						"organizationId: must be a valid UUID", 
						"personId: must be a valid UUID", 
						"Request: /settings")));
		
		verifyNoInteractions(feedbackSettingsServiceMock);	
	}
	
	@Test
	void getByQueryWithPageLessThanOne() {
		final var response = given()
				.contentType(MediaType.APPLICATION_JSON)
				.queryParam("personId", PERSON_ID)
				.queryParam("page", 0)
                .when()
                .get("/settings")
                .then().assertThat()
                .statusCode(BAD_REQUEST.getStatusCode())
				.contentType(APPLICATION_JSON)
                .extract().as(ServiceErrorResponse.class);

		assertThat(response.getTechnicalDetails()).isEqualTo(TechnicalDetails.create()
				.withRootCode(BAD_REQUEST.getStatusCode())
				.withRootCause("Constraint violation")
				.withServiceId(applicationName)
				.withDetails(List.of(
						"page: must be greater than or equal to 1", 
						"Request: /settings")));
		
		verifyNoInteractions(feedbackSettingsServiceMock);	
	}
	
	@Test
	void getByQueryWithPageSizeLessThanOne() {
		final var response = given()
				.contentType(MediaType.APPLICATION_JSON)
				.queryParam("personId", PERSON_ID)
				.queryParam("limit", 0)
                .when()
                .get("/settings")
                .then().assertThat()
                .statusCode(BAD_REQUEST.getStatusCode())
				.contentType(APPLICATION_JSON)
                .extract().as(ServiceErrorResponse.class);

		assertThat(response.getTechnicalDetails()).isEqualTo(TechnicalDetails.create()
				.withRootCode(BAD_REQUEST.getStatusCode())
				.withRootCause("Constraint violation")
				.withServiceId(applicationName)
				.withDetails(List.of(
						"limit: must be greater than or equal to 1", 
						"Request: /settings")));
		
		verifyNoInteractions(feedbackSettingsServiceMock);	
	}

	private CreateFeedbackSettingsRequest generateCreateRequest(String personId, String organizationId, ContactMethod contactMethod, String destination, Boolean sendFeedback) {
		return CreateFeedbackSettingsRequest.create()
				.withPersonId(personId)
				.withOrganizationId(organizationId)
				.withChannels(List.of(RequestedFeedbackChannel.create()
						.withContactMethod(contactMethod)
						.withDestination(destination)
						.withSendFeedback(sendFeedback)));
	}

	private UpdateFeedbackSettingsRequest generateUpdateRequest(ContactMethod contactMethod, String destination, Boolean sendFeedback) {
		return UpdateFeedbackSettingsRequest.create()
				.withChannels(List.of(RequestedFeedbackChannel.create()
						.withContactMethod(contactMethod)
						.withDestination(destination)
						.withSendFeedback(sendFeedback)));
	}
}
