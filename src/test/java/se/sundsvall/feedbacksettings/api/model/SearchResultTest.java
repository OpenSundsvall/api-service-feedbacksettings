package se.sundsvall.feedbacksettings.api.model;

import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanConstructor;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanEquals;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanHashCode;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanToString;
import static com.google.code.beanmatchers.BeanMatchers.hasValidGettersAndSetters;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.List;

import org.junit.jupiter.api.Test;

class SearchResultTest {
	private static final List<FeedbackSettings> FEEDBACK_SETTINGS = List.of(FeedbackSettings.create());
	private static final MetaData META_DATA = MetaData.create();
	
	@Test
	void testBean() {
		assertThat(SearchResult.class, allOf(
			hasValidBeanConstructor(),
			hasValidGettersAndSetters(),
			hasValidBeanHashCode(),
			hasValidBeanEquals(),
			hasValidBeanToString()));
	}

	@Test
	void testCreatePattern() {
		SearchResult bean = SearchResult.create()
				.withFeedbackSettings(FEEDBACK_SETTINGS)
				.withMetaData(META_DATA);
		
		assertThat(bean.getFeedbackSettings()).isEqualTo(FEEDBACK_SETTINGS);
		assertThat(bean.getMetaData()).isEqualTo(META_DATA);
	}

	@Test
	void testNoDirtOnCreatedBean() {
		assertThat(SearchResult.create())
				.hasAllNullFieldsOrProperties();

		assertThat(new SearchResult())
				.hasAllNullFieldsOrProperties();
	}
}
