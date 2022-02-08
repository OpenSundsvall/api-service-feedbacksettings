package se.sundsvall.feedbacksettings.api;

import static javax.ws.rs.core.HttpHeaders.LOCATION;
import static javax.ws.rs.core.Response.created;
import static javax.ws.rs.core.Response.noContent;
import static javax.ws.rs.core.Response.ok;
import static org.eclipse.microprofile.openapi.annotations.enums.SchemaType.STRING;

import java.util.Map;

import javax.inject.Inject;
import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.PATCH;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.eclipse.microprofile.openapi.annotations.headers.Header;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;

import se.sundsvall.feedbacksettings.api.exception.ServiceException;
import se.sundsvall.feedbacksettings.api.exception.model.ServiceErrorResponse;
import se.sundsvall.feedbacksettings.api.model.CreateFeedbackSettingsRequest;
import se.sundsvall.feedbacksettings.api.model.FeedbackSettings;
import se.sundsvall.feedbacksettings.api.model.SearchResult;
import se.sundsvall.feedbacksettings.api.model.UpdateFeedbackSettingsRequest;
import se.sundsvall.feedbacksettings.api.validation.ValidUuid;
import se.sundsvall.feedbacksettings.service.FeedbackSettingsService;

@Path("settings/")
public class FeedbackSettingsResource {
	@Inject
	FeedbackSettingsService feedbackSettingsService;

	@Context
	UriInfo uriInfo;
	
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@APIResponse(responseCode = "201", headers = @Header(name = LOCATION, schema = @Schema(type = STRING)), description = "Created", content = @Content(schema = @Schema(implementation = FeedbackSettings.class)))
	@APIResponse(responseCode = "400", description = "Bad request", content = @Content(schema = @Schema(implementation = ServiceErrorResponse.class)))
	@APIResponse(responseCode = "500", description = "Internal Server error", content = @Content(schema = @Schema(implementation = ServiceErrorResponse.class)))
	public Response postFeedbackSettings(@NotNull @Valid CreateFeedbackSettingsRequest body) throws ServiceException {

		FeedbackSettings settings = feedbackSettingsService.createFeedbackSettings(body);
		
		final var locationUri = uriInfo.getAbsolutePathBuilder()
				.path("/{id}")
				.buildFromMap(Map.of("id", settings.getId()));
		return created(locationUri).entity(settings).build();
	}

	@PATCH
	@Path("/{id}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@APIResponse(responseCode = "200", description = "Successful operation", content = @Content(schema = @Schema(implementation = FeedbackSettings.class)))
	@APIResponse(responseCode = "400", description = "Bad request", content = @Content(schema = @Schema(implementation = ServiceErrorResponse.class)))
	@APIResponse(responseCode = "404", description = "Not found", content = @Content(schema = @Schema(implementation = ServiceErrorResponse.class)))
	@APIResponse(responseCode = "500", description = "Internal Server error", content = @Content(schema = @Schema(implementation = ServiceErrorResponse.class)))
	public Response patchFeedbackSettings(@PathParam("id") @ValidUuid String id,
			@NotNull @Valid UpdateFeedbackSettingsRequest body) throws ServiceException {

		return ok(feedbackSettingsService.updateFeedbackSettings(id, body)).build();
	}

	@DELETE
	@Path("/{id}")
	@APIResponse(responseCode = "204", description = "Successful operation")
	@APIResponse(responseCode = "400", description = "Bad request", content = @Content(schema = @Schema(implementation = ServiceErrorResponse.class)))
	@APIResponse(responseCode = "404", description = "Not found", content = @Content(schema = @Schema(implementation = ServiceErrorResponse.class)))
	@APIResponse(responseCode = "500", description = "Internal Server error", content = @Content(schema = @Schema(implementation = ServiceErrorResponse.class)))
	public Response deleteFeedbackSettings(@PathParam("id") @ValidUuid String id) throws ServiceException {
		feedbackSettingsService.deleteFeedbackSettings(id);
		return noContent().build();
	}

	@GET
	@Path("/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	@APIResponse(responseCode = "200", description = "Successful operation", content = @Content(schema = @Schema(implementation = FeedbackSettings.class)))
	@APIResponse(responseCode = "400", description = "Bad request", content = @Content(schema = @Schema(implementation = ServiceErrorResponse.class)))
	@APIResponse(responseCode = "404", description = "Not found", content = @Content(schema = @Schema(implementation = ServiceErrorResponse.class)))
	@APIResponse(responseCode = "500", description = "Internal Server error", content = @Content(schema = @Schema(implementation = ServiceErrorResponse.class)))
	public Response getFeedbackSettingsById(@PathParam("id") @ValidUuid String id) throws ServiceException {

		return ok(feedbackSettingsService.getFeedbackSettingsById(id)).build();
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@APIResponse(responseCode = "200", description = "Successful operation", content = @Content(schema = @Schema(implementation = SearchResult.class)))
	@APIResponse(responseCode = "400", description = "Bad request", content = @Content(schema = @Schema(implementation = ServiceErrorResponse.class)))
	@APIResponse(responseCode = "404", description = "Not found", content = @Content(schema = @Schema(implementation = ServiceErrorResponse.class)))
	@APIResponse(responseCode = "500", description = "Internal Server error", content = @Content(schema = @Schema(implementation = ServiceErrorResponse.class)))
	public Response getFeedbackSettingsByQuery(
			@QueryParam("personId") @ValidUuid String personId,
			@QueryParam("organizationId") @ValidUuid String organizationId,
			@QueryParam("page") @Min(1) @DefaultValue("1") int page,
			@QueryParam("limit") @Min(1) @DefaultValue("20") int limit
			) {

		return ok(feedbackSettingsService.getFeedbackSettings(personId, organizationId, page, limit)).build();
	}
}