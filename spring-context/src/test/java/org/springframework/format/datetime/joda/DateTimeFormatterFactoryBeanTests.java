

package org.springframework.format.datetime.joda;

import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.junit.Test;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

/**
 * @author Phillip Webb
 * @author Sam Brannen
 */
public class DateTimeFormatterFactoryBeanTests {

	private final DateTimeFormatterFactoryBean factory = new DateTimeFormatterFactoryBean();


	@Test
	public void isSingleton() {
		assertThat(factory.isSingleton(), is(true));
	}

	@Test
	@SuppressWarnings("rawtypes")
	public void getObjectType() {
		assertThat(factory.getObjectType(), is(equalTo((Class) DateTimeFormatter.class)));
	}

	@Test
	public void getObject() {
		factory.afterPropertiesSet();
		assertThat(factory.getObject(), is(equalTo(DateTimeFormat.mediumDateTime())));
	}

	@Test
	public void getObjectIsAlwaysSingleton() {
		factory.afterPropertiesSet();
		DateTimeFormatter formatter = factory.getObject();
		assertThat(formatter, is(equalTo(DateTimeFormat.mediumDateTime())));
		factory.setStyle("LL");
		assertThat(factory.getObject(), is(sameInstance(formatter)));
	}

}
