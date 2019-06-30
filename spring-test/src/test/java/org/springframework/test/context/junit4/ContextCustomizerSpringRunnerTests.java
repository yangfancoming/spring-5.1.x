

package org.springframework.test.context.junit4;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.BootstrapWith;
import org.springframework.test.context.ContextCustomizer;
import org.springframework.test.context.ContextCustomizerFactory;
import org.springframework.test.context.junit4.ContextCustomizerSpringRunnerTests.CustomTestContextBootstrapper;
import org.springframework.test.context.support.DefaultTestContextBootstrapper;

import static java.util.Collections.*;
import static org.junit.Assert.*;

/**
 * JUnit 4 based integration test which verifies support of
 * {@link ContextCustomizerFactory} and {@link ContextCustomizer}.
 *
 * @author Sam Brannen
 * @author Phillip Webb
 * @since 4.3
 */
@RunWith(SpringRunner.class)
@BootstrapWith(CustomTestContextBootstrapper.class)
public class ContextCustomizerSpringRunnerTests {

	@Autowired String foo;


	@Test
	public void injectedBean() {
		assertEquals("foo", foo);
	}


	static class CustomTestContextBootstrapper extends DefaultTestContextBootstrapper {

		@Override
		protected List<ContextCustomizerFactory> getContextCustomizerFactories() {
			return singletonList(
				(ContextCustomizerFactory) (testClass, configAttributes) ->
					(ContextCustomizer) (context, mergedConfig) -> context.getBeanFactory().registerSingleton("foo", "foo")
			);
		}
	}

}
