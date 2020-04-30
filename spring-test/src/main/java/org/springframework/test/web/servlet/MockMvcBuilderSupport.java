

package org.springframework.test.web.servlet;

import java.util.List;
import javax.servlet.Filter;
import javax.servlet.ServletException;

import org.springframework.core.NestedRuntimeException;
import org.springframework.lang.Nullable;
import org.springframework.mock.web.MockServletConfig;
import org.springframework.web.context.WebApplicationContext;

/**
 * Base class for MockMvc builder implementations, providing the capability to
 * create a {@link MockMvc} instance.
 *
 * {@link org.springframework.test.web.servlet.setup.DefaultMockMvcBuilder},
 * which derives from this class, provides a concrete {@code build} method,
 * and delegates to abstract methods to obtain a {@link WebApplicationContext}.
 *
 *
 * @author Rob Winch
 * @author Stephane Nicoll
 * @since 3.2
 */
public abstract class MockMvcBuilderSupport {

	protected final MockMvc createMockMvc(Filter[] filters, MockServletConfig servletConfig,
			WebApplicationContext webAppContext, @Nullable RequestBuilder defaultRequestBuilder,
			List<ResultMatcher> globalResultMatchers, List<ResultHandler> globalResultHandlers,
			@Nullable List<DispatcherServletCustomizer> dispatcherServletCustomizers) {

		TestDispatcherServlet dispatcherServlet = new TestDispatcherServlet(webAppContext);
		if (dispatcherServletCustomizers != null) {
			for (DispatcherServletCustomizer customizers : dispatcherServletCustomizers) {
				customizers.customize(dispatcherServlet);
			}
		}
		try {
			dispatcherServlet.init(servletConfig);
		}
		catch (ServletException ex) {
			// should never happen..
			throw new MockMvcBuildException("Failed to initialize TestDispatcherServlet", ex);
		}

		MockMvc mockMvc = new MockMvc(dispatcherServlet, filters);
		mockMvc.setDefaultRequest(defaultRequestBuilder);
		mockMvc.setGlobalResultMatchers(globalResultMatchers);
		mockMvc.setGlobalResultHandlers(globalResultHandlers);

		return mockMvc;
	}


	@SuppressWarnings("serial")
	private static class MockMvcBuildException extends NestedRuntimeException {

		public MockMvcBuildException(String msg, Throwable cause) {
			super(msg, cause);
		}
	}

}
