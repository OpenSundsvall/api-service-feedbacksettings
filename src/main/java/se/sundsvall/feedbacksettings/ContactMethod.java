package se.sundsvall.feedbacksettings;

import java.util.stream.Stream;

import se.sundsvall.feedbacksettings.api.exception.ServiceException;

public enum ContactMethod {
	SMS, EMAIL;
	
	public static ContactMethod toEnum(String value) {
		return Stream.of(ContactMethod.values())
				.filter(method -> method.name().equalsIgnoreCase(value))
				.findAny()
				.orElseThrow(() -> 
					ServiceException.create("Invalid value for enum ContactMethod: " + value).asRuntimeException());

	}
}
