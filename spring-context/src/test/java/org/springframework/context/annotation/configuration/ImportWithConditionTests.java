

package org.springframework.context.annotation.configuration;

import org.junit.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ConfigurationCondition;
import org.springframework.context.annotation.Import;
import org.springframework.core.type.AnnotatedTypeMetadata;

import static org.junit.Assert.*;


public class ImportWithConditionTests {

	private AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();

	@Test
	public void conditionalThenUnconditional()  {
		this.context.register(ConditionalThenUnconditional.class);
		this.context.refresh();
		assertFalse(this.context.containsBean("beanTwo"));
		assertTrue(this.context.containsBean("beanOne"));
	}

	@Test
	public void unconditionalThenConditional()  {
		this.context.register(UnconditionalThenConditional.class);
		this.context.refresh();
		assertFalse(this.context.containsBean("beanTwo"));
		assertTrue(this.context.containsBean("beanOne"));
	}


	@Configuration
	@Import({ConditionalConfiguration.class, UnconditionalConfiguration.class})
	protected static class ConditionalThenUnconditional {

		@Autowired
		private BeanOne beanOne;
	}


	@Configuration
	@Import({UnconditionalConfiguration.class, ConditionalConfiguration.class})
	protected static class UnconditionalThenConditional {
		@Autowired
		private BeanOne beanOne;
	}


	@Configuration
	@Import(BeanProvidingConfiguration.class)
	protected static class UnconditionalConfiguration {
	}


	@Configuration
	@Conditional(NeverMatchingCondition.class)
	@Import(BeanProvidingConfiguration.class)
	protected static class ConditionalConfiguration {
	}


	@Configuration
	protected static class BeanProvidingConfiguration {
		@Bean
		BeanOne beanOne() {
			return new BeanOne();
		}
	}


	private static final class BeanOne {
	}


	private static final class NeverMatchingCondition implements ConfigurationCondition {

		@Override
		public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
			return false;
		}

		@Override
		public ConfigurationPhase getConfigurationPhase() {
			return ConfigurationPhase.REGISTER_BEAN;
		}
	}

}
