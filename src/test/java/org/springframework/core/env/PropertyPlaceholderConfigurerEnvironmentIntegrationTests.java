

package org.springframework.core.env;

import static org.springframework.beans.factory.support.BeanDefinitionBuilder.rootBeanDefinition;

import org.junit.Test;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.context.support.GenericApplicationContext;

@SuppressWarnings("resource")
public class PropertyPlaceholderConfigurerEnvironmentIntegrationTests {
	@Test
	public void test() {
		GenericApplicationContext ctx = new GenericApplicationContext();
		ctx.registerBeanDefinition("ppc",
				rootBeanDefinition(PropertyPlaceholderConfigurer.class)
				.addPropertyValue("searchSystemEnvironment", false)
				.getBeanDefinition());
		ctx.refresh();
		ctx.getBean("ppc");
	}
}
