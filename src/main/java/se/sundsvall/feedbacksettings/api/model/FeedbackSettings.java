package se.sundsvall.feedbacksettings.api.model;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Objects;

import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Schema(description = "Feedback settings response model")
public class FeedbackSettings {
	@Schema(description = "Unique id for the feedback setting", example = "0d64c132-3aea-11ec-8d3d-0242ac130003", readOnly = true)
	private String id;

	@Schema(description = "Unique id for the person to whom the feedback setting applies", example = "15aee472-46ab-4f03-9605-68bd64ebc73f", readOnly = true)
	private String personId;

	@Schema(description = "Unique id for the company to which the feedback setting applies in cases where the setting refers to an organizational representative", 
			example = "15aee472-46ab-4f03-9605-68bd64ebc84a", readOnly = true)
	private String organizationId;
	
	@Schema(type = SchemaType.ARRAY, implementation = FeedbackChannel.class, readOnly = true)
	private List<FeedbackChannel> channels;
	
	@Schema(description = "Timestamp for creation", example = "2022-01-20T10:30:09.469+01:00", readOnly = true)
	private OffsetDateTime created;

	@Schema(description = "Timestamp for last modification", example = "2022-01-20T10:30:09.469+01:00", readOnly = true)
	private OffsetDateTime modified;
	
	public static FeedbackSettings create() {
		return new FeedbackSettings();
	}
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
	
	public FeedbackSettings withId(String id) {
		this.id = id;
		return this;
	}
	
	public String getPersonId() {
		return personId;
	}

	public void setPersonId(String personId) {
		this.personId = personId;
	}

	public FeedbackSettings withPersonId(String personId) {
		this.personId = personId;
		return this;
	}

	public String getOrganizationId() {
		return organizationId;
	}

	public void setOrganizationId(String organizationId) {
		this.organizationId = organizationId;
	}

	public FeedbackSettings withOrganizationId(String organizationId) {
		this.organizationId = organizationId;
		return this;
	}

	public List<FeedbackChannel> getChannels() {
		return channels;
	}

	public void setChannels(List<FeedbackChannel> channels) {
		this.channels = channels;
	}

	public FeedbackSettings withChannels(List<FeedbackChannel> channels) {
		this.channels = channels;
		return this;
	}

	public OffsetDateTime getCreated() {
		return created;
	}

	public void setCreated(OffsetDateTime created) {
		this.created = created;
	}

	public FeedbackSettings withCreated(OffsetDateTime created) {
		this.created = created;
		return this;
	}
	
	public OffsetDateTime getModified() {
		return modified;
	}

	public void setModified(OffsetDateTime modified) {
		this.modified = modified;
	}
	
	public FeedbackSettings withModified(OffsetDateTime modified) {
		this.modified = modified;
		return this;
	}

	@Override
	public int hashCode() {
		return Objects.hash(channels, created, id, modified, organizationId, personId);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		FeedbackSettings other = (FeedbackSettings) obj;
		return Objects.equals(channels, other.channels) && Objects.equals(created, other.created) && id == other.id
				&& Objects.equals(modified, other.modified) && Objects.equals(organizationId, other.organizationId)
				&& Objects.equals(personId, other.personId);
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("FeedbackSettings [id=").append(id).append(", personId=").append(personId)
				.append(", organizationId=").append(organizationId).append(", channels=").append(channels)
				.append(", created=").append(created).append(", modified=").append(modified).append("]");
		return builder.toString();
	}
}
