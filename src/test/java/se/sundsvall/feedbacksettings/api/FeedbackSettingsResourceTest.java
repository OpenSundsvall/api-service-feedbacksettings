package se.sundsvall.feedbacksettings.api;

import static io.restassured.RestAssured.given;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.Response.Status.CREATED;
import static javax.ws.rs.core.Response.Status.NO_CONTENT;
import static javax.ws.rs.core.Response.Status.OK;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.UUID;

import javax.ws.rs.core.MediaType;

import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.mockito.InjectMock;
import se.sundsvall.feedbacksettings.ContactMethod;
import se.sundsvall.feedbacksettings.api.model.CreateFeedbackSettingsRequest;
import se.sundsvall.feedbacksettings.api.model.RequestedFeedbackChannel;
import se.sundsvall.feedbacksettings.api.model.FeedbackSettings;
import se.sundsvall.feedbacksettings.api.model.SearchResult;
import se.sundsvall.feedbacksettings.api.model.UpdateFeedbackSettingsRequest;
import se.sundsvall.feedbacksettings.service.FeedbackSettingsService;

@QuarkusTest
class FeedbackSettingsResourceTest {

	private static final String MOBILE_NBR = "0701234567";
	private static final String EMAIL_ADDRESS = "valid.email@host.org";
	private static final String PERSON_ID = UUID.randomUUID().toString();
	private static final String ORGANIZATION_ID = UUID.randomUUID().toString();
	private static final Boolean SEND_FEEDBACK = Boolean.TRUE;
	private static final String ID = UUID.randomUUID().toString();
	
	@InjectMock
	FeedbackSettingsService feedbackSettingsServiceMock;
	
	@Test
	void testPostForPerson() throws Exception {
		CreateFeedbackSettingsRequest request = CreateFeedbackSettingsRequest.create()
				.withPersonId(PERSON_ID)
				.withOrganizationId(ORGANIZATION_ID)
				.withChannels(List.of(RequestedFeedbackChannel.create()
						.withContactMethod(ContactMethod.EMAIL)
						.withDestination(EMAIL_ADDRESS)
						.withSendFeedback(SEND_FEEDBACK)));
		
		when(feedbackSettingsServiceMock.createFeedbackSettings(request)).thenReturn(FeedbackSettings.create().withId(String.valueOf(ID)));
		
		final var response = given()
			.contentType(APPLICATION_JSON)
            .body(request)
            .when()
            .post("/settings")
            .then().assertThat()
            .statusCode(CREATED.getStatusCode())
            .header("location", "http://localhost:8081/settings/" + ID)
            .extract().as(FeedbackSettings.class);

		assertThat(response).isNotNull();
		verify(feedbackSettingsServiceMock).createFeedbackSettings(request);
	}

	@Test
	void testPostForOrganizationalRepresentative() throws Exception {
		CreateFeedbackSettingsRequest request = CreateFeedbackSettingsRequest.create()
				.withPersonId(PERSON_ID)
				.withOrganizationId(ORGANIZATION_ID)
				.withChannels(List.of(RequestedFeedbackChannel.create()
						.withContactMethod(ContactMethod.SMS)
						.withDestination(MOBILE_NBR)
						.withSendFeedback(SEND_FEEDBACK)));
		
		when(feedbackSettingsServiceMock.createFeedbackSettings(request)).thenReturn(FeedbackSettings.create().withId(String.valueOf(ID)));
		
		final var response = given()
			.contentType(APPLICATION_JSON)
            .body(request)
            .when()
            .post("/settings")
            .then().assertThat()
            .statusCode(CREATED.getStatusCode())
            .header("location", "http://localhost:8081/settings/" + ID)
            .extract().as(FeedbackSettings.class);

		assertThat(response).isNotNull();
		verify(feedbackSettingsServiceMock).createFeedbackSettings(request);
	}
	
	@Test
	void testPatch() throws Exception {
		UpdateFeedbackSettingsRequest request = UpdateFeedbackSettingsRequest.create()
				.withChannels(List.of(RequestedFeedbackChannel.create()
						.withContactMethod(ContactMethod.SMS)
						.withDestination(MOBILE_NBR)
						.withSendFeedback(SEND_FEEDBACK)));

		when(feedbackSettingsServiceMock.updateFeedbackSettings(ID, request)).thenReturn(FeedbackSettings.create().withId(String.valueOf(ID)));

		final var response = given()
				.contentType(APPLICATION_JSON)
				.pathParam("id", ID)
				.body(request)
                .when()
                .patch("/settings/{id}")
                .then().assertThat()
                .statusCode(OK.getStatusCode())
				.contentType(APPLICATION_JSON)
                .extract().as(FeedbackSettings.class);

		assertThat(response).isNotNull();
		verify(feedbackSettingsServiceMock).updateFeedbackSettings(ID, request);
	}

	@Test
	void testPatchWithEmptyBody() throws Exception { // To verify that parameters can be null 
		UpdateFeedbackSettingsRequest request = UpdateFeedbackSettingsRequest.create();

		when(feedbackSettingsServiceMock.updateFeedbackSettings(ID, request)).thenReturn(FeedbackSettings.create().withId(String.valueOf(ID)));

		final var response = given()
				.contentType(APPLICATION_JSON)
				.pathParam("id", ID)
				.body(request)
                .when()
                .patch("/settings/{id}")
                .then().assertThat()
                .statusCode(OK.getStatusCode())
				.contentType(APPLICATION_JSON)
                .extract().as(FeedbackSettings.class);

		assertThat(response).isNotNull();
		verify(feedbackSettingsServiceMock).updateFeedbackSettings(ID, request);
	}
	
	@Test
	void testDelete() throws Exception {
		given()
			.contentType(APPLICATION_JSON)
			.pathParam("id", ID)
            .when()
            .delete("/settings/{id}")
            .then().assertThat()
            .statusCode(NO_CONTENT.getStatusCode());

		verify(feedbackSettingsServiceMock).deleteFeedbackSettings(ID);
	}
	
	@Test
	void testGetById() throws Exception {
		when(feedbackSettingsServiceMock.getFeedbackSettingsById(ID)).thenReturn(FeedbackSettings.create().withId(String.valueOf(ID)));

		final var response = given()
				.contentType(APPLICATION_JSON)
				.pathParam("id", ID)
                .when()
                .get("/settings/{id}")
                .then().assertThat()
                .statusCode(OK.getStatusCode())
				.contentType(APPLICATION_JSON)
                .extract().as(FeedbackSettings.class);

		assertThat(response).isNotNull();
		verify(feedbackSettingsServiceMock).getFeedbackSettingsById(ID);
	}

	@Test
	void testGetByQueryWithDefaultPageSettings() {
		when(feedbackSettingsServiceMock.getFeedbackSettings(PERSON_ID, null, 1, 20)).thenReturn(SearchResult.create());

		final var response = given()
				.contentType(MediaType.APPLICATION_JSON)
				.queryParam("personId", PERSON_ID)
                .when()
                .get("/settings")
                .then().assertThat()
                .statusCode(OK.getStatusCode())
				.contentType(APPLICATION_JSON)
                .extract().as(SearchResult.class);

		assertThat(response).isNotNull();
		verify(feedbackSettingsServiceMock).getFeedbackSettings(PERSON_ID, null, 1, 20);
	}
	
	@Test
	void testGetByQueryWithSpecificPageSettings() {
		when(feedbackSettingsServiceMock.getFeedbackSettings(PERSON_ID, ORGANIZATION_ID, 1, 10)).thenReturn(SearchResult.create());

		final var response = given()
				.contentType(MediaType.APPLICATION_JSON)
				.queryParam("personId", PERSON_ID)
				.queryParam("organizationId", ORGANIZATION_ID)
				.queryParam("page", 1)
				.queryParam("limit", 10)
                .when()
                .get("/settings")
                .then().assertThat()
                .statusCode(OK.getStatusCode())
				.contentType(APPLICATION_JSON)
                .extract().as(SearchResult.class);

		assertThat(response).isNotNull();
		verify(feedbackSettingsServiceMock).getFeedbackSettings(PERSON_ID, ORGANIZATION_ID, 1, 10);
	}
}
