

package org.springframework.web.servlet.tags;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * Unit tests for {@link Param}.
 *
 * @author Scott Andrews
 */
public class ParamTests {

	private final Param param = new Param();

	@Test
	public void name() {
		param.setName("name");
		assertEquals("name", param.getName());
	}

	@Test
	public void value() {
		param.setValue("value");
		assertEquals("value", param.getValue());
	}

	@Test
	public void nullDefaults() {
		assertNull(param.getName());
		assertNull(param.getValue());
	}

}
