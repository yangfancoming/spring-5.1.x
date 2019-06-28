

package org.springframework.scripting.config;

import java.lang.reflect.Field;

import org.junit.Test;

import org.springframework.aop.framework.Advised;
import org.springframework.aop.support.AopUtils;
import org.springframework.aop.target.dynamic.AbstractRefreshableTargetSource;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import static org.junit.Assert.*;

/**
 * @author Mark Fisher
 * @author Dave Syer
 */
@SuppressWarnings("resource")
public class ScriptingDefaultsTests {

	private static final String CONFIG =
		"org/springframework/scripting/config/scriptingDefaultsTests.xml";

	private static final String PROXY_CONFIG =
		"org/springframework/scripting/config/scriptingDefaultsProxyTargetClassTests.xml";


	@Test
	public void defaultRefreshCheckDelay() throws Exception {
		ApplicationContext context = new ClassPathXmlApplicationContext(CONFIG);
		Advised advised = (Advised) context.getBean("testBean");
		AbstractRefreshableTargetSource targetSource =
				((AbstractRefreshableTargetSource) advised.getTargetSource());
		Field field = AbstractRefreshableTargetSource.class.getDeclaredField("refreshCheckDelay");
		field.setAccessible(true);
		long delay = ((Long) field.get(targetSource)).longValue();
		assertEquals(5000L, delay);
	}

	@Test
	public void defaultInitMethod() {
		ApplicationContext context = new ClassPathXmlApplicationContext(CONFIG);
		ITestBean testBean = (ITestBean) context.getBean("testBean");
		assertTrue(testBean.isInitialized());
	}

	@Test
	public void nameAsAlias() {
		ApplicationContext context = new ClassPathXmlApplicationContext(CONFIG);
		ITestBean testBean = (ITestBean) context.getBean("/url");
		assertTrue(testBean.isInitialized());
	}

	@Test
	public void defaultDestroyMethod() {
		ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(CONFIG);
		ITestBean testBean = (ITestBean) context.getBean("nonRefreshableTestBean");
		assertFalse(testBean.isDestroyed());
		context.close();
		assertTrue(testBean.isDestroyed());
	}

	@Test
	public void defaultAutowire() {
		ApplicationContext context = new ClassPathXmlApplicationContext(CONFIG);
		ITestBean testBean = (ITestBean) context.getBean("testBean");
		ITestBean otherBean = (ITestBean) context.getBean("otherBean");
		assertEquals(otherBean, testBean.getOtherBean());
	}

	@Test
	public void defaultProxyTargetClass() {
		ApplicationContext context = new ClassPathXmlApplicationContext(PROXY_CONFIG);
		Object testBean = context.getBean("testBean");
		assertTrue(AopUtils.isCglibProxy(testBean));
	}

}
