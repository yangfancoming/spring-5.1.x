

package org.springframework.format.number;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.ParseException;
import java.util.Locale;

import org.junit.Test;

import static org.junit.Assert.*;


public class CurrencyStyleFormatterTests {

	private final CurrencyStyleFormatter formatter = new CurrencyStyleFormatter();


	@Test
	public void formatValue() {
		assertEquals("$23.00", formatter.print(new BigDecimal("23"), Locale.US));
	}

	@Test
	public void parseValue() throws ParseException {
		assertEquals(new BigDecimal("23.56"), formatter.parse("$23.56", Locale.US));
	}

	@Test(expected = ParseException.class)
	public void parseBogusValue() throws ParseException {
		formatter.parse("bogus", Locale.US);
	}

	@Test
	public void parseValueDefaultRoundDown() throws ParseException {
		this.formatter.setRoundingMode(RoundingMode.DOWN);
		assertEquals(new BigDecimal("23.56"), formatter.parse("$23.567", Locale.US));
	}

	@Test
	public void parseWholeValue() throws ParseException {
		assertEquals(new BigDecimal("23.00"), formatter.parse("$23", Locale.US));
	}

	@Test(expected = ParseException.class)
	public void parseValueNotLenientFailure() throws ParseException {
		formatter.parse("$23.56bogus", Locale.US);
	}

}
