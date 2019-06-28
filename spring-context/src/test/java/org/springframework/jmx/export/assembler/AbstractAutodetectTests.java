

package org.springframework.jmx.export.assembler;

import org.junit.Test;

import org.springframework.jmx.JmxTestBean;

import static org.junit.Assert.*;

/**
 * @author Rob Harrop
 */
public abstract class AbstractAutodetectTests {

	@Test
	public void autodetect() throws Exception {
		JmxTestBean bean = new JmxTestBean();

		AutodetectCapableMBeanInfoAssembler assembler = getAssembler();
		assertTrue("The bean should be included", assembler.includeBean(bean.getClass(), "testBean"));
	}

	protected abstract AutodetectCapableMBeanInfoAssembler getAssembler();

}
