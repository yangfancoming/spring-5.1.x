

package org.springframework.beans.factory.wiring;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Unit tests for the ClassNameBeanWiringInfoResolver class.
 *
 * @author Rick Evans
 */
public class ClassNameBeanWiringInfoResolverTests {

	@Test(expected = IllegalArgumentException.class)
	public void resolveWiringInfoWithNullBeanInstance() throws Exception {
		new ClassNameBeanWiringInfoResolver().resolveWiringInfo(null);
	}

	@Test
	public void resolveWiringInfo() {
		ClassNameBeanWiringInfoResolver resolver = new ClassNameBeanWiringInfoResolver();
		Long beanInstance = new Long(1);
		BeanWiringInfo info = resolver.resolveWiringInfo(beanInstance);
		assertNotNull(info);
		assertEquals("Not resolving bean name to the class name of the supplied bean instance as per class contract.",
				beanInstance.getClass().getName(), info.getBeanName());
	}

}
