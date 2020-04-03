

package org.springframework.beans.factory.support;

import org.junit.Test;
import org.springframework.tests.sample.beans.DerivedTestBean;
import org.springframework.tests.sample.beans.TestBean;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static org.junit.Assert.*;

/**
 * @since 04.07.2006
 */
public class DefaultSingletonBeanRegistryTests {

	DefaultSingletonBeanRegistry beanRegistry = new DefaultSingletonBeanRegistry();

	// 测试 registerSingleton中的 singletonObjects.get(beanName) 可以替换成 singletonObjects.containsKey(beanName)
	@Test
	public void containsKey() {
		Map<String, Object> singletonMutex = new ConcurrentHashMap<>();
		singletonMutex.put("1",2);
		System.out.println(singletonMutex);
		System.out.println(singletonMutex.containsKey("1"));
		System.out.println(singletonMutex.containsKey(1));
		System.out.println(singletonMutex.containsKey("2"));
	}

	// 测试 key相同 不能覆盖 而是抛出异常
	@Test
	public void testSingletons1() {
		TestBean tb = new TestBean();
		beanRegistry.registerSingleton("test1", tb);
		beanRegistry.registerSingleton("test1", tb);
		Map<String, Object> singletonMutex = (Map<String, Object>) beanRegistry.getSingletonMutex();
		System.out.println(singletonMutex);
	}

	@Test
	public void testSingletons() {
		TestBean tb = new TestBean();
		beanRegistry.registerSingleton("tb", tb);
		assertSame(tb, beanRegistry.getSingleton("tb"));

		TestBean tb2 = (TestBean) beanRegistry.getSingleton("tb2", ()->new TestBean());
		assertSame(tb2, beanRegistry.getSingleton("tb2"));

		assertEquals(2, beanRegistry.getSingletonCount());
		String[] names = beanRegistry.getSingletonNames();
		assertEquals(2, names.length);
		assertEquals("tb", names[0]);
		assertEquals("tb2", names[1]);

		beanRegistry.destroySingletons();
		assertEquals(0, beanRegistry.getSingletonCount());
		assertEquals(0, beanRegistry.getSingletonNames().length);
	}

	@Test
	public void testDisposableBean() {
		DerivedTestBean tb = new DerivedTestBean();
		System.out.println(tb.getBeanName());
		System.out.println(tb.getSex());
		beanRegistry.registerSingleton("tb", tb);
		beanRegistry.registerDisposableBean("tb", tb);
		assertSame(tb, beanRegistry.getSingleton("tb"));

		assertSame(tb, beanRegistry.getSingleton("tb"));
		assertEquals(1, beanRegistry.getSingletonCount());
		String[] names = beanRegistry.getSingletonNames();
		assertEquals(1, names.length);
		assertEquals("tb", names[0]);
		assertFalse(tb.wasDestroyed());

		beanRegistry.destroySingletons();
		assertEquals(0, beanRegistry.getSingletonCount());
		assertEquals(0, beanRegistry.getSingletonNames().length);
		assertTrue(tb.wasDestroyed());
	}

	@Test
	public void testDependentRegistration() {
		beanRegistry.registerDependentBean("a", "b");
		beanRegistry.registerDependentBean("b", "c");
		beanRegistry.registerDependentBean("c", "b");
		assertTrue(beanRegistry.isDependent("a", "b"));
		assertTrue(beanRegistry.isDependent("b", "c"));
		assertTrue(beanRegistry.isDependent("c", "b"));
		assertTrue(beanRegistry.isDependent("a", "c"));
		assertFalse(beanRegistry.isDependent("c", "a"));
		assertFalse(beanRegistry.isDependent("b", "a"));
		assertFalse(beanRegistry.isDependent("a", "a"));
		assertTrue(beanRegistry.isDependent("b", "b"));
		assertTrue(beanRegistry.isDependent("c", "c"));
	}

}
