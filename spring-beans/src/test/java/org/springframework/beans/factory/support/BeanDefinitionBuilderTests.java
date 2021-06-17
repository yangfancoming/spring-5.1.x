

package org.springframework.beans.factory.support;

import java.util.Arrays;

import org.junit.Test;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.tests.sample.beans.TestBean;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;


public class BeanDefinitionBuilderTests {

	class Child{

	}

	@Test
	public void tst() {
		AbstractBeanDefinition beanDefinition = BeanDefinitionBuilder.genericBeanDefinition(Child.class)
				.setRole(BeanDefinition.ROLE_APPLICATION)
				.setScope(BeanDefinition.SCOPE_SINGLETON)
				.addPropertyValue("name", "fsx")
				.setLazyInit(false)
				// Spring5.0后提供的，可以自己书写函数，在里面做任意事情
				// bdf是个AbstractBeanDefinition
				.applyCustomizers((bdf) -> {
					AbstractBeanDefinition abdf = (AbstractBeanDefinition) bdf;
					abdf.setAutowireMode(AbstractBeanDefinition.AUTOWIRE_NO);
				}).getRawBeanDefinition();
		System.out.println(beanDefinition);
	}

	@Test
	public void beanClassWithSimpleProperty() {
		String[] dependsOn = new String[] { "A", "B", "C" };
		BeanDefinitionBuilder bdb = BeanDefinitionBuilder.rootBeanDefinition(TestBean.class);
		bdb.setScope(BeanDefinition.SCOPE_PROTOTYPE);
		bdb.addPropertyValue("age", "15");
		for (String dependsOnEntry : dependsOn) {
			bdb.addDependsOn(dependsOnEntry);
		}

		RootBeanDefinition rbd = (RootBeanDefinition) bdb.getBeanDefinition();
		assertFalse(rbd.isSingleton());
		assertEquals(TestBean.class, rbd.getBeanClass());
		assertTrue("Depends on was added", Arrays.equals(dependsOn, rbd.getDependsOn()));
		assertTrue(rbd.getPropertyValues().contains("age"));
	}

	@Test
	public void beanClassWithFactoryMethod() {
		BeanDefinitionBuilder bdb = BeanDefinitionBuilder.rootBeanDefinition(TestBean.class, "create");
		RootBeanDefinition rbd = (RootBeanDefinition) bdb.getBeanDefinition();
		assertTrue(rbd.hasBeanClass());
		assertEquals(TestBean.class, rbd.getBeanClass());
		assertEquals("create", rbd.getFactoryMethodName());
	}

	@Test
	public void beanClassName() {
		BeanDefinitionBuilder bdb = BeanDefinitionBuilder.rootBeanDefinition(TestBean.class.getName());
		RootBeanDefinition rbd = (RootBeanDefinition) bdb.getBeanDefinition();
		assertFalse(rbd.hasBeanClass());
		assertEquals(TestBean.class.getName(), rbd.getBeanClassName());
	}

	@Test
	public void beanClassNameWithFactoryMethod() {
		BeanDefinitionBuilder bdb = BeanDefinitionBuilder.rootBeanDefinition(TestBean.class.getName(), "create");
		RootBeanDefinition rbd = (RootBeanDefinition) bdb.getBeanDefinition();
		assertFalse(rbd.hasBeanClass());
		assertEquals(TestBean.class.getName(), rbd.getBeanClassName());
		assertEquals("create", rbd.getFactoryMethodName());
	}

}
