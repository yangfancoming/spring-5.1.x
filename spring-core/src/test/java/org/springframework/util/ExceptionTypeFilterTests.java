

package org.springframework.util;

import org.junit.Test;

import static java.util.Arrays.*;
import static org.junit.Assert.*;

/**
 * @author Stephane Nicoll
 */
public class ExceptionTypeFilterTests {

	@Test
	public void subClassMatch() {
		ExceptionTypeFilter filter = new ExceptionTypeFilter(
				asList(RuntimeException.class), null, true);
		assertTrue(filter.match(RuntimeException.class));
		assertTrue(filter.match(IllegalStateException.class));
	}

}
