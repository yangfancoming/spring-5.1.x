

package org.springframework.format.number;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

/**
 * A formatter for number values in percent style.
 *
 * <p>Delegates to {@link java.text.NumberFormat#getPercentInstance(Locale)}.
 * Configures BigDecimal parsing so there is no loss in precision.
 * The {@link #parse(String, Locale)} routine always returns a BigDecimal.
 *
 * @author Keith Donald
 * @author Juergen Hoeller
 * @since 4.2
 * @see #setLenient
 */
public class PercentStyleFormatter extends AbstractNumberFormatter {

	@Override
	protected NumberFormat getNumberFormat(Locale locale) {
		NumberFormat format = NumberFormat.getPercentInstance(locale);
		if (format instanceof DecimalFormat) {
			((DecimalFormat) format).setParseBigDecimal(true);
		}
		return format;
	}

}
