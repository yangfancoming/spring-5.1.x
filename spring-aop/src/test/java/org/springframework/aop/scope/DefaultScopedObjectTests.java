

package org.springframework.aop.scope;

import org.junit.Test;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;

import static org.mockito.BDDMockito.*;

/**
 * Unit tests for the {@link DefaultScopedObject} class.
 */
public class DefaultScopedObjectTests {

	private static final String GOOD_BEAN_NAME = "foo";


	@Test(expected = IllegalArgumentException.class)
	public void testCtorWithNullBeanFactory() throws Exception {
		new DefaultScopedObject(null, GOOD_BEAN_NAME);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testCtorWithNullTargetBeanName() throws Exception {
		testBadTargetBeanName(null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testCtorWithEmptyTargetBeanName() throws Exception {
		testBadTargetBeanName("");
	}

	@Test(expected = IllegalArgumentException.class)
	public void testCtorWithJustWhitespacedTargetBeanName() throws Exception {
		testBadTargetBeanName("   ");
	}

	private static void testBadTargetBeanName(final String badTargetBeanName) {
		ConfigurableBeanFactory factory = mock(ConfigurableBeanFactory.class);
		new DefaultScopedObject(factory, badTargetBeanName);
	}

}
