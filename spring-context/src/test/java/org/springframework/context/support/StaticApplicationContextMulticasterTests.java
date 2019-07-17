

package org.springframework.context.support;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.junit.Test;

import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.factory.support.PropertiesBeanDefinitionReader;
import org.springframework.context.ACATester;
import org.springframework.context.AbstractApplicationContextTests;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.BeanThatListens;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.event.SimpleApplicationEventMulticaster;
import org.springframework.core.ResolvableType;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.EncodedResource;
import org.springframework.lang.Nullable;
import org.springframework.tests.sample.beans.TestBean;

import static org.junit.Assert.*;

/**
 * Tests for static application context with custom application event multicaster.
 *

 */
public class StaticApplicationContextMulticasterTests extends AbstractApplicationContextTests {

	protected StaticApplicationContext sac;

	@Override
	protected ConfigurableApplicationContext createContext() throws Exception {
		StaticApplicationContext parent = new StaticApplicationContext();
		Map<String, String> m = new HashMap<>();
		m.put("name", "Roderick");
		parent.registerPrototype("rod", TestBean.class, new MutablePropertyValues(m));
		m.put("name", "Albert");
		parent.registerPrototype("father", TestBean.class, new MutablePropertyValues(m));
		parent.registerSingleton(StaticApplicationContext.APPLICATION_EVENT_MULTICASTER_BEAN_NAME,
				TestApplicationEventMulticaster.class, null);
		parent.refresh();
		parent.addApplicationListener(parentListener) ;

		parent.getStaticMessageSource().addMessage("code1", Locale.getDefault(), "message1");

		this.sac = new StaticApplicationContext(parent);
		sac.registerSingleton("beanThatListens", BeanThatListens.class, new MutablePropertyValues());
		sac.registerSingleton("aca", ACATester.class, new MutablePropertyValues());
		sac.registerPrototype("aca-prototype", ACATester.class, new MutablePropertyValues());
		PropertiesBeanDefinitionReader reader = new PropertiesBeanDefinitionReader(sac.getDefaultListableBeanFactory());
		Resource resource = new ClassPathResource("testBeans.properties", getClass());
		reader.loadBeanDefinitions(new EncodedResource(resource, "ISO-8859-1"));
		sac.refresh();
		sac.addApplicationListener(listener);

		sac.getStaticMessageSource().addMessage("code2", Locale.getDefault(), "message2");

		return sac;
	}

	@Test
	@Override
	public void count() {
		assertCount(15);
	}

	@Test
	@Override
	public void events() throws Exception {
		TestApplicationEventMulticaster.counter = 0;
		super.events();
		assertEquals(1, TestApplicationEventMulticaster.counter);
	}


	public static class TestApplicationEventMulticaster extends SimpleApplicationEventMulticaster {

		private static int counter = 0;

		@Override
		public void multicastEvent(ApplicationEvent event, @Nullable ResolvableType eventType) {
			super.multicastEvent(event, eventType);
			counter++;
		}
	}

}
