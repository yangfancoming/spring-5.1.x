

package org.springframework.context.annotation;

import org.junit.Test;

import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.support.RootBeanDefinition;

/**
 * @author Andy Wilkinson
 */
public class ImportVersusDirectRegistrationTests {

	@Test(expected = NoSuchBeanDefinitionException.class)
	public void thingIsNotAvailableWhenOuterConfigurationIsRegisteredDirectly() {
		try (AnnotationConfigApplicationContext directRegistration = new AnnotationConfigApplicationContext()) {
			directRegistration.register(AccidentalLiteConfiguration.class);
			directRegistration.refresh();
			directRegistration.getBean(Thing.class);
		}
	}

	@Test(expected = NoSuchBeanDefinitionException.class)
	public void thingIsNotAvailableWhenOuterConfigurationIsRegisteredWithClassName() {
		try (AnnotationConfigApplicationContext directRegistration = new AnnotationConfigApplicationContext()) {
			directRegistration.registerBeanDefinition("config",
					new RootBeanDefinition(AccidentalLiteConfiguration.class.getName()));
			directRegistration.refresh();
			directRegistration.getBean(Thing.class);
		}
	}

	@Test(expected = NoSuchBeanDefinitionException.class)
	public void thingIsNotAvailableWhenOuterConfigurationIsImported() {
		try (AnnotationConfigApplicationContext viaImport = new AnnotationConfigApplicationContext()) {
			viaImport.register(Importer.class);
			viaImport.refresh();
			viaImport.getBean(Thing.class);
		}
	}

}


@Import(AccidentalLiteConfiguration.class)
class Importer {
}


class AccidentalLiteConfiguration {

	@Configuration
	class InnerConfiguration {

		@Bean
		public Thing thing() {
			return new Thing();
		}
	}
}


class Thing {
}
