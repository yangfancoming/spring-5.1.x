

package org.springframework.format.number;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.Locale;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author Keith Donald
 */
public class NumberStyleFormatterTests {

	private final NumberStyleFormatter formatter = new NumberStyleFormatter();


	@Test
	public void formatValue() {
		assertEquals("23.56", formatter.print(new BigDecimal("23.56"), Locale.US));
	}

	@Test
	public void parseValue() throws ParseException {
		assertEquals(new BigDecimal("23.56"), formatter.parse("23.56", Locale.US));
	}

	@Test(expected = ParseException.class)
	public void parseBogusValue() throws ParseException {
		formatter.parse("bogus", Locale.US);
	}

	@Test(expected = ParseException.class)
	public void parsePercentValueNotLenientFailure() throws ParseException {
		formatter.parse("23.56bogus", Locale.US);
	}

}
