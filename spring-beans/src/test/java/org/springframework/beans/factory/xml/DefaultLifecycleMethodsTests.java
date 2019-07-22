

package org.springframework.beans.factory.xml;

import org.junit.Before;
import org.junit.Test;

import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.core.io.ClassPathResource;

import static org.junit.Assert.*;


public class DefaultLifecycleMethodsTests {

	private final DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();


	@Before
	public void setup() throws Exception {
		new XmlBeanDefinitionReader(this.beanFactory).loadBeanDefinitions(
				new ClassPathResource("defaultLifecycleMethods.xml", getClass()));
	}


	@Test
	public void lifecycleMethodsInvoked() {
		LifecycleAwareBean bean = (LifecycleAwareBean) this.beanFactory.getBean("lifecycleAware");
		assertTrue("Bean not initialized", bean.isInitCalled());
		assertFalse("Custom init method called incorrectly", bean.isCustomInitCalled());
		assertFalse("Bean destroyed too early", bean.isDestroyCalled());
		this.beanFactory.destroySingletons();
		assertTrue("Bean not destroyed", bean.isDestroyCalled());
		assertFalse("Custom destroy method called incorrectly", bean.isCustomDestroyCalled());
	}

	@Test
	public void lifecycleMethodsDisabled() throws Exception {
		LifecycleAwareBean bean = (LifecycleAwareBean) this.beanFactory.getBean("lifecycleMethodsDisabled");
		assertFalse("Bean init method called incorrectly", bean.isInitCalled());
		assertFalse("Custom init method called incorrectly", bean.isCustomInitCalled());
		this.beanFactory.destroySingletons();
		assertFalse("Bean destroy method called incorrectly", bean.isDestroyCalled());
		assertFalse("Custom destroy method called incorrectly", bean.isCustomDestroyCalled());
	}

	@Test
	public void ignoreDefaultLifecycleMethods() throws Exception {
		DefaultListableBeanFactory bf = new DefaultListableBeanFactory();
		new XmlBeanDefinitionReader(bf).loadBeanDefinitions(new ClassPathResource(
				"ignoreDefaultLifecycleMethods.xml", getClass()));
		bf.preInstantiateSingletons();
		bf.destroySingletons();
	}

	@Test
	public void overrideDefaultLifecycleMethods() throws Exception {
		LifecycleAwareBean bean = (LifecycleAwareBean) this.beanFactory.getBean("overrideLifecycleMethods");
		assertFalse("Default init method called incorrectly", bean.isInitCalled());
		assertTrue("Custom init method not called", bean.isCustomInitCalled());
		this.beanFactory.destroySingletons();
		assertFalse("Default destroy method called incorrectly", bean.isDestroyCalled());
		assertTrue("Custom destroy method not called", bean.isCustomDestroyCalled());
	}

	@Test
	public void childWithDefaultLifecycleMethods() throws Exception {
		LifecycleAwareBean bean = (LifecycleAwareBean) this.beanFactory.getBean("childWithDefaultLifecycleMethods");
		assertTrue("Bean not initialized", bean.isInitCalled());
		assertFalse("Custom init method called incorrectly", bean.isCustomInitCalled());
		assertFalse("Bean destroyed too early", bean.isDestroyCalled());
		this.beanFactory.destroySingletons();
		assertTrue("Bean not destroyed", bean.isDestroyCalled());
		assertFalse("Custom destroy method called incorrectly", bean.isCustomDestroyCalled());
	}

	@Test
	public void childWithLifecycleMethodsDisabled() throws Exception {
		LifecycleAwareBean bean = (LifecycleAwareBean) this.beanFactory.getBean("childWithLifecycleMethodsDisabled");
		assertFalse("Bean init method called incorrectly", bean.isInitCalled());
		assertFalse("Custom init method called incorrectly", bean.isCustomInitCalled());
		this.beanFactory.destroySingletons();
		assertFalse("Bean destroy method called incorrectly", bean.isDestroyCalled());
		assertFalse("Custom destroy method called incorrectly", bean.isCustomDestroyCalled());
	}


	public static class LifecycleAwareBean {

		private boolean initCalled;

		private boolean destroyCalled;

		private boolean customInitCalled;

		private boolean customDestroyCalled;

		public void init() {
			this.initCalled = true;
		}

		public void destroy() {
			this.destroyCalled = true;
		}

		public void customInit() {
			this.customInitCalled = true;
		}

		public void customDestroy() {
			this.customDestroyCalled = true;
		}

		public boolean isInitCalled() {
			return initCalled;
		}

		public boolean isDestroyCalled() {
			return destroyCalled;
		}

		public boolean isCustomInitCalled() {
			return customInitCalled;
		}

		public boolean isCustomDestroyCalled() {
			return customDestroyCalled;
		}
	}

}
