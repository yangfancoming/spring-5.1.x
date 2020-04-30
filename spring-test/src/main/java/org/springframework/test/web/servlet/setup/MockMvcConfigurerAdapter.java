

package org.springframework.test.web.servlet.setup;

import org.springframework.lang.Nullable;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import org.springframework.web.context.WebApplicationContext;

/**
 * An empty method implementation of {@link MockMvcConfigurer}.
 *
 *
 * @since 4.1
 */
public abstract class MockMvcConfigurerAdapter implements MockMvcConfigurer {

	@Override
	public void afterConfigurerAdded(ConfigurableMockMvcBuilder<?> builder) {
	}

	@Override
	@Nullable
	public RequestPostProcessor beforeMockMvcCreated(ConfigurableMockMvcBuilder<?> builder, WebApplicationContext cxt) {
		return null;
	}

}
