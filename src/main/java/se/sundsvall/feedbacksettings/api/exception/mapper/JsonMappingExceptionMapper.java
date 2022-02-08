package se.sundsvall.feedbacksettings.api.exception.mapper;

import static javax.ws.rs.core.Response.Status.BAD_REQUEST;
import static org.slf4j.LoggerFactory.getLogger;

import java.util.List;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;

import org.slf4j.Logger;

import com.fasterxml.jackson.databind.JsonMappingException;

import se.sundsvall.feedbacksettings.api.exception.model.ServiceErrorResponse;
import se.sundsvall.feedbacksettings.api.exception.model.TechnicalDetails;

@Provider
public class JsonMappingExceptionMapper extends AbstractExceptionMapper<JsonMappingException> {

	private static final Logger LOGGER = getLogger(JsonMappingExceptionMapper.class);

	@Override
	public Response toResponse(JsonMappingException e) {

		LOGGER.info("Mapping exception into ServiceErrorResponse", e);

		final var serviceErrorResponse = ServiceErrorResponse.create()
			.withMessage("Bad request format").withHttpCode(BAD_REQUEST.getStatusCode())
			.withTechnicalDetails(TechnicalDetails.create()
				.withRootCode(BAD_REQUEST.getStatusCode())
				.withRootCause(e.getOriginalMessage())
				.withServiceId(getApplicationName())
				.withDetails(List.of("Request: " + uriInfo.getPath())));

		return wrapServiceErrorResponse(serviceErrorResponse);
	}
}