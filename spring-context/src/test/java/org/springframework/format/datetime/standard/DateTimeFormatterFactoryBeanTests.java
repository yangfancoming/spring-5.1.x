

package org.springframework.format.datetime.standard;

import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;

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
	public void getObjectType() {
		assertThat(factory.getObjectType(), is(equalTo(DateTimeFormatter.class)));
	}

	@Test
	public void getObject() {
		factory.afterPropertiesSet();
		assertThat(factory.getObject().toString(), is(equalTo(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM).toString())));
	}

	@Test
	public void getObjectIsAlwaysSingleton() {
		factory.afterPropertiesSet();
		DateTimeFormatter formatter = factory.getObject();
		assertThat(formatter.toString(), is(equalTo(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM).toString())));
		factory.setStylePattern("LL");
		assertThat(factory.getObject(), is(sameInstance(formatter)));
	}

}
