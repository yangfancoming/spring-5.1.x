

package org.springframework.scripting.groovy;

import groovy.lang.GroovyClassLoader;

import java.lang.reflect.Method;

import org.junit.Test;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.context.support.StaticApplicationContext;
import org.springframework.util.ReflectionUtils;

import static org.junit.Assert.*;


public class GroovyClassLoadingTests {

	@Test
	@SuppressWarnings("resource")
	public void classLoading() throws Exception {
		StaticApplicationContext context = new StaticApplicationContext();

		GroovyClassLoader gcl = new GroovyClassLoader();
		Class<?> class1 = gcl.parseClass("class TestBean { def myMethod() { \"foo\" } }");
		Class<?> class2 = gcl.parseClass("class TestBean { def myMethod() { \"bar\" } }");

		context.registerBeanDefinition("testBean", new RootBeanDefinition(class1));
		Object testBean1 = context.getBean("testBean");
		Method method1 = class1.getDeclaredMethod("myMethod", new Class<?>[0]);
		Object result1 = ReflectionUtils.invokeMethod(method1, testBean1);
		assertEquals("foo", result1);

		context.removeBeanDefinition("testBean");
		context.registerBeanDefinition("testBean", new RootBeanDefinition(class2));
		Object testBean2 = context.getBean("testBean");
		Method method2 = class2.getDeclaredMethod("myMethod", new Class<?>[0]);
		Object result2 = ReflectionUtils.invokeMethod(method2, testBean2);
		assertEquals("bar", result2);
	}

}
