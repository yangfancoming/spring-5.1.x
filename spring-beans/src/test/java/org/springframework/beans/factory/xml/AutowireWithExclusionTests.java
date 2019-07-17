

package org.springframework.beans.factory.xml;

import org.junit.Test;

import org.springframework.beans.factory.config.PropertiesFactoryBean;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.core.io.ClassPathResource;
import org.springframework.tests.sample.beans.TestBean;

import static org.junit.Assert.*;

/**
 * @author Rob Harrop

 */
public class AutowireWithExclusionTests {

	@Test
	public void byTypeAutowireWithAutoSelfExclusion() throws Exception {
		CountingFactory.reset();
		DefaultListableBeanFactory beanFactory = getBeanFactory("autowire-with-exclusion.xml");
		beanFactory.preInstantiateSingletons();
		TestBean rob = (TestBean) beanFactory.getBean("rob");
		TestBean sally = (TestBean) beanFactory.getBean("sally");
		assertEquals(sally, rob.getSpouse());
		assertEquals(1, CountingFactory.getFactoryBeanInstanceCount());
	}

	@Test
	public void byTypeAutowireWithExclusion() throws Exception {
		CountingFactory.reset();
		DefaultListableBeanFactory beanFactory = getBeanFactory("autowire-with-exclusion.xml");
		beanFactory.preInstantiateSingletons();
		TestBean rob = (TestBean) beanFactory.getBean("rob");
		assertEquals("props1", rob.getSomeProperties().getProperty("name"));
		assertEquals(1, CountingFactory.getFactoryBeanInstanceCount());
	}

	@Test
	public void byTypeAutowireWithExclusionInParentFactory() throws Exception {
		CountingFactory.reset();
		DefaultListableBeanFactory parent = getBeanFactory("autowire-with-exclusion.xml");
		parent.preInstantiateSingletons();
		DefaultListableBeanFactory child = new DefaultListableBeanFactory(parent);
		RootBeanDefinition robDef = new RootBeanDefinition(TestBean.class);
		robDef.setAutowireMode(RootBeanDefinition.AUTOWIRE_BY_TYPE);
		robDef.getPropertyValues().add("spouse", new RuntimeBeanReference("sally"));
		child.registerBeanDefinition("rob2", robDef);
		TestBean rob = (TestBean) child.getBean("rob2");
		assertEquals("props1", rob.getSomeProperties().getProperty("name"));
		assertEquals(1, CountingFactory.getFactoryBeanInstanceCount());
	}

	@Test
	public void byTypeAutowireWithPrimaryInParentFactory() throws Exception {
		CountingFactory.reset();
		DefaultListableBeanFactory parent = getBeanFactory("autowire-with-exclusion.xml");
		parent.getBeanDefinition("props1").setPrimary(true);
		parent.preInstantiateSingletons();
		DefaultListableBeanFactory child = new DefaultListableBeanFactory(parent);
		RootBeanDefinition robDef = new RootBeanDefinition(TestBean.class);
		robDef.setAutowireMode(RootBeanDefinition.AUTOWIRE_BY_TYPE);
		robDef.getPropertyValues().add("spouse", new RuntimeBeanReference("sally"));
		child.registerBeanDefinition("rob2", robDef);
		RootBeanDefinition propsDef = new RootBeanDefinition(PropertiesFactoryBean.class);
		propsDef.getPropertyValues().add("properties", "name=props3");
		child.registerBeanDefinition("props3", propsDef);
		TestBean rob = (TestBean) child.getBean("rob2");
		assertEquals("props1", rob.getSomeProperties().getProperty("name"));
		assertEquals(1, CountingFactory.getFactoryBeanInstanceCount());
	}

	@Test
	public void byTypeAutowireWithPrimaryOverridingParentFactory() throws Exception {
		CountingFactory.reset();
		DefaultListableBeanFactory parent = getBeanFactory("autowire-with-exclusion.xml");
		parent.preInstantiateSingletons();
		DefaultListableBeanFactory child = new DefaultListableBeanFactory(parent);
		RootBeanDefinition robDef = new RootBeanDefinition(TestBean.class);
		robDef.setAutowireMode(RootBeanDefinition.AUTOWIRE_BY_TYPE);
		robDef.getPropertyValues().add("spouse", new RuntimeBeanReference("sally"));
		child.registerBeanDefinition("rob2", robDef);
		RootBeanDefinition propsDef = new RootBeanDefinition(PropertiesFactoryBean.class);
		propsDef.getPropertyValues().add("properties", "name=props3");
		propsDef.setPrimary(true);
		child.registerBeanDefinition("props3", propsDef);
		TestBean rob = (TestBean) child.getBean("rob2");
		assertEquals("props3", rob.getSomeProperties().getProperty("name"));
		assertEquals(1, CountingFactory.getFactoryBeanInstanceCount());
	}

	@Test
	public void byTypeAutowireWithPrimaryInParentAndChild() throws Exception {
		CountingFactory.reset();
		DefaultListableBeanFactory parent = getBeanFactory("autowire-with-exclusion.xml");
		parent.getBeanDefinition("props1").setPrimary(true);
		parent.preInstantiateSingletons();
		DefaultListableBeanFactory child = new DefaultListableBeanFactory(parent);
		RootBeanDefinition robDef = new RootBeanDefinition(TestBean.class);
		robDef.setAutowireMode(RootBeanDefinition.AUTOWIRE_BY_TYPE);
		robDef.getPropertyValues().add("spouse", new RuntimeBeanReference("sally"));
		child.registerBeanDefinition("rob2", robDef);
		RootBeanDefinition propsDef = new RootBeanDefinition(PropertiesFactoryBean.class);
		propsDef.getPropertyValues().add("properties", "name=props3");
		propsDef.setPrimary(true);
		child.registerBeanDefinition("props3", propsDef);
		TestBean rob = (TestBean) child.getBean("rob2");
		assertEquals("props3", rob.getSomeProperties().getProperty("name"));
		assertEquals(1, CountingFactory.getFactoryBeanInstanceCount());
	}

	@Test
	public void byTypeAutowireWithInclusion() throws Exception {
		CountingFactory.reset();
		DefaultListableBeanFactory beanFactory = getBeanFactory("autowire-with-inclusion.xml");
		beanFactory.preInstantiateSingletons();
		TestBean rob = (TestBean) beanFactory.getBean("rob");
		assertEquals("props1", rob.getSomeProperties().getProperty("name"));
		assertEquals(1, CountingFactory.getFactoryBeanInstanceCount());
	}

	@Test
	public void byTypeAutowireWithSelectiveInclusion() throws Exception {
		CountingFactory.reset();
		DefaultListableBeanFactory beanFactory = getBeanFactory("autowire-with-selective-inclusion.xml");
		beanFactory.preInstantiateSingletons();
		TestBean rob = (TestBean) beanFactory.getBean("rob");
		assertEquals("props1", rob.getSomeProperties().getProperty("name"));
		assertEquals(1, CountingFactory.getFactoryBeanInstanceCount());
	}

	@Test
	public void constructorAutowireWithAutoSelfExclusion() throws Exception {
		DefaultListableBeanFactory beanFactory = getBeanFactory("autowire-constructor-with-exclusion.xml");
		TestBean rob = (TestBean) beanFactory.getBean("rob");
		TestBean sally = (TestBean) beanFactory.getBean("sally");
		assertEquals(sally, rob.getSpouse());
		TestBean rob2 = (TestBean) beanFactory.getBean("rob");
		assertEquals(rob, rob2);
		assertNotSame(rob, rob2);
		assertEquals(rob.getSpouse(), rob2.getSpouse());
		assertNotSame(rob.getSpouse(), rob2.getSpouse());
	}

	@Test
	public void constructorAutowireWithExclusion() throws Exception {
		DefaultListableBeanFactory beanFactory = getBeanFactory("autowire-constructor-with-exclusion.xml");
		TestBean rob = (TestBean) beanFactory.getBean("rob");
		assertEquals("props1", rob.getSomeProperties().getProperty("name"));
	}

	private DefaultListableBeanFactory getBeanFactory(String configPath) {
		DefaultListableBeanFactory bf = new DefaultListableBeanFactory();
		new XmlBeanDefinitionReader(bf).loadBeanDefinitions(
				new ClassPathResource(configPath, getClass()));
		return bf;
	}

}
