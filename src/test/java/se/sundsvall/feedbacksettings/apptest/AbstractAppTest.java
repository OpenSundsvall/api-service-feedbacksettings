package se.sundsvall.feedbacksettings.apptest;

import static java.lang.Class.forName;
import static java.lang.System.lineSeparator;
import static java.util.Objects.nonNull;
import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.joining;
import static org.assertj.core.api.Assertions.assertThat;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.ws.rs.HttpMethod;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.assertj.core.api.AbstractAssert;
import org.assertj.core.api.BooleanAssert;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;

import net.javacrumbs.jsonunit.JsonAssert;
import net.javacrumbs.jsonunit.core.Option;

abstract class AbstractAppTest {

	private static final HttpClient HTTP_CLIENT = HttpClient.newBuilder()
		.executor(Executors.newFixedThreadPool(3))
		.version(HttpClient.Version.HTTP_2)
		.build();

	private static final String CONTENT_TYPE_HEADER_NAME = "Content-Type";
	private static final String X_TESTCASE_HEADER_NAME = "x-testCase";
	private static final ObjectMapper  JSON_MAPPER = JsonMapper.builder().findAndAddModules().build();

	private static final String FILES_DIRECTORY = "__files/";

	private String servicePath;
	private String method;
	private String requestBody;
	private String mappingPath;
	private Map<String, Object> attributeValues;
	private Map<String, String> headerValues;
	private Response.Status expectedResponseStatus;
	private Map<String, List<String>> expectedResponseHeaders;
	private String expectedResponseBody;
	private boolean expectedResponseBodyIsNullOrEmpty;
	private String responseBody;
	
	protected AbstractAppTest setupCall() throws Exception {
		final var testClassName = getTestClassName();
		mappingPath = testClassName;
		if (!mappingPath.endsWith("/")) {
			mappingPath += "/";
		}

		return this;
	}

	protected AbstractAppTest withHttpMethod(final String method) {
		this.method = method;
		return this;
	}

	protected AbstractAppTest withAttributeReplacement(final String jsonPattern, final Object replacement) {
		if (attributeValues == null) {
			attributeValues = new HashMap<>();
		}
		attributeValues.put(jsonPattern, replacement);
		return this;
	}

	protected AbstractAppTest withExpectedResponseStatus(final Response.Status expectedResponseStatus) {
		this.expectedResponseStatus = expectedResponseStatus;
		return this;
	}

	protected AbstractAppTest withHeader(final String key, final String value) {
		if (headerValues == null) {
			headerValues = new HashMap<>();
		}
		headerValues.put(key, value);
		return this;
	}

	protected AbstractAppTest withExpectedResponseHeader(final String expectedHeaderKey, final List<String> expectedHeaderValues) {
		if (expectedResponseHeaders == null) {
			expectedResponseHeaders = new HashMap<>();
		}
		expectedResponseHeaders.put(expectedHeaderKey, expectedHeaderValues);
		return this;
	}

	protected AbstractAppTest withExpectedResponse(final String expectedResponse) throws Exception {
		return withExpectedResponse(expectedResponse, null);
	}

	protected AbstractAppTest withExpectedResponse(final String expectedResponse, final Map<String, Object> replacements) throws Exception {
		final var contentFromFile = fromTestFile(expectedResponse);
		if (nonNull(contentFromFile)) {
			expectedResponseBody = contentFromFile;
		} else {
			expectedResponseBody = expectedResponse;
		}

		if (nonNull(replacements)) {
			DocumentContext doc = JsonPath.parse(expectedResponseBody);
			replacements.forEach(doc::set);
			expectedResponseBody = doc.jsonString();
		}

		return this;
	}

	protected AbstractAppTest withExpectedResponseBodyIsNullOrEmpty() {
		this.expectedResponseBodyIsNullOrEmpty = true;
		return this;
	}

	protected AbstractAppTest withServicePath(final String servicePath) {
		this.servicePath = servicePath;
		return this;
	}

	protected AbstractAppTest withRequest(final String request) throws Exception {
		final var contentFromFile = fromTestFile(request);
		if (nonNull(contentFromFile)) {
			requestBody = contentFromFile;
		} else {
			requestBody = request;
		}
		return this;
	}

	protected AbstractAppTest withOptions(List<Option> options) {
		JsonAssert.resetOptions();
		if (nonNull(options)) {
			JsonAssert.setOptions(options.size() == 1 ? options.get(0) : options.get(0), options.subList(1, options.size()).toArray(new Option[0]) );
		}
		return this;
	}
	
	protected void sendRequestAndVerifyResponse(final MediaType mediaType) throws Exception {
		final var request = httpClientRequest(method, servicePath, mediaType, modifyRequestAttributes(requestBody));
		final var response = HTTP_CLIENT.send(request, HttpResponse.BodyHandlers.ofString());

		responseBody = response.body();

		if (nonNull(expectedResponseHeaders)) {
			for (Map.Entry<String, List<String>> header : expectedResponseHeaders.entrySet()) {
				final var headerMap = response.headers().map();
				assertThat(headerMap).containsKey(header.getKey());
				
				List<String> values = headerMap.get(header.getKey());
				values.forEach(value -> assertThat(matchesOneOf(header.getKey(), value, header.getValue())));
			}
		}
		assertThat(response.statusCode()).isEqualTo(expectedResponseStatus.getStatusCode());
		if (nonNull(expectedResponseBody) && nonNull(responseBody)) {
			JsonAssert.assertJsonEquals(expectedResponseBody, responseBody);
		}
		if (expectedResponseBodyIsNullOrEmpty) {
			assertThat(responseBody).isNullOrEmpty();
		}
	}
	
	private AbstractAssert<?,?> matchesOneOf(String headerKey, String headerValue, List<String> expectedMatches) {
		AtomicBoolean matches = new AtomicBoolean();
		expectedMatches.forEach(expectedMatch -> matches.set(matches.get() || headerValue.matches(expectedMatch)));
		return new BooleanAssert(matches.get()).withFailMessage(
				"Header '%s' was expected to contain a value matching '%s' but the value '%s' did not meet the expectation", 
				headerKey, 
				expectedMatches, 
				headerValue).isTrue();
	}
	
	protected AbstractAppTest sendRequestAndVerifyResponse() throws Exception {
		sendRequestAndVerifyResponse(MediaType.APPLICATION_JSON_TYPE);
		return this;
	}
	
	protected <T> T andReturnBody(Class<T> clazz) throws JsonMappingException, JsonProcessingException, ClassNotFoundException {
		return clazz.cast(JSON_MAPPER.readValue(responseBody, forName(clazz.getName())));
	}
	
	protected String deleteJsonAttributes(final String originalJson, final String... pathToExclude) {
		final var document = JsonPath.parse(originalJson);
		for (String path : pathToExclude) {
			document.delete(path);
		}
		return document.jsonString();
	}

	private HttpRequest httpClientRequest(final String method, final String servicePath, final MediaType mediaType, final String body) throws Exception {
		final var builder = HttpRequest.newBuilder()
			.uri(URI.create("http://localhost:8081" + servicePath))
			.header(CONTENT_TYPE_HEADER_NAME, mediaType.toString())
			.header(X_TESTCASE_HEADER_NAME, getClass().getSimpleName() + "." + getTestMethodName());

		if (headerValues != null && !headerValues.isEmpty()) {
			headerValues.forEach(builder::header);
		}

		return switch (method) {
			case HttpMethod.GET -> builder.GET().build();
			case HttpMethod.POST -> builder.POST(HttpRequest.BodyPublishers.ofString(body)).build();
			case HttpMethod.PUT -> builder.PUT(HttpRequest.BodyPublishers.ofString(body)).build();
			case HttpMethod.PATCH -> builder.method("PATCH", HttpRequest.BodyPublishers.ofString(body)).build();
			case HttpMethod.DELETE -> builder.DELETE().build();
			default -> throw new IllegalArgumentException("Unsupported HTTP method: " + method);
		};
	}

	private String modifyRequestAttributes(final String originalRequest) {
		var request = originalRequest;

		final var optionalAttributes = ofNullable(attributeValues);
		if (optionalAttributes.isPresent()) {
			DocumentContext doc = JsonPath.parse(requestBody);
			optionalAttributes.get().forEach(doc::set);
			request = doc.jsonString();
		}

		return request;
	}

	protected String fromTestFile(final String fileName) throws Exception {
		return fromClasspath(FILES_DIRECTORY + mappingPath + getTestMethodName() + "/" + fileName);
	}

	private String fromClasspath(final String path) {
		try (InputStream is = getClasspathResourceAsStream(path.startsWith("/") ? path.substring(1) : path)) {
			return convertStreamToString(is);
		} catch (Exception e) {
			throw new IllegalArgumentException("Cannot load classpath resource: '" + path + "'", e);
		}
	}

	private InputStream getClasspathResourceAsStream(final String resourceName) {
		final var classLoader = Thread.currentThread().getContextClassLoader();

		return ofNullable(classLoader.getResourceAsStream(resourceName))
			.orElseThrow(() -> new IllegalArgumentException("Resource not found with name: " + resourceName));
	}

	private String convertStreamToString(final InputStream is) {
		return new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))
			.lines()
			.collect(joining(lineSeparator()));
	}

	private String getTestMethodName() throws Exception {
		return Arrays.stream(Thread.currentThread().getStackTrace())
			.map(StackTraceElement::getMethodName)
			.filter(methodName -> methodName.startsWith("test"))
			.findFirst()
			.orElseThrow(() -> new Exception("Could not find method name! Test method must start with 'test'"));
	}

	private String getTestClassName() {
		final var className = getClass().getSimpleName();
		final var indexOfClassNameSuffixStart = className.indexOf("_");

		// Remove Quarkus generated sub class name suffixes (e.g. TestClass_SubClass -> TestClass)
		if (indexOfClassNameSuffixStart > 0) {
			return className.substring(0, indexOfClassNameSuffixStart);
		}

		return className;
	}
}
