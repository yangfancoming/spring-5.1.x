

package org.springframework.web.context.request;

import javax.servlet.ServletContextEvent;

import org.junit.Test;

import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.mock.web.test.MockHttpServletRequest;
import org.springframework.mock.web.test.MockServletContext;
import org.springframework.tests.sample.beans.DerivedTestBean;
import org.springframework.web.context.ContextCleanupListener;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.GenericWebApplicationContext;

import static org.junit.Assert.*;

/**
 * @author Juergen Hoeller
 */
public class WebApplicationContextScopeTests {

	private static final String NAME = "scoped";


	private WebApplicationContext initApplicationContext(String scope) {
		MockServletContext sc = new MockServletContext();
		GenericWebApplicationContext ac = new GenericWebApplicationContext(sc);
		GenericBeanDefinition bd = new GenericBeanDefinition();
		bd.setBeanClass(DerivedTestBean.class);
		bd.setScope(scope);
		ac.registerBeanDefinition(NAME, bd);
		ac.refresh();
		return ac;
	}

	@Test
	public void testRequestScope() {
		WebApplicationContext ac = initApplicationContext(WebApplicationContext.SCOPE_REQUEST);
		MockHttpServletRequest request = new MockHttpServletRequest();
		ServletRequestAttributes requestAttributes = new ServletRequestAttributes(request);
		RequestContextHolder.setRequestAttributes(requestAttributes);
		try {
			assertNull(request.getAttribute(NAME));
			DerivedTestBean bean = ac.getBean(NAME, DerivedTestBean.class);
			assertSame(bean, request.getAttribute(NAME));
			assertSame(bean, ac.getBean(NAME));
			requestAttributes.requestCompleted();
			assertTrue(bean.wasDestroyed());
		}
		finally {
			RequestContextHolder.setRequestAttributes(null);
		}
	}

	@Test
	public void testSessionScope() {
		WebApplicationContext ac = initApplicationContext(WebApplicationContext.SCOPE_SESSION);
		MockHttpServletRequest request = new MockHttpServletRequest();
		ServletRequestAttributes requestAttributes = new ServletRequestAttributes(request);
		RequestContextHolder.setRequestAttributes(requestAttributes);
		try {
			assertNull(request.getSession().getAttribute(NAME));
			DerivedTestBean bean = ac.getBean(NAME, DerivedTestBean.class);
			assertSame(bean, request.getSession().getAttribute(NAME));
			assertSame(bean, ac.getBean(NAME));
			request.getSession().invalidate();
			assertTrue(bean.wasDestroyed());
		}
		finally {
			RequestContextHolder.setRequestAttributes(null);
		}
	}

	@Test
	public void testApplicationScope() {
		WebApplicationContext ac = initApplicationContext(WebApplicationContext.SCOPE_APPLICATION);
		assertNull(ac.getServletContext().getAttribute(NAME));
		DerivedTestBean bean = ac.getBean(NAME, DerivedTestBean.class);
		assertSame(bean, ac.getServletContext().getAttribute(NAME));
		assertSame(bean, ac.getBean(NAME));
		new ContextCleanupListener().contextDestroyed(new ServletContextEvent(ac.getServletContext()));
		assertTrue(bean.wasDestroyed());
	}

}
