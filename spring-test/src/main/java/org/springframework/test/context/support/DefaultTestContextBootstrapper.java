

package org.springframework.test.context.support;

import org.springframework.test.context.ContextLoader;
import org.springframework.test.context.TestContextBootstrapper;

/**
 * Default implementation of the {@link TestContextBootstrapper} SPI.
 * Uses {@link DelegatingSmartContextLoader} as the default {@link ContextLoader}.
 * @since 4.1
 */
public class DefaultTestContextBootstrapper extends AbstractTestContextBootstrapper {

	/**
	 * Returns {@link DelegatingSmartContextLoader}.
	 */
	@Override
	protected Class<? extends ContextLoader> getDefaultContextLoaderClass(Class<?> testClass) {
		return DelegatingSmartContextLoader.class;
	}
}
