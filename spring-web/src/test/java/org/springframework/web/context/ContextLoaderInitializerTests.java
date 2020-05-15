

package org.springframework.web.context;
import java.util.EventListener;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletException;
import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.test.MockServletContext;
import org.springframework.web.context.support.StaticWebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import static org.junit.Assert.*;

/**
 * Test case for {@link AbstractContextLoaderInitializer}.
 * 0 = {Class@2936} "class org.springframework.web.server.adapter.AbstractReactiveWebInitializer"
 * 1 = {Class@2937} "class org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer"
 * 2 = {Class@2938} "class org.springframework.web.context.AbstractContextLoaderInitializer"
 * 3 = {Class@2939} "class org.springframework.web.servlet.support.AbstractDispatcherServletInitializer"
 */
public class ContextLoaderInitializerTests {

	private static final String BEAN_NAME = "myBean";

	private AbstractContextLoaderInitializer initializer;

	private MockServletContext servletContext;

	private EventListener eventListener;

	@Before
	public void setUp() throws Exception {
		servletContext = new MyMockServletContext();
		initializer = new MyContextLoaderInitializer();
		eventListener = null;
	}

	@Test
	public void register() throws ServletException {
		initializer.onStartup(servletContext);
		assertTrue(eventListener instanceof ContextLoaderListener);
		ContextLoaderListener cll = (ContextLoaderListener) eventListener;
		cll.contextInitialized(new ServletContextEvent(servletContext));

		WebApplicationContext applicationContext = WebApplicationContextUtils.getRequiredWebApplicationContext(servletContext);
		assertTrue(applicationContext.containsBean(BEAN_NAME));
		assertTrue(applicationContext.getBean(BEAN_NAME) instanceof MyBean);
	}

	private class MyMockServletContext extends MockServletContext {
		@Override
		public <T extends EventListener> void addListener(T listener) {
			eventListener = listener;
		}
	}

	private static class MyContextLoaderInitializer extends AbstractContextLoaderInitializer {
		@Override
		protected WebApplicationContext createRootApplicationContext() {
			StaticWebApplicationContext rootContext = new StaticWebApplicationContext();
			rootContext.registerSingleton(BEAN_NAME, MyBean.class);
			return rootContext;
		}
	}

	private static class MyBean {}
}
