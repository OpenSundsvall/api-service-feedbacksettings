package se.sundsvall.feedbacksettings.api.exception.mapper;

import static javax.ws.rs.core.Response.Status.BAD_REQUEST;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import javax.ws.rs.core.UriInfo;

import org.eclipse.microprofile.config.Config;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.fasterxml.jackson.databind.JsonMappingException;

import se.sundsvall.feedbacksettings.api.exception.model.ServiceErrorResponse;
import se.sundsvall.feedbacksettings.api.exception.model.TechnicalDetails;

@ExtendWith(MockitoExtension.class)
class JsonMappingExceptionMapperTest {

	private static final String REQUEST_PATH = "http://localhost:1234/path";
	private static final String APPLICATION_NAME = "The-app";
	private static final String EXCEPTION_MESSAGE = "Cannot deserialize value from Array value (token `JsonToken.START_ARRAY`)";

	@Mock
	private JsonMappingException jsonMappingExceptionMock;

	@Mock
	private UriInfo uriInfoMock;

	@Mock
	private Config configMock;

	@InjectMocks
	private JsonMappingExceptionMapper exceptionMapper;

	@BeforeEach
	void setup() {

		when(configMock.getOptionalValue("quarkus.application.name", String.class)).thenReturn(Optional.of(APPLICATION_NAME));
		when(uriInfoMock.getPath()).thenReturn(REQUEST_PATH);
		when(jsonMappingExceptionMock.getOriginalMessage()).thenReturn(EXCEPTION_MESSAGE);
	}

	@Test
	void constraintValidationException() {

		final var response = exceptionMapper.toResponse(jsonMappingExceptionMock).readEntity(ServiceErrorResponse.class);

		assertThat(response).isEqualTo(ServiceErrorResponse.create()
			.withMessage("Bad request format")
			.withHttpCode(BAD_REQUEST.getStatusCode())
			.withTechnicalDetails(TechnicalDetails.create()
				.withRootCode(BAD_REQUEST.getStatusCode())
				.withRootCause(EXCEPTION_MESSAGE)
				.withServiceId(APPLICATION_NAME)
				.withDetails(List.of("Request: ".concat(REQUEST_PATH)))));
	}
}
