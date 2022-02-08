package se.sundsvall.feedbacksettings.api.model;

import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanConstructor;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanEquals;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanHashCode;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanToString;
import static com.google.code.beanmatchers.BeanMatchers.hasValidGettersAndSetters;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.jupiter.api.Test;

class MetaDataTest {
	private static final int COUNT = 101;
	private static final int LIMIT = 202;
	private static final int PAGE = 303;
	private static final int TOTAL_PAGES = 404;
	private static final long TOTAL_RECORDS = 505;
	
	@Test
	void testBean() {
		assertThat(MetaData.class, allOf(
			hasValidBeanConstructor(),
			hasValidGettersAndSetters(),
			hasValidBeanHashCode(),
			hasValidBeanEquals(),
			hasValidBeanToString()));
	}

	@Test
	void testCreatePattern() {
		MetaData meta = MetaData.create()
				.withCount(COUNT)
				.withLimit(LIMIT)
				.withPage(PAGE)
				.withTotalPages(TOTAL_PAGES)
				.withTotalRecords(TOTAL_RECORDS);

		assertThat(meta.getCount()).isEqualTo(COUNT);
		assertThat(meta.getLimit()).isEqualTo(LIMIT);
		assertThat(meta.getPage()).isEqualTo(PAGE);
		assertThat(meta.getTotalPages()).isEqualTo(TOTAL_PAGES);
		assertThat(meta.getTotalRecords()).isEqualTo(TOTAL_RECORDS);
	}

	@Test
	void testNoDirtOnCreatedBean() {
		assertThat(MetaData.create())
			.hasFieldOrPropertyWithValue("count", 0)
			.hasFieldOrPropertyWithValue("limit", 0)
			.hasFieldOrPropertyWithValue("page", 0)
			.hasFieldOrPropertyWithValue("totalRecords", 0L)
			.hasFieldOrPropertyWithValue("totalPages", 0);

		assertThat(new MetaData())
			.hasFieldOrPropertyWithValue("count", 0)
			.hasFieldOrPropertyWithValue("limit", 0)
			.hasFieldOrPropertyWithValue("page", 0)
			.hasFieldOrPropertyWithValue("totalRecords", 0L)
			.hasFieldOrPropertyWithValue("totalPages", 0);
	}
}
