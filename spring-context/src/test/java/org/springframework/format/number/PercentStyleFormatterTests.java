

package org.springframework.format.number;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.Locale;

import org.junit.Test;

import static org.junit.Assert.*;


public class PercentStyleFormatterTests {

	private final PercentStyleFormatter formatter = new PercentStyleFormatter();


	@Test
	public void formatValue() {
		assertEquals("23%", formatter.print(new BigDecimal(".23"), Locale.US));
	}

	@Test
	public void parseValue() throws ParseException {
		assertEquals(new BigDecimal(".2356"), formatter.parse("23.56%", Locale.US));
	}

	@Test(expected = ParseException.class)
	public void parseBogusValue() throws ParseException {
		formatter.parse("bogus", Locale.US);
	}

	@Test(expected = ParseException.class)
	public void parsePercentValueNotLenientFailure() throws ParseException {
		formatter.parse("23.56%bogus", Locale.US);
	}

}
