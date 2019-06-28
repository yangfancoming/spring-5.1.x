

package org.springframework.format.datetime.standard;

import java.time.format.DateTimeFormatter;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.lang.Nullable;

/**
 * {@link FactoryBean} that creates a JSR-310 {@link java.time.format.DateTimeFormatter}.
 * See the {@link DateTimeFormatterFactory base class} for configuration details.
 *
 * @author Juergen Hoeller
 * @since 4.0
 * @see #setPattern
 * @see #setIso
 * @see #setDateStyle
 * @see #setTimeStyle
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
