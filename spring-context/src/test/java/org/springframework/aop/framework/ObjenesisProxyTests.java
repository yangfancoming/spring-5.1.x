

package org.springframework.aop.framework;

import org.junit.Test;

import org.springframework.aop.interceptor.DebugInterceptor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

/**
 * Integration test for Objenesis proxy creation.
 *
 * @author Oliver Gierke
 */
public class ObjenesisProxyTests {

	@Test
	public void appliesAspectToClassWithComplexConstructor() {
		@SuppressWarnings("resource")
		ApplicationContext context = new ClassPathXmlApplicationContext("ObjenesisProxyTests-context.xml", getClass());

		ClassWithComplexConstructor bean = context.getBean(ClassWithComplexConstructor.class);
		bean.method();

		DebugInterceptor interceptor = context.getBean(DebugInterceptor.class);
		assertThat(interceptor.getCount(), is(1L));
		assertThat(bean.getDependency().getValue(), is(1));
	}

}
