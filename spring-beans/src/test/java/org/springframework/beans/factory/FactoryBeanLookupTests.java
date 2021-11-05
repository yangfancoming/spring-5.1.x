

package org.springframework.beans.factory;

import org.junit.Before;
import org.junit.Test;

import org.springframework.beans.factory.config.AbstractFactoryBean;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.core.io.ClassPathResource;

import java.util.Arrays;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

/**
 * Written with the intention of reproducing SPR-7318.

 */
public class FactoryBeanLookupTests {
	private BeanFactory beanFactory;

	@Before
	public void setUp() {
		beanFactory = new DefaultListableBeanFactory();
		new XmlBeanDefinitionReader((BeanDefinitionRegistry) beanFactory).loadBeanDefinitions(
				new ClassPathResource("FactoryBeanLookupTests-context.xml", this.getClass()));
	}

	// 自定义测试  查看容器中已加载的bean集合
	@Test
	public void myTest() {
		ListableBeanFactory listableBeanFactory = (ListableBeanFactory)beanFactory;
		String[] str = listableBeanFactory.getBeanDefinitionNames();
		Arrays.stream(str).forEach(x->System.out.println("***---***" + x));
	}

	@Test
	public void factoryBeanLookupByNameDereferencing() {
		Object fooFactory = beanFactory.getBean("&fooFactory");
		assertThat(fooFactory, instanceOf(FooFactoryBean.class));
	}

	@Test
	public void factoryBeanLookupByType() {
		FooFactoryBean fooFactory = beanFactory.getBean(FooFactoryBean.class);
		assertNotNull(fooFactory);
	}

	@Test
	public void factoryBeanLookupByTypeAndNameDereference() {
		FooFactoryBean fooFactory = beanFactory.getBean("&fooFactory", FooFactoryBean.class);
		assertNotNull(fooFactory);
	}

	@Test
	public void factoryBeanObjectLookupByName() {
		Object fooFactory = beanFactory.getBean("fooFactory");
		assertThat(fooFactory, instanceOf(Foo.class));
	}

	@Test
	public void factoryBeanObjectLookupByNameAndType() {
		Foo foo = beanFactory.getBean("fooFactory", Foo.class);
		assertNotNull(foo);
	}
}

class FooFactoryBean extends AbstractFactoryBean<Foo> {
	@Override
	protected Foo createInstance() throws Exception {
		return new Foo();
	}

	@Override
	public Class<?> getObjectType() {
		return Foo.class;
	}
}

class Foo { }
