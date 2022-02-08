package se.sundsvall.feedbacksettings.integration.db.model;

import static java.time.OffsetDateTime.now;
import static java.time.temporal.ChronoUnit.MILLIS;

import java.io.Serializable;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Objects;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.hibernate.annotations.Filter;
import org.hibernate.annotations.FilterDef;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.ParamDef;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;

@Entity
@Table(name = "feedback_settings",
	uniqueConstraints = {
			@UniqueConstraint(name= "feedback_settings_person_id_organization_id_unique_constraint", columnNames = {"person_id", "organization_id"})},
	indexes = {
			@Index(name = "feedback_settings_organization_id_index", columnList = "organization_id"),
			@Index(name = "feedback_settings_person_id_index", columnList = "person_id")
})
@FilterDef(name = FeedbackSettingsEntity.FEEDBACK_SETTINGS_ORGANIZATION_ID_FILTER, parameters = { @ParamDef(name = "organizationIdFilter", type = "string") })
@FilterDef(name = FeedbackSettingsEntity.FEEDBACK_SETTINGS_PERSON_ID_FILTER, parameters = { @ParamDef(name = "personIdFilter", type = "string") })
@Filter(name = FeedbackSettingsEntity.FEEDBACK_SETTINGS_ORGANIZATION_ID_FILTER, condition = "organization_id like :organizationIdFilter")
@Filter(name = FeedbackSettingsEntity.FEEDBACK_SETTINGS_PERSON_ID_FILTER, condition = "person_id like :personIdFilter")
public class FeedbackSettingsEntity extends PanacheEntityBase implements Serializable {

	public static final String FEEDBACK_SETTINGS_ORGANIZATION_ID_FILTER = "feedbackSettingsOrganizationIdFilter";
	public static final String FEEDBACK_SETTINGS_PERSON_ID_FILTER = "feedbackSettingsPersonIdFilter";
	
	private static final long serialVersionUID = 4363931508904928891L;

	@Id
	@GenericGenerator(name = "string_based_uuid", strategy = "org.hibernate.id.UUIDGenerator")	
	@GeneratedValue(generator = "string_based_uuid")  	
	@Column(name = "id")
	private String id;

	@Column(name = "person_id", nullable = false)
	private String personId;

	@Column(name = "organization_id")
	private String organizationId;

	@ElementCollection
	@CollectionTable(name = "feedback_channels", 
		joinColumns = @JoinColumn(name = "setting_id"), 
		foreignKey = @ForeignKey(name = "fk_feedback_settings_id"),
		uniqueConstraints = {
			@UniqueConstraint(name = "feedback_channels_unique_combinations_constraint", columnNames = {"setting_id", "contact_method", "destination"})}
	)
	private List<FeedbackChannelEmbeddable> feedbackChannels;
	
	@Column(name = "created", nullable = false)
	private OffsetDateTime created;

	@Column(name = "modified")
	private OffsetDateTime modified;

	public static FeedbackSettingsEntity create() {
		return new FeedbackSettingsEntity();
	}
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public FeedbackSettingsEntity withId(String id) {
		this.id = id;
		return this;
	}

	public String getPersonId() {
		return personId;
	}

	public void setPersonId(String personId) {
		this.personId = personId;
	}

	public FeedbackSettingsEntity withPersonId(String personId) {
		this.personId = personId;
		return this;
	}

	public String getOrganizationId() {
		return organizationId;
	}

	public void setOrganizationId(String organizationId) {
		this.organizationId = organizationId;
	}

	public FeedbackSettingsEntity withOrganizationId(String organizationId) {
		this.organizationId = organizationId;
		return this;
	}

	public List<FeedbackChannelEmbeddable> getFeedbackChannels() {
		return feedbackChannels;
	}

	public void setFeedbackChannels(List<FeedbackChannelEmbeddable> feedbackChannels) {
		this.feedbackChannels = feedbackChannels;
	}

	public FeedbackSettingsEntity withFeedbackChannels(List<FeedbackChannelEmbeddable> feedbackChannels) {
		this.feedbackChannels = feedbackChannels;
		return this;
	}

	public OffsetDateTime getCreated() {
		return created;
	}

	public void setCreated(OffsetDateTime created) {
		this.created = created;
	}

	public FeedbackSettingsEntity withCreated(OffsetDateTime created) {
		this.created = created;
		return this;
	}

	public OffsetDateTime getModified() {
		return modified;
	}

	public void setModified(OffsetDateTime modified) {
		this.modified = modified;
	}

	public FeedbackSettingsEntity withModified(OffsetDateTime modified) {
		this.modified = modified;
		return this;
	}

	@PrePersist
	void prePersist() {
		created = now().truncatedTo(MILLIS);
	}

	@PreUpdate
	public void preUpdate() {
		modified = now().truncatedTo(MILLIS);
	}

	@Override
	public int hashCode() {
		return Objects.hash(created, feedbackChannels, id, modified, organizationId, personId);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		FeedbackSettingsEntity other = (FeedbackSettingsEntity) obj;
		return Objects.equals(created, other.created) && Objects.equals(feedbackChannels, other.feedbackChannels)
				&& id == other.id && Objects.equals(modified, other.modified)
				&& Objects.equals(organizationId, other.organizationId) && Objects.equals(personId, other.personId);
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("FeedbackSettingsEntity [id=").append(id).append(", personId=").append(personId)
				.append(", organizationId=").append(organizationId).append(", feedbackChannels=")
				.append(feedbackChannels).append(", created=").append(created).append(", modified=").append(modified)
				.append("]");
		return builder.toString();
	}
}
