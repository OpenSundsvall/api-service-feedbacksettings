package se.sundsvall.feedbacksettings.api.validation.impl;

import static java.util.Optional.ofNullable;

import java.util.Collection;
import java.util.Collections;
import java.util.Objects;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import io.smallrye.mutiny.tuples.Tuple2;
import se.sundsvall.feedbacksettings.api.model.RequestedFeedbackChannel;
import se.sundsvall.feedbacksettings.api.validation.UniqueFeedbackChannels;

public class UniqueFeedbackChannelsConstraintValidator implements ConstraintValidator<UniqueFeedbackChannels, Collection<RequestedFeedbackChannel>> {
	@Override
	public boolean isValid(final Collection<RequestedFeedbackChannel> value, final ConstraintValidatorContext context) {
		Collection<RequestedFeedbackChannel> collection = ofNullable(value).orElse(Collections.emptyList());
		
		long distictElements = collection.stream()
				.filter(Objects::nonNull)
				.map(channel -> Tuple2.of(channel.getContactMethod(), channel.getDestination()))
				.distinct()
				.count();

		return distictElements == collection.size();
	}
}
