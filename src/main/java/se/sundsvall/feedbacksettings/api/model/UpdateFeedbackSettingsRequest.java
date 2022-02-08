package se.sundsvall.feedbacksettings.api.model;

import java.util.List;
import java.util.Objects;

import javax.validation.Valid;

import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import se.sundsvall.feedbacksettings.api.validation.UniqueFeedbackChannels;

@Schema(description = "Request model for updating feedback settings")
public class UpdateFeedbackSettingsRequest {
	@Schema(type = SchemaType.ARRAY, implementation = RequestedFeedbackChannel.class)
	@UniqueFeedbackChannels
	private List<@Valid RequestedFeedbackChannel> channels;

	public static UpdateFeedbackSettingsRequest create() {
		return new UpdateFeedbackSettingsRequest();
	}

	public List<RequestedFeedbackChannel> getChannels() {
		return channels;
	}

	public void setChannels(List<RequestedFeedbackChannel> channels) {
		this.channels = channels;
	}

	public UpdateFeedbackSettingsRequest withChannels(List<RequestedFeedbackChannel> channels) {
		this.channels = channels;
		return this;
	}

	@Override
	public int hashCode() {
		return Objects.hash(channels);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		UpdateFeedbackSettingsRequest other = (UpdateFeedbackSettingsRequest) obj;
		return Objects.equals(channels, other.channels);
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("UpdateFeedbackSettingsRequest [channels=").append(channels).append("]");
		return builder.toString();
	}
}
