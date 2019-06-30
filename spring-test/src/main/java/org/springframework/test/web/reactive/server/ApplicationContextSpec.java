

package org.springframework.test.web.reactive.server;

import org.springframework.context.ApplicationContext;
import org.springframework.util.Assert;
import org.springframework.web.server.adapter.WebHttpHandlerBuilder;

/**
 * Spec for setting up server-less testing by detecting components in an
 * {@link ApplicationContext}.
 *
 * @author Rossen Stoyanchev
 * @since 5.0
 */
class ApplicationContextSpec extends AbstractMockServerSpec<ApplicationContextSpec> {

	private final ApplicationContext applicationContext;


	ApplicationContextSpec(ApplicationContext applicationContext) {
		Assert.notNull(applicationContext, "ApplicationContext is required");
		this.applicationContext = applicationContext;
	}


	@Override
	protected WebHttpHandlerBuilder initHttpHandlerBuilder() {
		return WebHttpHandlerBuilder.applicationContext(this.applicationContext);
	}

}
