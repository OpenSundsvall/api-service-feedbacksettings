package se.sundsvall.feedbacksettings.service.util;

import static java.util.Collections.emptyList;
import static java.util.Objects.isNull;
import static java.util.Optional.ofNullable;
import static org.apache.commons.lang3.StringUtils.equalsIgnoreCase;

import java.util.List;
import java.util.Optional;

import se.sundsvall.feedbacksettings.integration.db.model.FeedbackChannelEmbeddable;
import se.sundsvall.feedbacksettings.integration.db.model.FeedbackSettingsEntity;

public class MappingUtils {
	
	private MappingUtils() {}
	
	/**
	 * Returns all removed FeedbackChannelEmbeddable elements from oldSettingsEntity.getFeedbackChannels(), when comparing with
	 * newFeedbackChannels.
	 * 
	 * E.g.
	 * 
	 * oldSettingsEntity.getFeedbackChannels() contains: <ELEMENT-1>, <ELEMENT-2>, <ELEMENT-3>
	 * newFeedbackChannels contains: <ELEMENT-1>, <ELEMENT-3>
	 * 
	 * Result: This method will return [<ELEMENT-2>]
	 * 
	 * @param oldSettingsEntity
	 * @param newFeedbackChannels
	 * @return Returns the difference (removed elements) from oldSettingsEntity.
	 */
	public static List<FeedbackChannelEmbeddable> getRemovedFeedbackChannels(FeedbackSettingsEntity oldSettingsEntity, List<FeedbackChannelEmbeddable> newFeedbackChannels) {
		// If affectedEntities in newFeedbackChannels isn't set (i.e. is null), just return an empty list.
		if (isNull(newFeedbackChannels)) {
			return emptyList();
		}
		return ofNullable(oldSettingsEntity.getFeedbackChannels()).orElse(emptyList()).stream()
			.filter(oldEntity -> !existsInList(oldEntity, newFeedbackChannels))
			.toList();
	}

	/**
	 * Returns all added FeedbackChannelEmbeddable elements from newFeedbackChannels, when comparing with
	 * oldSettingsEntity.getFeedbackChannels().
	 * 
	 * E.g.
	 * 
	 * oldSettingsEntity.getFeedbackChannels() contains: <ELEMENT-1>, <ELEMENT-2>, <ELEMENT-3>
	 * newFeedbackChannels contains: <ELEMENT-1>, <ELEMENT-4>
	 * 
	 * Result: This method will return [<ELEMENT-4>]
	 * 
	 * @param oldSettingsEntity
	 * @param newFeedbackChannels
	 * @return Returns the added elements from newFeedbackChannels.
	 */
	public static List<FeedbackChannelEmbeddable> getAddedFeedbackChannels(FeedbackSettingsEntity oldSettingsEntity, List<FeedbackChannelEmbeddable> newFeedbackChannels) {
		if (isNull(oldSettingsEntity) || isNull(oldSettingsEntity.getFeedbackChannels())) {
			return Optional.ofNullable(newFeedbackChannels).orElse(emptyList());
		}
		// If newFeedbackChannelEntities isn't set (i.e. is null), just return an empty list.
		if (isNull(newFeedbackChannels)) {
			return emptyList();
		}
		return newFeedbackChannels.stream()
			.filter(newEntity -> !existsInList(newEntity, oldSettingsEntity.getFeedbackChannels()))
			.toList();
	}

	private static boolean existsInList(FeedbackChannelEmbeddable objectToCheck, List<FeedbackChannelEmbeddable> list) {
		return ofNullable(list).orElse(emptyList()).stream()
			.anyMatch(entity -> 
					equalsIgnoreCase(entity.getDestination(), objectToCheck.getDestination()) &&
					entity.getContactMethod() == objectToCheck.getContactMethod() && 
					entity.isSendFeedback() == objectToCheck.isSendFeedback());
	}
}
