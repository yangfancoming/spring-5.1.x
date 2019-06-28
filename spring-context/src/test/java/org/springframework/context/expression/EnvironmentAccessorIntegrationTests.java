

package org.springframework.context.expression;

import org.junit.Test;

import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.mock.env.MockPropertySource;
import org.springframework.tests.sample.beans.TestBean;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;
import static org.springframework.beans.factory.support.BeanDefinitionBuilder.*;

/**
 * Integration tests for {@link EnvironmentAccessor}, which is registered with
 * SpEL for all {@link AbstractApplicationContext} implementations via
 * {@link StandardBeanExpressionResolver}.
 *
 * @author Chris Beams
 */
public class EnvironmentAccessorIntegrationTests {

	@Test
	@SuppressWarnings("all")
	public void braceAccess() {
		DefaultListableBeanFactory bf = new DefaultListableBeanFactory();
		bf.registerBeanDefinition("testBean",
				genericBeanDefinition(TestBean.class)
					.addPropertyValue("name", "#{environment['my.name']}")
					.getBeanDefinition());

		GenericApplicationContext ctx = new GenericApplicationContext(bf);
		ctx.getEnvironment().getPropertySources().addFirst(new MockPropertySource().withProperty("my.name", "myBean"));
		ctx.refresh();

		assertThat(ctx.getBean(TestBean.class).getName(), equalTo("myBean"));
	}

}
