

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

	/**
	 * 测试 key相同 不能覆盖 而是抛出异常
	*/
	@Test(expected = IllegalStateException.class)
	public void testOverridingException() {
		TestBean tb = new TestBean();
		beanRegistry.registerSingleton("test1", tb);
		beanRegistry.registerSingleton("test1", tb);
	}

	/**
	 * 测试 getSingletonMutex()
	 */
	@Test
	public void testGetSingletonMutex() {
		TestBean tb = new TestBean("goat");
		beanRegistry.registerSingleton("tb", tb);
		Map<String, Object> singletonObjects = (Map<String, Object>) beanRegistry.getSingletonMutex();
		assertEquals(1,singletonObjects.size());
	}

	/**
	 * 测试 getSingleton 单参  其实 所有的 getSingleton(String beanName); 都是是调用的
	 * @see DefaultSingletonBeanRegistry#getSingleton(java.lang.String, boolean)    P2 为true
	*/
	@Test
	public void testGetSingleton1() {
		TestBean tb = new TestBean("goat");
		beanRegistry.registerSingleton("tb", tb);
		TestBean testBean = (TestBean) beanRegistry.getSingleton("tb");
		assertSame(tb,testBean);
		assertSame("goat",testBean.getName());
	}
	/**
	 * 测试getSingleton 双参
	 * @see DefaultSingletonBeanRegistry#getSingleton(java.lang.String, org.springframework.beans.factory.ObjectFactory)
	*/
	@Test
	public void testGetSingleton2() {
		beanRegistry.getSingleton("tb2", ()->new TestBean());
		Map<String, Object> singletonObjects = (Map<String, Object>) beanRegistry.getSingletonMutex();
		assertEquals(1,singletonObjects.size());
	}

	@Test
	public void testSingletons() {
		TestBean tb = new TestBean();
		beanRegistry.registerSingleton("tb", tb);
		TestBean tb2 = (TestBean) beanRegistry.getSingleton("tb2", ()->new TestBean());
		assertNotSame(tb,tb2);

		// 测试 getSingletonCount
		assertEquals(2, beanRegistry.getSingletonCount());
		// 测试 getSingletonNames
		String[] names = beanRegistry.getSingletonNames();
		assertEquals(2, names.length);
		assertEquals("tb", names[0]);
		assertEquals("tb2", names[1]);
		// 测试 destroySingletons
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
