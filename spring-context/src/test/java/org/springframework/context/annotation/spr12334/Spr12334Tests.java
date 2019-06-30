

package org.springframework.context.annotation.spr12334;

import org.junit.Test;

import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;

/**
 * @author Juergen Hoeller
 * @author Alex Pogrebnyak
 */
public class Spr12334Tests {

	@Test
	public void shouldNotScanTwice() {
		TestImport.scanned = false;

		AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
		context.scan(TestImport.class.getPackage().getName());
		context.refresh();
		context.getBean(TestConfiguration.class);
	}


	@Import(TestImport.class)
	public @interface AnotherImport {
	}


	@Configuration
	@AnotherImport
	public static class TestConfiguration {
	}


	public static class TestImport implements ImportBeanDefinitionRegistrar {

		private static boolean scanned = false;

		@Override
		public void registerBeanDefinitions(AnnotationMetadata metadata, BeanDefinitionRegistry registry)  {
			if (scanned) {
				throw new IllegalStateException("Already scanned");
			}
			scanned = true;
		}
	}

}
