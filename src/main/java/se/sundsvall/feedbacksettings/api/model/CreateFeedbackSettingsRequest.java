package se.sundsvall.feedbacksettings.api.model;

import java.util.List;
import java.util.Objects;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import se.sundsvall.feedbacksettings.api.validation.UniqueFeedbackChannels;
import se.sundsvall.feedbacksettings.api.validation.ValidUuid;

@Schema(description = "Request model for creating new feedback settings")
public class CreateFeedbackSettingsRequest {
	@Schema(description = "Unique id for the person to whom the feedback setting shall apply", example = "15aee472-46ab-4f03-9605-68bd64ebc73f")
	@ValidUuid
	@NotNull
	private String personId;

	@Schema(description = "Unique id for the company to which the feedback setting shall apply if the setting refers to an organizational representative", 
			example = "15aee472-46ab-4f03-9605-68bd64ebc84a")
	@ValidUuid
	private String organizationId;

	@Schema(type = SchemaType.ARRAY, implementation = RequestedFeedbackChannel.class)
	@UniqueFeedbackChannels
	private List<@Valid RequestedFeedbackChannel> channels;
	
	public static CreateFeedbackSettingsRequest create() {
		return new CreateFeedbackSettingsRequest();
	}
	
	public String getPersonId() {
		return personId;
	}

	public void setPersonId(String personId) {
		this.personId = personId;
	}

	public CreateFeedbackSettingsRequest withPersonId(String personId) {
		this.personId = personId;
		return this;
	}

	public String getOrganizationId() {
		return organizationId;
	}

	public void setOrganizationId(String organizationId) {
		this.organizationId = organizationId;
	}

	public CreateFeedbackSettingsRequest withOrganizationId(String organizationId) {
		this.organizationId = organizationId;
		return this;
	}

	public List<RequestedFeedbackChannel> getChannels() {
		return channels;
	}

	public void setChannels(List<RequestedFeedbackChannel> channels) {
		this.channels = channels;
	}

	public CreateFeedbackSettingsRequest withChannels(List<RequestedFeedbackChannel> channels) {
		this.channels = channels;
		return this;
	}

	@Override
	public int hashCode() {
		return Objects.hash(channels, organizationId, personId);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		CreateFeedbackSettingsRequest other = (CreateFeedbackSettingsRequest) obj;
		return Objects.equals(channels, other.channels) && Objects.equals(organizationId, other.organizationId)
				&& Objects.equals(personId, other.personId);
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("CreateFeedbackSettingsRequest [personId=").append(personId).append(", organizationId=")
				.append(organizationId).append(", channels=").append(channels).append("]");
		return builder.toString();
	}
}
