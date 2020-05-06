
package org.springframework.context.annotation;

import org.junit.Test;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.parsing.BeanDefinitionParsingException;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;

import static org.junit.Assert.*;
import static org.springframework.beans.factory.support.BeanDefinitionBuilder.*;

/**
 * Unit tests covering cases where a user defines an invalid Configuration
 * class, e.g.: forgets to annotate with {@link Configuration} or declares
 * a Configuration class as final.

 */
public class InvalidConfigurationClassDefinitionTests {

	@Test
	public void configurationClassesMayNotBeFinal() {
		@Configuration
		final class Config { }

		BeanDefinition configBeanDef = rootBeanDefinition(Config.class).getBeanDefinition();
		DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();
		beanFactory.registerBeanDefinition("config", configBeanDef);

		try {
			ConfigurationClassPostProcessor pp = new ConfigurationClassPostProcessor();
			pp.postProcessBeanFactory(beanFactory);
			fail("expected exception");
		}
		catch (BeanDefinitionParsingException ex) {
			assertTrue(ex.getMessage(), ex.getMessage().contains("Remove the final modifier"));
		}
	}

}
