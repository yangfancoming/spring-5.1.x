

package org.springframework.format.number;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

import org.springframework.lang.Nullable;

/**
 * A general-purpose number formatter using NumberFormat's number style.
 *
 * <p>Delegates to {@link java.text.NumberFormat#getInstance(Locale)}.
 * Configures BigDecimal parsing so there is no loss in precision.
 * Allows configuration over the decimal number pattern.
 * The {@link #parse(String, Locale)} routine always returns a BigDecimal.
 *
 * @author Keith Donald
 * @author Juergen Hoeller
 * @since 4.2
 * @see #setPattern
 * @see #setLenient
 */
public class NumberStyleFormatter extends AbstractNumberFormatter {

	@Nullable
	private String pattern;


	/**
	 * Create a new NumberStyleFormatter without a pattern.
	 */
	public NumberStyleFormatter() {
	}

	/**
	 * Create a new NumberStyleFormatter with the specified pattern.
	 * @param pattern the format pattern
	 * @see #setPattern
	 */
	public NumberStyleFormatter(String pattern) {
		this.pattern = pattern;
	}


	/**
	 * Specify the pattern to use to format number values.
	 * If not specified, the default DecimalFormat pattern is used.
	 * @see java.text.DecimalFormat#applyPattern(String)
	 */
	public void setPattern(String pattern) {
		this.pattern = pattern;
	}


	@Override
	public NumberFormat getNumberFormat(Locale locale) {
		NumberFormat format = NumberFormat.getInstance(locale);
		if (!(format instanceof DecimalFormat)) {
			if (this.pattern != null) {
				throw new IllegalStateException("Cannot support pattern for non-DecimalFormat: " + format);
			}
			return format;
		}
		DecimalFormat decimalFormat = (DecimalFormat) format;
		decimalFormat.setParseBigDecimal(true);
		if (this.pattern != null) {
			decimalFormat.applyPattern(this.pattern);
		}
		return decimalFormat;
	}

}
