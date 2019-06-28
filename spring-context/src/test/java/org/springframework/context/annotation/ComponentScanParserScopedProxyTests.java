

package org.springframework.context.annotation;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.parsing.BeanDefinitionParsingException;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.tests.context.SimpleMapScope;
import org.springframework.util.SerializationTestUtils;

import example.scannable.FooService;
import example.scannable.ScopedProxyTestBean;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

/**
 * @author Mark Fisher
 * @author Juergen Hoeller
 * @author Sam Brannen
 */
public class ComponentScanParserScopedProxyTests {

	@Rule
	public final ExpectedException exception = ExpectedException.none();


	@Test
	public void testDefaultScopedProxy() {
		ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(
				"org/springframework/context/annotation/scopedProxyDefaultTests.xml");
		context.getBeanFactory().registerScope("myScope", new SimpleMapScope());

		ScopedProxyTestBean bean = (ScopedProxyTestBean) context.getBean("scopedProxyTestBean");
		// should not be a proxy
		assertFalse(AopUtils.isAopProxy(bean));
		context.close();
	}

	@Test
	public void testNoScopedProxy() {
		ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(
				"org/springframework/context/annotation/scopedProxyNoTests.xml");
		context.getBeanFactory().registerScope("myScope", new SimpleMapScope());

		ScopedProxyTestBean bean = (ScopedProxyTestBean) context.getBean("scopedProxyTestBean");
		// should not be a proxy
		assertFalse(AopUtils.isAopProxy(bean));
		context.close();
	}

	@Test
	public void testInterfacesScopedProxy() throws Exception {
		ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(
				"org/springframework/context/annotation/scopedProxyInterfacesTests.xml");
		context.getBeanFactory().registerScope("myScope", new SimpleMapScope());

		// should cast to the interface
		FooService bean = (FooService) context.getBean("scopedProxyTestBean");
		// should be dynamic proxy
		assertTrue(AopUtils.isJdkDynamicProxy(bean));
		// test serializability
		assertEquals("bar", bean.foo(1));
		FooService deserialized = (FooService) SerializationTestUtils.serializeAndDeserialize(bean);
		assertNotNull(deserialized);
		assertEquals("bar", deserialized.foo(1));
		context.close();
	}

	@Test
	public void testTargetClassScopedProxy() throws Exception {
		ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(
				"org/springframework/context/annotation/scopedProxyTargetClassTests.xml");
		context.getBeanFactory().registerScope("myScope", new SimpleMapScope());

		ScopedProxyTestBean bean = (ScopedProxyTestBean) context.getBean("scopedProxyTestBean");
		// should be a class-based proxy
		assertTrue(AopUtils.isCglibProxy(bean));
		// test serializability
		assertEquals("bar", bean.foo(1));
		ScopedProxyTestBean deserialized = (ScopedProxyTestBean) SerializationTestUtils.serializeAndDeserialize(bean);
		assertNotNull(deserialized);
		assertEquals("bar", deserialized.foo(1));
		context.close();
	}

	@Test
	@SuppressWarnings("resource")
	public void testInvalidConfigScopedProxy() throws Exception {
		exception.expect(BeanDefinitionParsingException.class);
		exception.expectMessage(containsString("Cannot define both 'scope-resolver' and 'scoped-proxy' on <component-scan> tag"));
		exception.expectMessage(containsString("Offending resource: class path resource [org/springframework/context/annotation/scopedProxyInvalidConfigTests.xml]"));

		new ClassPathXmlApplicationContext("org/springframework/context/annotation/scopedProxyInvalidConfigTests.xml");
	}

}
