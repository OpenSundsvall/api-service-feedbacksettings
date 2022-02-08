package se.sundsvall.feedbacksettings.api.model;

import java.util.List;
import java.util.Objects;

import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import com.fasterxml.jackson.annotation.JsonProperty;

@Schema(description = "Search result response model")
public class SearchResult {

	@JsonProperty("_meta")
	@Schema(implementation = MetaData.class)
	private MetaData metaData;
	
	@Schema(type = SchemaType.ARRAY, implementation = FeedbackSettings.class, readOnly = true)
	private List<FeedbackSettings> feedbackSettings;

	public static SearchResult create() {
		return new SearchResult();
	}
	
	public List<FeedbackSettings> getFeedbackSettings() {
		return feedbackSettings;
	}
	
	public void setFeedbackSettings(List<FeedbackSettings> feedbackSettings) {
		this.feedbackSettings = feedbackSettings;
	}

	public SearchResult withFeedbackSettings(List<FeedbackSettings> feedbackSettings) {
		this.feedbackSettings = feedbackSettings;
		return this;
	}

	public MetaData getMetaData() {
		return metaData;
	}

	public void setMetaData(MetaData metaData) {
		this.metaData = metaData;
	}

	public SearchResult withMetaData(MetaData metaData) {
		this.metaData = metaData;
		return this;
	}

	@Override
	public int hashCode() {
		return Objects.hash(feedbackSettings, metaData);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SearchResult other = (SearchResult) obj;
		return Objects.equals(feedbackSettings, other.feedbackSettings) && Objects.equals(metaData, other.metaData);
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("SearchResult [metaData=").append(metaData).append(", feedbackSettings=").append(feedbackSettings)
				.append("]");
		return builder.toString();
	}
}
