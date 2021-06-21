

package org.springframework.core.io.support;

import java.lang.reflect.Modifier;
import java.util.List;

import org.junit.Test;
import org.springframework.core.annotation.AnnotationAwareOrderComparator;

import static org.junit.Assert.*;

/**
 * Tests for {@link SpringFactoriesLoader}.
 * 【spring.factories】配置文件内容：
 * org.springframework.core.io.support.DummyFactory =org.springframework.core.io.support.MyDummyFactory2, org.springframework.core.io.support.MyDummyFactory1
 * java.lang.String=org.springframework.core.io.support.MyDummyFactory1
 * org.springframework.core.io.support.DummyPackagePrivateFactory=org.springframework.core.io.support.DummyPackagePrivateFactory
 */
public class SpringFactoriesLoaderTests {

	/**
	 * 测试 从spring.factories文件加载内容，并且按照@Order注解排序。
	 * @see SpringFactoriesLoader#loadFactories(java.lang.Class, java.lang.ClassLoader)
	 * @see SpringFactoriesLoader#loadFactoryNames(java.lang.Class, java.lang.ClassLoader)
	 * @see SpringFactoriesLoader#loadSpringFactories(java.lang.ClassLoader)
	 * @see AnnotationAwareOrderComparator#sort(java.util.List)
	*/
	@Test
	public void loadFactoriesInCorrectOrder() {
		List<DummyFactory> factories = SpringFactoriesLoader.loadFactories(DummyFactory.class, null);
		assertEquals(2, factories.size());
		assertTrue(factories.get(0) instanceof MyDummyFactory1);
		assertTrue(factories.get(1) instanceof MyDummyFactory2);
	}

	/**
	 * 测试  String.class 必须得是实现类的父类，即为 DummyFactory.class 必须是  MyDummyFactory1.class 的父类，否则会报异常
	 * @see SpringFactoriesLoader#loadFactories(java.lang.Class, java.lang.ClassLoader)
	 */
	@Test(expected = IllegalArgumentException.class)
	public void loadInvalid() {
		SpringFactoriesLoader.loadFactories(String.class, null);
	}

	@Test
	public void loadPackagePrivateFactory() {
		List<DummyPackagePrivateFactory> factories = SpringFactoriesLoader.loadFactories(DummyPackagePrivateFactory.class, null);
		assertEquals(1, factories.size());
		assertTrue((factories.get(0).getClass().getModifiers() & Modifier.PUBLIC) == 0);
	}
}
