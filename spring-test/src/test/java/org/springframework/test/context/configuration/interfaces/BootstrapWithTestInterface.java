

package org.springframework.test.context.configuration.interfaces;

import java.util.List;

import org.springframework.test.context.BootstrapWith;
import org.springframework.test.context.ContextCustomizer;
import org.springframework.test.context.ContextCustomizerFactory;
import org.springframework.test.context.configuration.interfaces.BootstrapWithTestInterface.CustomTestContextBootstrapper;
import org.springframework.test.context.support.DefaultTestContextBootstrapper;

import static java.util.Collections.*;

/**
 * @author Sam Brannen
 * @author Phillip Webb
 * @since 4.3
 */
@BootstrapWith(CustomTestContextBootstrapper.class)
interface BootstrapWithTestInterface {

	static class CustomTestContextBootstrapper extends DefaultTestContextBootstrapper {

		@Override
		protected List<ContextCustomizerFactory> getContextCustomizerFactories() {
			return singletonList(
				(ContextCustomizerFactory) (testClass, configAttributes) -> (ContextCustomizer) (context,
						mergedConfig) -> context.getBeanFactory().registerSingleton("foo", "foo"));
		}
	}

}
