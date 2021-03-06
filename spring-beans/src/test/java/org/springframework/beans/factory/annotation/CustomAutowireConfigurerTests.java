

package org.springframework.beans.factory.annotation;

import org.junit.Test;

import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.config.DependencyDescriptor;
import org.springframework.beans.factory.support.AutowireCandidateResolver;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;

import static org.junit.Assert.*;
import static org.springframework.tests.TestResourceUtils.*;

/**
 * Unit tests for {@link CustomAutowireConfigurer}.
 */
public class CustomAutowireConfigurerTests {

	@Test
	public void testCustomResolver() {
		DefaultListableBeanFactory bf = new DefaultListableBeanFactory();
		new XmlBeanDefinitionReader(bf).loadBeanDefinitions(qualifiedResource(CustomAutowireConfigurerTests.class, "context.xml"));
		CustomAutowireConfigurer cac = new CustomAutowireConfigurer();
		CustomResolver customResolver = new CustomResolver();
		bf.setAutowireCandidateResolver(customResolver);
		cac.postProcessBeanFactory(bf);
		TestBean testBean = (TestBean) bf.getBean("testBean");
		assertEquals("#1!", testBean.getName());
	}


	public static class TestBean {

		private String name;

		public TestBean(String name) {
			this.name = name;
		}

		public String getName() {
			return this.name;
		}
	}


	public static class CustomResolver implements AutowireCandidateResolver {

		@Override
		public boolean isAutowireCandidate(BeanDefinitionHolder bdHolder, DependencyDescriptor descriptor) {
			if (!bdHolder.getBeanDefinition().isAutowireCandidate()) {
				return false;
			}
			if (!bdHolder.getBeanName().matches("[a-z-]+")) {
				return false;
			}
			if (bdHolder.getBeanDefinition().getAttribute("priority").equals("1")) {
				return true;
			}
			return false;
		}

		@Override
		public Object getSuggestedValue(DependencyDescriptor descriptor) {
			return null;
		}

		@Override
		public Object getLazyResolutionProxyIfNecessary(DependencyDescriptor descriptor, String beanName) {
			return null;
		}
	}

}
