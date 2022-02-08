package se.sundsvall.feedbacksettings.api.model;

import java.util.Objects;

import javax.validation.constraints.NotNull;

import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import se.sundsvall.feedbacksettings.ContactMethod;
import se.sundsvall.feedbacksettings.api.validation.ValidDestinationFormat;

@Schema(description = "Requested feedback channel model")
@ValidDestinationFormat
public class RequestedFeedbackChannel {

	@Schema(description = "Method of contact", example = "SMS", required = true)
	@NotNull
	private ContactMethod contactMethod;

	@Schema(description = "Point of destination", example = "0701234567", required = true)
	private String destination;

	@Schema(type = SchemaType.BOOLEAN, description = "Signal if channel should be used or not when sending feedback ", example = "true", required = true)
	@NotNull
	private Boolean sendFeedback;
	
	public static RequestedFeedbackChannel create() {
		return new RequestedFeedbackChannel();				
	}

	public ContactMethod getContactMethod() {
		return contactMethod;
	}

	public void setContactMethod(ContactMethod contactMethod) {
		this.contactMethod = contactMethod;
	}

	public RequestedFeedbackChannel withContactMethod(ContactMethod contactMethod) {
		this.contactMethod = contactMethod;
		return this;
	}

	public String getDestination() {
		return destination;
	}

	public void setDestination(String destination) {
		this.destination = destination;
	}

	public RequestedFeedbackChannel withDestination(String destination) {
		this.destination = destination;
		return this;
	}

	public Boolean getSendFeedback() {
		return sendFeedback;
	}

	public void setSendFeedback(Boolean sendFeedback) {
		this.sendFeedback = sendFeedback;
	}
	
	public RequestedFeedbackChannel withSendFeedback(Boolean sendFeedback) {
		this.sendFeedback = sendFeedback;
		return this;
	}

	@Override
	public int hashCode() {
		return Objects.hash(contactMethod, destination, sendFeedback);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		RequestedFeedbackChannel other = (RequestedFeedbackChannel) obj;
		return contactMethod == other.contactMethod && Objects.equals(destination, other.destination)
				&& Objects.equals(sendFeedback, other.sendFeedback);
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("RequestedFeedbackChannel [contactMethod=").append(contactMethod).append(", destination=")
				.append(destination).append(", sendFeedback=").append(sendFeedback).append("]");
		return builder.toString();
	}

}
