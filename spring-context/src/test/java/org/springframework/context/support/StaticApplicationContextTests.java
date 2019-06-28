

package org.springframework.context.support;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.junit.Test;

import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.factory.support.PropertiesBeanDefinitionReader;
import org.springframework.context.ACATester;
import org.springframework.context.AbstractApplicationContextTests;
import org.springframework.context.BeanThatListens;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.io.ClassPathResource;
import org.springframework.tests.sample.beans.TestBean;

/**
 * Tests for static application context.
 *
 * @author Rod Johnson
 */
public class StaticApplicationContextTests extends AbstractApplicationContextTests {

	protected StaticApplicationContext sac;

	@Override
	protected ConfigurableApplicationContext createContext() throws Exception {
		StaticApplicationContext parent = new StaticApplicationContext();
		Map<String, String> m = new HashMap<>();
		m.put("name", "Roderick");
		parent.registerPrototype("rod", TestBean.class, new MutablePropertyValues(m));
		m.put("name", "Albert");
		parent.registerPrototype("father", TestBean.class, new MutablePropertyValues(m));
		parent.refresh();
		parent.addApplicationListener(parentListener) ;

		parent.getStaticMessageSource().addMessage("code1", Locale.getDefault(), "message1");

		this.sac = new StaticApplicationContext(parent);
		sac.registerSingleton("beanThatListens", BeanThatListens.class, new MutablePropertyValues());
		sac.registerSingleton("aca", ACATester.class, new MutablePropertyValues());
		sac.registerPrototype("aca-prototype", ACATester.class, new MutablePropertyValues());
		PropertiesBeanDefinitionReader reader = new PropertiesBeanDefinitionReader(sac.getDefaultListableBeanFactory());
		reader.loadBeanDefinitions(new ClassPathResource("testBeans.properties", getClass()));
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

}
