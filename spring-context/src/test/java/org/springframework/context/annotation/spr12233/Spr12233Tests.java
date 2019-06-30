

package org.springframework.context.annotation.spr12233;

import org.junit.Test;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ConfigurationCondition;
import org.springframework.context.annotation.Import;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.type.AnnotatedTypeMetadata;


/**
 * Tests cornering the regression reported in SPR-12233.
 *
 * @author Phillip Webb
 */
public class Spr12233Tests {

	@Test
	public void spr12233() throws Exception {
		AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext();
		ctx.register(PropertySourcesPlaceholderConfigurer.class);
		ctx.register(ImportConfiguration.class);
		ctx.refresh();
		ctx.close();
	}

	static class NeverConfigurationCondition implements ConfigurationCondition {
		@Override
		public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
			return false;
		}

		@Override
		public ConfigurationPhase getConfigurationPhase() {
			return ConfigurationPhase.REGISTER_BEAN;
		}
	}

	@Import(ComponentScanningConfiguration.class)
	static class ImportConfiguration {

	}

	@Configuration
	@ComponentScan
	static class ComponentScanningConfiguration {

	}


	@Configuration
	@Conditional(NeverConfigurationCondition.class)
	static class ConditionWithPropertyValueInjection {

		@Value("${idontexist}")
		private String property;
	}
}
