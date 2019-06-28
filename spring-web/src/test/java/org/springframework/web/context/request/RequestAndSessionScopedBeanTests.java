

package org.springframework.web.context.request;

import javax.servlet.http.HttpServletRequest;

import org.junit.Test;

import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.mock.web.test.MockHttpServletRequest;
import org.springframework.tests.sample.beans.TestBean;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.StaticWebApplicationContext;

import static org.junit.Assert.*;

/**
 * @author Rod Johnson
 * @author Juergen Hoeller
 */
public class RequestAndSessionScopedBeanTests {

	@Test
	@SuppressWarnings("resource")
	public void testPutBeanInRequest() throws Exception {
		String targetBeanName = "target";

		StaticWebApplicationContext wac = new StaticWebApplicationContext();
		RootBeanDefinition bd = new RootBeanDefinition(TestBean.class);
		bd.setScope(WebApplicationContext.SCOPE_REQUEST);
		bd.getPropertyValues().add("name", "abc");
		wac.registerBeanDefinition(targetBeanName, bd);
		wac.refresh();

		HttpServletRequest request = new MockHttpServletRequest();
		RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
		TestBean target = (TestBean) wac.getBean(targetBeanName);
		assertEquals("abc", target.getName());
		assertSame(target, request.getAttribute(targetBeanName));

		TestBean target2 = (TestBean) wac.getBean(targetBeanName);
		assertEquals("abc", target2.getName());
		assertSame(target2, target);
		assertSame(target2, request.getAttribute(targetBeanName));

		request = new MockHttpServletRequest();
		RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
		TestBean target3 = (TestBean) wac.getBean(targetBeanName);
		assertEquals("abc", target3.getName());
		assertSame(target3, request.getAttribute(targetBeanName));
		assertNotSame(target3, target);

		RequestContextHolder.setRequestAttributes(null);
		try {
			wac.getBean(targetBeanName);
			fail("Should have thrown BeanCreationException");
		}
		catch (BeanCreationException ex) {
			// expected
		}
	}

	@Test
	@SuppressWarnings("resource")
	public void testPutBeanInSession() throws Exception {
		String targetBeanName = "target";
		HttpServletRequest request = new MockHttpServletRequest();
		RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

		StaticWebApplicationContext wac = new StaticWebApplicationContext();
		RootBeanDefinition bd = new RootBeanDefinition(TestBean.class);
		bd.setScope(WebApplicationContext.SCOPE_SESSION);
		bd.getPropertyValues().add("name", "abc");
		wac.registerBeanDefinition(targetBeanName, bd);
		wac.refresh();

		TestBean target = (TestBean) wac.getBean(targetBeanName);
		assertEquals("abc", target.getName());
		assertSame(target, request.getSession().getAttribute(targetBeanName));

		RequestContextHolder.setRequestAttributes(null);
		try {
			wac.getBean(targetBeanName);
			fail("Should have thrown BeanCreationException");
		}
		catch (BeanCreationException ex) {
			// expected
		}


	}

}
