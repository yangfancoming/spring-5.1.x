package org.springframework.beans.factory.support;

import org.junit.Test;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.tests.sample.beans.TestBean;
import static org.junit.Assert.*;


public class BeanDefinitionTests {

	/**
	 * 只有为 "singleton" 或者 ""  的情况下，该bean才是单例！
	*/
	@Test
	public void testScope1() {
		RootBeanDefinition bd = new RootBeanDefinition(TestBean.class);
		// 默认为""，单例
		assertTrue(bd.isSingleton());
		// 非单例
		bd.setScope("fuck");
		assertFalse(bd.isSingleton());
		// 非单例
		bd.setScope(BeanDefinition.SCOPE_PROTOTYPE);
		assertFalse(bd.isSingleton());
	}

	// 测试 作用域为 原型模式下  两次获取的bean是不相同的
	@Test
	public void testScope2() {
		DefaultListableBeanFactory bf = new DefaultListableBeanFactory();
		RootBeanDefinition bd = new RootBeanDefinition(TestBean.class);
		bd.setScope(BeanDefinition.SCOPE_PROTOTYPE);
		bf.registerBeanDefinition("shit",bd);
		assertFalse(bd.isSingleton());
		TestBean shit1 = (TestBean) bf.getBean("shit");
		TestBean shit2 = (TestBean) bf.getBean("shit");
		assertNotSame(shit1,shit2);
	}

	@Test
	public void beanDefinitionEquality() {
		RootBeanDefinition bd = new RootBeanDefinition(TestBean.class);
		bd.setAbstract(true);
		bd.setLazyInit(true);
		bd.setScope("request");
		RootBeanDefinition otherBd = new RootBeanDefinition(TestBean.class);
		assertTrue(!bd.equals(otherBd));
		assertTrue(!otherBd.equals(bd));
		otherBd.setAbstract(true);
		otherBd.setLazyInit(true);
		otherBd.setScope("request");
		assertTrue(bd.equals(otherBd));
		assertTrue(otherBd.equals(bd));
		assertTrue(bd.hashCode() == otherBd.hashCode());
	}

	// 通过断点可以看到 bd 和 otherBd 地址是不同的，只不过是重写了 equals和hashcode方法。
	@Test
	public void beanDefinitionEqualityWithPropertyValues() {
		RootBeanDefinition bd = new RootBeanDefinition(TestBean.class);
		bd.getPropertyValues().add("name", "myName");
		bd.getPropertyValues().add("age", "99");
		RootBeanDefinition otherBd = new RootBeanDefinition(TestBean.class);
		otherBd.getPropertyValues().add("name", "myName");
		assertTrue(!bd.equals(otherBd));
		assertTrue(!otherBd.equals(bd));
		otherBd.getPropertyValues().add("age", "11");
		assertTrue(!bd.equals(otherBd));
		assertTrue(!otherBd.equals(bd));
		otherBd.getPropertyValues().add("age", "99");
		assertTrue(bd.equals(otherBd));
		assertTrue(otherBd.equals(bd));
		assertTrue(bd.hashCode() == otherBd.hashCode());
	}

	@Test
	public void beanDefinitionEqualityWithConstructorArguments() {
		RootBeanDefinition bd = new RootBeanDefinition(TestBean.class);
		bd.getConstructorArgumentValues().addGenericArgumentValue("test");
		bd.getConstructorArgumentValues().addIndexedArgumentValue(1, new Integer(5));
		RootBeanDefinition otherBd = new RootBeanDefinition(TestBean.class);
		otherBd.getConstructorArgumentValues().addGenericArgumentValue("test");
		assertTrue(!bd.equals(otherBd));
		assertTrue(!otherBd.equals(bd));
		otherBd.getConstructorArgumentValues().addIndexedArgumentValue(1, new Integer(9));
		assertTrue(!bd.equals(otherBd));
		assertTrue(!otherBd.equals(bd));
		otherBd.getConstructorArgumentValues().addIndexedArgumentValue(1, new Integer(5));
		assertTrue(bd.equals(otherBd));
		assertTrue(otherBd.equals(bd));
		assertTrue(bd.hashCode() == otherBd.hashCode());
	}

	@Test
	public void beanDefinitionEqualityWithTypedConstructorArguments() {
		RootBeanDefinition bd = new RootBeanDefinition(TestBean.class);
		bd.getConstructorArgumentValues().addGenericArgumentValue("test", "int");
		bd.getConstructorArgumentValues().addIndexedArgumentValue(1, new Integer(5), "long");
		RootBeanDefinition otherBd = new RootBeanDefinition(TestBean.class);
		otherBd.getConstructorArgumentValues().addGenericArgumentValue("test", "int");
		otherBd.getConstructorArgumentValues().addIndexedArgumentValue(1, new Integer(5));
		assertTrue(!bd.equals(otherBd));
		assertTrue(!otherBd.equals(bd));
		otherBd.getConstructorArgumentValues().addIndexedArgumentValue(1, new Integer(5), "int");
		assertTrue(!bd.equals(otherBd));
		assertTrue(!otherBd.equals(bd));
		otherBd.getConstructorArgumentValues().addIndexedArgumentValue(1, new Integer(5), "long");
		assertTrue(bd.equals(otherBd));
		assertTrue(otherBd.equals(bd));
		assertTrue(bd.hashCode() == otherBd.hashCode());
	}

	@Test
	public void beanDefinitionHolderEquality() {
		RootBeanDefinition bd = new RootBeanDefinition(TestBean.class);
		bd.setAbstract(true);
		bd.setLazyInit(true);
		bd.setScope("request");
		BeanDefinitionHolder holder = new BeanDefinitionHolder(bd, "bd");
		RootBeanDefinition otherBd = new RootBeanDefinition(TestBean.class);
		assertTrue(!bd.equals(otherBd));
		assertTrue(!otherBd.equals(bd));
		otherBd.setAbstract(true);
		otherBd.setLazyInit(true);
		otherBd.setScope("request");
		BeanDefinitionHolder otherHolder = new BeanDefinitionHolder(bd, "bd");
		assertTrue(holder.equals(otherHolder));
		assertTrue(otherHolder.equals(holder));
		assertTrue(holder.hashCode() == otherHolder.hashCode());
	}

	// 测试 bean定义合并
	@Test
	public void beanDefinitionMerging() {
		RootBeanDefinition bd = new RootBeanDefinition(TestBean.class);
		bd.getConstructorArgumentValues().addGenericArgumentValue("test");
		bd.getConstructorArgumentValues().addIndexedArgumentValue(1, new Integer(5));
		bd.getPropertyValues().add("name", "myName");
		bd.getPropertyValues().add("age", "99");
		bd.setQualifiedElement(getClass());

		GenericBeanDefinition childBd = new GenericBeanDefinition();
		childBd.setParentName("bd");

		RootBeanDefinition mergedBd = new RootBeanDefinition(bd);
		mergedBd.overrideFrom(childBd);
		assertEquals(2, mergedBd.getConstructorArgumentValues().getArgumentCount());
		assertEquals(2, mergedBd.getPropertyValues().size());
		assertEquals(bd, mergedBd);

		mergedBd.getConstructorArgumentValues().getArgumentValue(1, null).setValue(new Integer(9));
		assertEquals(new Integer(5), bd.getConstructorArgumentValues().getArgumentValue(1, null).getValue());
		assertEquals(getClass(), bd.getQualifiedElement());
	}
}
