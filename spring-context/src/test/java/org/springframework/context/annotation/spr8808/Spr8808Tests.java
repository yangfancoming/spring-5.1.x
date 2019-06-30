

package org.springframework.context.annotation.spr8808;

import org.junit.Test;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * Tests cornering the bug in which @Configuration classes that @ComponentScan themselves
 * would result in a ConflictingBeanDefinitionException.
 *
 * @author Chris Beams
 * @since 3.1
 */
public class Spr8808Tests {

	/**
	 * This test failed with ConflictingBeanDefinitionException prior to fixes for
	 * SPR-8808.
	 */
	@Test
	public void repro() {
		AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext();
		ctx.register(Config.class);
		ctx.refresh();
	}

}

@Configuration
@ComponentScan(basePackageClasses=Spr8808Tests.class) // scan *this* package
class Config {
}
