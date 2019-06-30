

package org.springframework.context.groovy;

import org.junit.Test;

import org.springframework.beans.factory.parsing.BeanDefinitionParsingException;
import org.springframework.context.support.GenericGroovyApplicationContext;

import static org.junit.Assert.*;

/**
 * @author Jeff Brown
 * @author Juergen Hoeller
 */
public class GroovyApplicationContextTests {

	@Test
	public void testLoadingConfigFile() {
		GenericGroovyApplicationContext ctx = new GenericGroovyApplicationContext(
				"org/springframework/context/groovy/applicationContext.groovy");

		Object framework = ctx.getBean("framework");
		assertNotNull("could not find framework bean", framework);
		assertEquals("Grails", framework);
	}

	@Test
	public void testLoadingMultipleConfigFiles() {
		GenericGroovyApplicationContext ctx = new GenericGroovyApplicationContext(
				"org/springframework/context/groovy/applicationContext2.groovy",
				"org/springframework/context/groovy/applicationContext.groovy");

		Object framework = ctx.getBean("framework");
		assertNotNull("could not find framework bean", framework);
		assertEquals("Grails", framework);

		Object company = ctx.getBean("company");
		assertNotNull("could not find company bean", company);
		assertEquals("SpringSource", company);
	}

	@Test
	public void testLoadingMultipleConfigFilesWithRelativeClass() {
		GenericGroovyApplicationContext ctx = new GenericGroovyApplicationContext();
		ctx.load(GroovyApplicationContextTests.class, "applicationContext2.groovy", "applicationContext.groovy");
		ctx.refresh();

		Object framework = ctx.getBean("framework");
		assertNotNull("could not find framework bean", framework);
		assertEquals("Grails", framework);

		Object company = ctx.getBean("company");
		assertNotNull("could not find company bean", company);
		assertEquals("SpringSource", company);
	}

	@Test(expected = BeanDefinitionParsingException.class)
	public void testConfigFileParsingError() {
		new GenericGroovyApplicationContext("org/springframework/context/groovy/applicationContext-error.groovy");
	}

}
