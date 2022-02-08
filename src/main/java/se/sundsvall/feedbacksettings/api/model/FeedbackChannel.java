package se.sundsvall.feedbacksettings.api.model;

import java.util.Objects;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

import se.sundsvall.feedbacksettings.ContactMethod;

@Schema(description = "Feedback channel model")
public class FeedbackChannel {

	@Schema(description = "Method of contact", example = "SMS", readOnly = true)
	private ContactMethod contactMethod;

	@Schema(description = "Point of destination", readOnly = true)
	private String destination;

	@Schema(description = "Signal if channel should be used or not when sending feedback ", example = "true", readOnly = true)
	private boolean sendFeedback;
	
	public static FeedbackChannel create() {
		return new FeedbackChannel();				
	}

	public ContactMethod getContactMethod() {
		return contactMethod;
	}

	public void setContactMethod(ContactMethod contactMethod) {
		this.contactMethod = contactMethod;
	}

	public FeedbackChannel withContactMethod(ContactMethod contactMethod) {
		this.contactMethod = contactMethod;
		return this;
	}

	public String getDestination() {
		return destination;
	}

	public void setDestination(String destination) {
		this.destination = destination;
	}

	public FeedbackChannel withDestination(String destination) {
		this.destination = destination;
		return this;
	}

	public boolean isSendFeedback() {
		return sendFeedback;
	}

	public void setSendFeedback(boolean sendFeedback) {
		this.sendFeedback = sendFeedback;
	}
	
	public FeedbackChannel withSendFeedback(boolean sendFeedback) {
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
		FeedbackChannel other = (FeedbackChannel) obj;
		return contactMethod == other.contactMethod
				&& Objects.equals(destination, other.destination)
				&& sendFeedback == other.sendFeedback;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("FeedbackChannel [contactMethod=").append(contactMethod).append(", destination=")
				.append(destination).append(", sendFeedback=").append(sendFeedback).append("]");
		return builder.toString();
	}
	
}
