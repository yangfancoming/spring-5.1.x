

package org.springframework.web.context.support;

import org.junit.Test;

import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.AnnotationConfigUtils;
import org.springframework.mock.web.test.MockServletContext;
import org.springframework.tests.sample.beans.ITestBean;
import org.springframework.tests.sample.beans.TestBean;
import org.springframework.web.context.WebApplicationContext;

import static org.junit.Assert.*;


public class SpringBeanAutowiringSupportTests {

	@Test
	public void testProcessInjectionBasedOnServletContext() {
		StaticWebApplicationContext wac = new StaticWebApplicationContext();
		AnnotationConfigUtils.registerAnnotationConfigProcessors(wac);

		MutablePropertyValues pvs = new MutablePropertyValues();
		pvs.add("name", "tb");
		wac.registerSingleton("testBean", TestBean.class, pvs);

		MockServletContext sc = new MockServletContext();
		wac.setServletContext(sc);
		wac.refresh();
		sc.setAttribute(WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE, wac);

		InjectionTarget target = new InjectionTarget();
		SpringBeanAutowiringSupport.processInjectionBasedOnServletContext(target, sc);
		assertTrue(target.testBean instanceof TestBean);
		assertEquals("tb", target.name);
	}


	public static class InjectionTarget {

		@Autowired
		public ITestBean testBean;

		@Value("#{testBean.name}")
		public String name;
	}

}
