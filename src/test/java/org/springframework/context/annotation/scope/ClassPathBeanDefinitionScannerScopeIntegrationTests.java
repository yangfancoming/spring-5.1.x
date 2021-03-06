

package org.springframework.context.annotation.scope;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import org.springframework.aop.support.AopUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;
import org.springframework.web.context.annotation.SessionScope;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.context.support.GenericWebApplicationContext;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.springframework.context.annotation.ScopedProxyMode.DEFAULT;
import static org.springframework.context.annotation.ScopedProxyMode.INTERFACES;
import static org.springframework.context.annotation.ScopedProxyMode.NO;
import static org.springframework.context.annotation.ScopedProxyMode.TARGET_CLASS;


public class ClassPathBeanDefinitionScannerScopeIntegrationTests {

	private static final String DEFAULT_NAME = "default";
	private static final String MODIFIED_NAME = "modified";

	private ServletRequestAttributes oldRequestAttributes = new ServletRequestAttributes(new MockHttpServletRequest());
	private ServletRequestAttributes newRequestAttributes = new ServletRequestAttributes(new MockHttpServletRequest());

	private ServletRequestAttributes oldRequestAttributesWithSession;
	private ServletRequestAttributes newRequestAttributesWithSession;


	@Before
	public void setUp() {
		MockHttpServletRequest oldRequestWithSession = new MockHttpServletRequest();
		oldRequestWithSession.setSession(new MockHttpSession());
		this.oldRequestAttributesWithSession = new ServletRequestAttributes(oldRequestWithSession);

		MockHttpServletRequest newRequestWithSession = new MockHttpServletRequest();
		newRequestWithSession.setSession(new MockHttpSession());
		this.newRequestAttributesWithSession = new ServletRequestAttributes(newRequestWithSession);
	}

	@After
	public void tearDown() {
		RequestContextHolder.resetRequestAttributes();
	}


	@Test
	public void singletonScopeWithNoProxy() {
		RequestContextHolder.setRequestAttributes(oldRequestAttributes);
		ApplicationContext context = createContext(NO);
		ScopedTestBean bean = (ScopedTestBean) context.getBean("singleton");

		// should not be a proxy
		assertFalse(AopUtils.isAopProxy(bean));

		assertEquals(DEFAULT_NAME, bean.getName());
		bean.setName(MODIFIED_NAME);

		RequestContextHolder.setRequestAttributes(newRequestAttributes);
		// not a proxy so this should not have changed
		assertEquals(MODIFIED_NAME, bean.getName());

		// singleton bean, so name should be modified even after lookup
		ScopedTestBean bean2 = (ScopedTestBean) context.getBean("singleton");
		assertEquals(MODIFIED_NAME, bean2.getName());
	}

	@Test
	public void singletonScopeIgnoresProxyInterfaces() {
		RequestContextHolder.setRequestAttributes(oldRequestAttributes);
		ApplicationContext context = createContext(INTERFACES);
		ScopedTestBean bean = (ScopedTestBean) context.getBean("singleton");

		// should not be a proxy
		assertFalse(AopUtils.isAopProxy(bean));

		assertEquals(DEFAULT_NAME, bean.getName());
		bean.setName(MODIFIED_NAME);

		RequestContextHolder.setRequestAttributes(newRequestAttributes);
		// not a proxy so this should not have changed
		assertEquals(MODIFIED_NAME, bean.getName());

		// singleton bean, so name should be modified even after lookup
		ScopedTestBean bean2 = (ScopedTestBean) context.getBean("singleton");
		assertEquals(MODIFIED_NAME, bean2.getName());
	}

	@Test
	public void singletonScopeIgnoresProxyTargetClass() {
		RequestContextHolder.setRequestAttributes(oldRequestAttributes);
		ApplicationContext context = createContext(TARGET_CLASS);
		ScopedTestBean bean = (ScopedTestBean) context.getBean("singleton");

		// should not be a proxy
		assertFalse(AopUtils.isAopProxy(bean));

		assertEquals(DEFAULT_NAME, bean.getName());
		bean.setName(MODIFIED_NAME);

		RequestContextHolder.setRequestAttributes(newRequestAttributes);
		// not a proxy so this should not have changed
		assertEquals(MODIFIED_NAME, bean.getName());

		// singleton bean, so name should be modified even after lookup
		ScopedTestBean bean2 = (ScopedTestBean) context.getBean("singleton");
		assertEquals(MODIFIED_NAME, bean2.getName());
	}

	@Test
	public void requestScopeWithNoProxy() {
		RequestContextHolder.setRequestAttributes(oldRequestAttributes);
		ApplicationContext context = createContext(NO);
		ScopedTestBean bean = (ScopedTestBean) context.getBean("request");

		// should not be a proxy
		assertFalse(AopUtils.isAopProxy(bean));

		assertEquals(DEFAULT_NAME, bean.getName());
		bean.setName(MODIFIED_NAME);

		RequestContextHolder.setRequestAttributes(newRequestAttributes);
		// not a proxy so this should not have changed
		assertEquals(MODIFIED_NAME, bean.getName());

		// but a newly retrieved bean should have the default name
		ScopedTestBean bean2 = (ScopedTestBean) context.getBean("request");
		assertEquals(DEFAULT_NAME, bean2.getName());
	}

	@Test
	public void requestScopeWithProxiedInterfaces() {
		RequestContextHolder.setRequestAttributes(oldRequestAttributes);
		ApplicationContext context = createContext(INTERFACES);
		IScopedTestBean bean = (IScopedTestBean) context.getBean("request");

		// should be dynamic proxy, implementing both interfaces
		assertTrue(AopUtils.isJdkDynamicProxy(bean));
		assertTrue(bean instanceof AnotherScopeTestInterface);

		assertEquals(DEFAULT_NAME, bean.getName());
		bean.setName(MODIFIED_NAME);

		RequestContextHolder.setRequestAttributes(newRequestAttributes);
		// this is a proxy so it should be reset to default
		assertEquals(DEFAULT_NAME, bean.getName());

		RequestContextHolder.setRequestAttributes(oldRequestAttributes);
		assertEquals(MODIFIED_NAME, bean.getName());
	}

	@Test
	public void requestScopeWithProxiedTargetClass() {
		RequestContextHolder.setRequestAttributes(oldRequestAttributes);
		ApplicationContext context = createContext(TARGET_CLASS);
		IScopedTestBean bean = (IScopedTestBean) context.getBean("request");

		// should be a class-based proxy
		assertTrue(AopUtils.isCglibProxy(bean));
		assertTrue(bean instanceof RequestScopedTestBean);

		assertEquals(DEFAULT_NAME, bean.getName());
		bean.setName(MODIFIED_NAME);

		RequestContextHolder.setRequestAttributes(newRequestAttributes);
		// this is a proxy so it should be reset to default
		assertEquals(DEFAULT_NAME, bean.getName());

		RequestContextHolder.setRequestAttributes(oldRequestAttributes);
		assertEquals(MODIFIED_NAME, bean.getName());
	}

	@Test
	public void sessionScopeWithNoProxy() {
		RequestContextHolder.setRequestAttributes(oldRequestAttributesWithSession);
		ApplicationContext context = createContext(NO);
		ScopedTestBean bean = (ScopedTestBean) context.getBean("session");

		// should not be a proxy
		assertFalse(AopUtils.isAopProxy(bean));

		assertEquals(DEFAULT_NAME, bean.getName());
		bean.setName(MODIFIED_NAME);

		RequestContextHolder.setRequestAttributes(newRequestAttributesWithSession);
		// not a proxy so this should not have changed
		assertEquals(MODIFIED_NAME, bean.getName());

		// but a newly retrieved bean should have the default name
		ScopedTestBean bean2 = (ScopedTestBean) context.getBean("session");
		assertEquals(DEFAULT_NAME, bean2.getName());
	}

	@Test
	public void sessionScopeWithProxiedInterfaces() {
		RequestContextHolder.setRequestAttributes(oldRequestAttributesWithSession);
		ApplicationContext context = createContext(INTERFACES);
		IScopedTestBean bean = (IScopedTestBean) context.getBean("session");

		// should be dynamic proxy, implementing both interfaces
		assertTrue(AopUtils.isJdkDynamicProxy(bean));
		assertTrue(bean instanceof AnotherScopeTestInterface);

		assertEquals(DEFAULT_NAME, bean.getName());
		bean.setName(MODIFIED_NAME);

		RequestContextHolder.setRequestAttributes(newRequestAttributesWithSession);
		// this is a proxy so it should be reset to default
		assertEquals(DEFAULT_NAME, bean.getName());
		bean.setName(MODIFIED_NAME);

		IScopedTestBean bean2 = (IScopedTestBean) context.getBean("session");
		assertEquals(MODIFIED_NAME, bean2.getName());
		bean2.setName(DEFAULT_NAME);
		assertEquals(DEFAULT_NAME, bean.getName());

		RequestContextHolder.setRequestAttributes(oldRequestAttributesWithSession);
		assertEquals(MODIFIED_NAME, bean.getName());
	}

	@Test
	public void sessionScopeWithProxiedTargetClass() {
		RequestContextHolder.setRequestAttributes(oldRequestAttributesWithSession);
		ApplicationContext context = createContext(TARGET_CLASS);
		IScopedTestBean bean = (IScopedTestBean) context.getBean("session");

		// should be a class-based proxy
		assertTrue(AopUtils.isCglibProxy(bean));
		assertTrue(bean instanceof ScopedTestBean);
		assertTrue(bean instanceof SessionScopedTestBean);

		assertEquals(DEFAULT_NAME, bean.getName());
		bean.setName(MODIFIED_NAME);

		RequestContextHolder.setRequestAttributes(newRequestAttributesWithSession);
		// this is a proxy so it should be reset to default
		assertEquals(DEFAULT_NAME, bean.getName());
		bean.setName(MODIFIED_NAME);

		IScopedTestBean bean2 = (IScopedTestBean) context.getBean("session");
		assertEquals(MODIFIED_NAME, bean2.getName());
		bean2.setName(DEFAULT_NAME);
		assertEquals(DEFAULT_NAME, bean.getName());

		RequestContextHolder.setRequestAttributes(oldRequestAttributesWithSession);
		assertEquals(MODIFIED_NAME, bean.getName());
	}


	private ApplicationContext createContext(ScopedProxyMode scopedProxyMode) {
		GenericWebApplicationContext context = new GenericWebApplicationContext();
		ClassPathBeanDefinitionScanner scanner = new ClassPathBeanDefinitionScanner(context);
		scanner.setIncludeAnnotationConfig(false);
		scanner.setBeanNameGenerator((definition, registry) -> definition.getScope());
		scanner.setScopedProxyMode(scopedProxyMode);

		// Scan twice in order to find errors in the bean definition compatibility check.
		scanner.scan(getClass().getPackage().getName());
		scanner.scan(getClass().getPackage().getName());

		context.refresh();
		return context;
	}


	static interface IScopedTestBean {

		String getName();

		void setName(String name);
	}


	static abstract class ScopedTestBean implements IScopedTestBean {

		private String name = DEFAULT_NAME;

		@Override
		public String getName() { return this.name; }

		@Override
		public void setName(String name) { this.name = name; }
	}


	@Component
	static class SingletonScopedTestBean extends ScopedTestBean {
	}


	static interface AnotherScopeTestInterface {
	}


	@Component
	@RequestScope(proxyMode = DEFAULT)
	static class RequestScopedTestBean extends ScopedTestBean implements AnotherScopeTestInterface {
	}


	@Component
	@SessionScope(proxyMode = DEFAULT)
	static class SessionScopedTestBean extends ScopedTestBean implements AnotherScopeTestInterface {
	}

}
