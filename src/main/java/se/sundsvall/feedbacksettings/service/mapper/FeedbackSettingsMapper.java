package se.sundsvall.feedbacksettings.service.mapper;

import static java.util.Collections.emptyList;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static se.sundsvall.feedbacksettings.service.util.MappingUtils.getAddedFeedbackChannels;
import static se.sundsvall.feedbacksettings.service.util.MappingUtils.getRemovedFeedbackChannels;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import se.sundsvall.feedbacksettings.api.model.CreateFeedbackSettingsRequest;
import se.sundsvall.feedbacksettings.api.model.FeedbackChannel;
import se.sundsvall.feedbacksettings.api.model.FeedbackSettings;
import se.sundsvall.feedbacksettings.api.model.RequestedFeedbackChannel;
import se.sundsvall.feedbacksettings.api.model.UpdateFeedbackSettingsRequest;
import se.sundsvall.feedbacksettings.integration.db.model.FeedbackChannelEmbeddable;
import se.sundsvall.feedbacksettings.integration.db.model.FeedbackSettingsEntity;

public class FeedbackSettingsMapper {
	private FeedbackSettingsMapper() {}
	
	public static FeedbackSettingsEntity toFeedbackSettingsEntity(CreateFeedbackSettingsRequest feedbackSettings) {
		if (isNull(feedbackSettings)) return null;
		
		return FeedbackSettingsEntity.create()
				.withPersonId(feedbackSettings.getPersonId())
				.withOrganizationId(feedbackSettings.getOrganizationId())
				.withFeedbackChannels(toFeedbackChannelEmbeddables(feedbackSettings.getChannels()));
	}
	
	private static List<FeedbackChannelEmbeddable> toFeedbackChannelEmbeddables(List<RequestedFeedbackChannel> feedbackChannels) {
		
		return Optional.ofNullable(feedbackChannels).orElse(emptyList()).stream()
				.filter(Objects::nonNull)
				.map(channel -> FeedbackChannelEmbeddable.create()
						.withContactMethod(channel.getContactMethod())
						.withDestination(channel.getDestination())
						.withSendFeedback(channel.getSendFeedback()))
				.toList();
	}
	
	public static FeedbackSettings toFeedbackSettings(FeedbackSettingsEntity entity) {
		if (isNull(entity)) return null;
		
		return FeedbackSettings.create()
				.withId(entity.getId())
				.withPersonId(entity.getPersonId())
				.withOrganizationId(entity.getOrganizationId())
				.withChannels(toFeedbackChannels(entity.getFeedbackChannels()))
				.withCreated(entity.getCreated())
				.withModified(entity.getModified());
	}
	
	private static List<FeedbackChannel> toFeedbackChannels(List<FeedbackChannelEmbeddable> feedbackChannelEmbeddables) {
		return Optional.ofNullable(feedbackChannelEmbeddables).orElse(emptyList()).stream()
				.filter(Objects::nonNull)
				.map(entity -> FeedbackChannel.create()
						.withContactMethod(entity.getContactMethod())
						.withDestination(entity.getDestination())
						.withSendFeedback(entity.isSendFeedback()))
				.toList();
	}
	
	public static List<FeedbackSettings> toFeedbackSettings(List<FeedbackSettingsEntity> entities) {
		return Optional.ofNullable(entities).orElse(emptyList()).stream()
				.map(FeedbackSettingsMapper::toFeedbackSettings)
				.toList();
	}
	
	public static void mergeFeedbackSettings(FeedbackSettingsEntity entity, UpdateFeedbackSettingsRequest feedbackSettings) {
		if (nonNull(feedbackSettings) && nonNull(feedbackSettings.getChannels())) {
			List<FeedbackChannelEmbeddable> requestedChannelEntities = toFeedbackChannelEmbeddables(feedbackSettings.getChannels());
			
			// Save current channels before modificaton
			List<FeedbackChannelEmbeddable> oldChannelEntities = new ArrayList<>(entity.getFeedbackChannels());
			
			entity.getFeedbackChannels().addAll(getAddedFeedbackChannels(entity, requestedChannelEntities));
			entity.getFeedbackChannels().removeAll(getRemovedFeedbackChannels(entity, requestedChannelEntities));
			
			// Compare old and new and if they differ, call preUpdate as Hibernate has an open bug regarding @preUpdate doesn't get triggered when collection is updated
			if (!oldChannelEntities.equals(entity.getFeedbackChannels())) {
				entity.preUpdate();
			}
		}
	}
}
