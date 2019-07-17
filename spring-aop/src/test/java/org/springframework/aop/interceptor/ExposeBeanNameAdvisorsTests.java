

package org.springframework.aop.interceptor;

import org.junit.Test;

import org.springframework.aop.framework.ProxyFactory;
import org.springframework.beans.factory.NamedBean;
import org.springframework.tests.sample.beans.ITestBean;
import org.springframework.tests.sample.beans.TestBean;

import static org.junit.Assert.*;

/**
 * @author Rod Johnson

 */
public class ExposeBeanNameAdvisorsTests {

	private class RequiresBeanNameBoundTestBean extends TestBean {
		private final String beanName;

		public RequiresBeanNameBoundTestBean(String beanName) {
			this.beanName = beanName;
		}

		@Override
		public int getAge() {
			assertEquals(beanName, ExposeBeanNameAdvisors.getBeanName());
			return super.getAge();
		}
	}

	@Test
	public void testNoIntroduction() {
		String beanName = "foo";
		TestBean target = new RequiresBeanNameBoundTestBean(beanName);
		ProxyFactory pf = new ProxyFactory(target);
		pf.addAdvisor(ExposeInvocationInterceptor.ADVISOR);
		pf.addAdvisor(ExposeBeanNameAdvisors.createAdvisorWithoutIntroduction(beanName));
		ITestBean proxy = (ITestBean) pf.getProxy();

		assertFalse("No introduction", proxy instanceof NamedBean);
		// Requires binding
		proxy.getAge();
	}

	@Test
	public void testWithIntroduction() {
		String beanName = "foo";
		TestBean target = new RequiresBeanNameBoundTestBean(beanName);
		ProxyFactory pf = new ProxyFactory(target);
		pf.addAdvisor(ExposeInvocationInterceptor.ADVISOR);
		pf.addAdvisor(ExposeBeanNameAdvisors.createAdvisorIntroducingNamedBean(beanName));
		ITestBean proxy = (ITestBean) pf.getProxy();

		assertTrue("Introduction was made", proxy instanceof NamedBean);
		// Requires binding
		proxy.getAge();

		NamedBean nb = (NamedBean) proxy;
		assertEquals("Name returned correctly", beanName, nb.getBeanName());
	}

}
