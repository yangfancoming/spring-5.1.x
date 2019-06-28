

package org.springframework.format.datetime.joda;

import org.joda.time.format.DateTimeFormatter;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.lang.Nullable;

/**
 * {@link FactoryBean} that creates a Joda-Time {@link DateTimeFormatter}.
 * See the {@link DateTimeFormatterFactory base class} for configuration details.
 *
 * @author Phillip Webb
 * @author Sam Brannen
 * @since 3.2
 * @see #setPattern
 * @see #setIso
 * @see #setStyle
 * @see DateTimeFormatterFactory
 */
public class DateTimeFormatterFactoryBean extends DateTimeFormatterFactory
		implements FactoryBean<DateTimeFormatter>, InitializingBean {

	@Nullable
	private DateTimeFormatter dateTimeFormatter;


	@Override
	public void afterPropertiesSet() {
		this.dateTimeFormatter = createDateTimeFormatter();
	}

	@Override
	@Nullable
	public DateTimeFormatter getObject() {
		return this.dateTimeFormatter;
	}

	@Override
	public Class<?> getObjectType() {
		return DateTimeFormatter.class;
	}

	@Override
	public boolean isSingleton() {
		return true;
	}

}
