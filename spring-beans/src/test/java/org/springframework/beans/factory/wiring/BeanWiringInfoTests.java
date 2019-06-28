

package org.springframework.beans.factory.wiring;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Unit tests for the BeanWiringInfo class.
 *
 * @author Rick Evans
 * @author Sam Brannen
 */
public class BeanWiringInfoTests {

	@Test(expected = IllegalArgumentException.class)
	public void ctorWithNullBeanName() throws Exception {
		new BeanWiringInfo(null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void ctorWithWhitespacedBeanName() throws Exception {
		new BeanWiringInfo("   \t");
	}

	@Test(expected = IllegalArgumentException.class)
	public void ctorWithEmptyBeanName() throws Exception {
		new BeanWiringInfo("");
	}

	@Test(expected = IllegalArgumentException.class)
	public void ctorWithNegativeIllegalAutowiringValue() throws Exception {
		new BeanWiringInfo(-1, true);
	}

	@Test(expected = IllegalArgumentException.class)
	public void ctorWithPositiveOutOfRangeAutowiringValue() throws Exception {
		new BeanWiringInfo(123871, true);
	}

	@Test
	public void usingAutowireCtorIndicatesAutowiring() throws Exception {
		BeanWiringInfo info = new BeanWiringInfo(BeanWiringInfo.AUTOWIRE_BY_NAME, true);
		assertTrue(info.indicatesAutowiring());
	}

	@Test
	public void usingBeanNameCtorDoesNotIndicateAutowiring() throws Exception {
		BeanWiringInfo info = new BeanWiringInfo("fooService");
		assertFalse(info.indicatesAutowiring());
	}

	@Test
	public void noDependencyCheckValueIsPreserved() throws Exception {
		BeanWiringInfo info = new BeanWiringInfo(BeanWiringInfo.AUTOWIRE_BY_NAME, true);
		assertTrue(info.getDependencyCheck());
	}

	@Test
	public void dependencyCheckValueIsPreserved() throws Exception {
		BeanWiringInfo info = new BeanWiringInfo(BeanWiringInfo.AUTOWIRE_BY_TYPE, false);
		assertFalse(info.getDependencyCheck());
	}

}
