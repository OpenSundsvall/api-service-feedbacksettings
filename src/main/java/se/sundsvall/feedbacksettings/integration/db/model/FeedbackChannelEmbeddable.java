package se.sundsvall.feedbacksettings.integration.db.model;

import static java.util.Objects.isNull;

import java.io.Serializable;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import se.sundsvall.feedbacksettings.ContactMethod;

@Embeddable
public class FeedbackChannelEmbeddable implements Serializable {
	private static final long serialVersionUID = -5056106879518247872L;

	@Column(name = "contact_method", nullable = false)
	private String contactMethod;

	@Column(name = "destination", nullable = false)
	private String destination;

	@Column(name = "send_feedback", nullable = false)
	private boolean sendFeedback;

	public static FeedbackChannelEmbeddable create() {
		return new FeedbackChannelEmbeddable();
	}
	
	public String getDestination() {
		return destination;
	}

	public void setDestination(String destination) {
		this.destination = destination;
	}

	public FeedbackChannelEmbeddable withDestination(String destination) {
		this.destination = destination;
		return this;
	}

	public ContactMethod getContactMethod() {
		return isNull(contactMethod) ? null : ContactMethod.toEnum(contactMethod);
	}

	public void setContactMethod(ContactMethod contactMethod) {
		this.contactMethod = isNull(contactMethod) ? null : contactMethod.name();
	}

	public FeedbackChannelEmbeddable withContactMethod(ContactMethod contactMethod) {
		this.contactMethod = isNull(contactMethod) ? null : contactMethod.name();
		return this;
	}

	public boolean isSendFeedback() {
		return sendFeedback;
	}

	public void setSendFeedback(boolean sendFeedback) {
		this.sendFeedback = sendFeedback;
	}

	public FeedbackChannelEmbeddable withSendFeedback(boolean sendFeedback) {
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
		FeedbackChannelEmbeddable other = (FeedbackChannelEmbeddable) obj;
		return Objects.equals(contactMethod, other.contactMethod) && Objects.equals(destination, other.destination)
				&& sendFeedback == other.sendFeedback;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("FeedbackChannelEmbeddable [contactMethod=").append(contactMethod)
				.append(", destination=").append(destination).append(", sendFeedback=").append(sendFeedback)
				.append("]");
		return builder.toString();
	}
}
