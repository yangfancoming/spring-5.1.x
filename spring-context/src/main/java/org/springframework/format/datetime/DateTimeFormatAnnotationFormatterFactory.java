

package org.springframework.format.datetime;

import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.springframework.context.support.EmbeddedValueResolutionSupport;
import org.springframework.format.AnnotationFormatterFactory;
import org.springframework.format.Formatter;
import org.springframework.format.Parser;
import org.springframework.format.Printer;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.util.StringUtils;

/**
 * Formats fields annotated with the {@link DateTimeFormat} annotation using a {@link DateFormatter}.
 *
 * @author Phillip Webb
 * @since 3.2
 * @see org.springframework.format.datetime.joda.JodaDateTimeFormatAnnotationFormatterFactory
 */
public class DateTimeFormatAnnotationFormatterFactory  extends EmbeddedValueResolutionSupport
		implements AnnotationFormatterFactory<DateTimeFormat> {

	private static final Set<Class<?>> FIELD_TYPES;

	static {
		Set<Class<?>> fieldTypes = new HashSet<>(4);
		fieldTypes.add(Date.class);
		fieldTypes.add(Calendar.class);
		fieldTypes.add(Long.class);
		FIELD_TYPES = Collections.unmodifiableSet(fieldTypes);
	}


	@Override
	public Set<Class<?>> getFieldTypes() {
		return FIELD_TYPES;
	}

	@Override
	public Printer<?> getPrinter(DateTimeFormat annotation, Class<?> fieldType) {
		return getFormatter(annotation, fieldType);
	}

	@Override
	public Parser<?> getParser(DateTimeFormat annotation, Class<?> fieldType) {
		return getFormatter(annotation, fieldType);
	}

	protected Formatter<Date> getFormatter(DateTimeFormat annotation, Class<?> fieldType) {
		DateFormatter formatter = new DateFormatter();
		String style = resolveEmbeddedValue(annotation.style());
		if (StringUtils.hasLength(style)) {
			formatter.setStylePattern(style);
		}
		formatter.setIso(annotation.iso());
		String pattern = resolveEmbeddedValue(annotation.pattern());
		if (StringUtils.hasLength(pattern)) {
			formatter.setPattern(pattern);
		}
		return formatter;
	}

}
