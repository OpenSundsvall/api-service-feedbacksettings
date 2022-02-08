package se.sundsvall.feedbacksettings;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import se.sundsvall.feedbacksettings.api.exception.ServiceRuntimeException;

class ContactMethodTest {

	@Test
	void testValidEnumValues() {
		for (ContactMethod method : ContactMethod.values()) {
			assertThat(method).isEqualTo(ContactMethod.toEnum(method.name()));
		}
	}

	@Test
	void testUnknownEnumValue() {
		final var exception = assertThrows(ServiceRuntimeException.class, 
				() -> ContactMethod.toEnum("UNKNOWN"));
		assertThat(exception.getMessage()).isEqualTo("Invalid value for enum ContactMethod: UNKNOWN");
	}
}
