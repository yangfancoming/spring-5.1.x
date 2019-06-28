

package org.springframework.beans.factory.serviceloader;

import java.util.List;
import java.util.ServiceLoader;
import javax.xml.parsers.DocumentBuilderFactory;

import org.junit.Test;

import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.support.RootBeanDefinition;

import static org.junit.Assert.*;
import static org.junit.Assume.*;

/**
 * @author Juergen Hoeller
 * @author Chris Beams
 */
public class ServiceLoaderTests {

	@Test
	public void testServiceLoaderFactoryBean() {
		assumeTrue(ServiceLoader.load(DocumentBuilderFactory.class).iterator().hasNext());

		DefaultListableBeanFactory bf = new DefaultListableBeanFactory();
		RootBeanDefinition bd = new RootBeanDefinition(ServiceLoaderFactoryBean.class);
		bd.getPropertyValues().add("serviceType", DocumentBuilderFactory.class.getName());
		bf.registerBeanDefinition("service", bd);
		ServiceLoader<?> serviceLoader = (ServiceLoader<?>) bf.getBean("service");
		assertTrue(serviceLoader.iterator().next() instanceof DocumentBuilderFactory);
	}

	@Test
	public void testServiceFactoryBean() {
		assumeTrue(ServiceLoader.load(DocumentBuilderFactory.class).iterator().hasNext());

		DefaultListableBeanFactory bf = new DefaultListableBeanFactory();
		RootBeanDefinition bd = new RootBeanDefinition(ServiceFactoryBean.class);
		bd.getPropertyValues().add("serviceType", DocumentBuilderFactory.class.getName());
		bf.registerBeanDefinition("service", bd);
		assertTrue(bf.getBean("service") instanceof DocumentBuilderFactory);
	}

	@Test
	public void testServiceListFactoryBean() {
		assumeTrue(ServiceLoader.load(DocumentBuilderFactory.class).iterator().hasNext());

		DefaultListableBeanFactory bf = new DefaultListableBeanFactory();
		RootBeanDefinition bd = new RootBeanDefinition(ServiceListFactoryBean.class);
		bd.getPropertyValues().add("serviceType", DocumentBuilderFactory.class.getName());
		bf.registerBeanDefinition("service", bd);
		List<?> serviceList = (List<?>) bf.getBean("service");
		assertTrue(serviceList.get(0) instanceof DocumentBuilderFactory);
	}

}
