

package org.springframework.context.groovy

import org.junit.Test

import org.springframework.beans.factory.NoSuchBeanDefinitionException
import org.springframework.context.support.GenericGroovyApplicationContext

import static groovy.test.GroovyAssert.*

/**
 * @author Jeff Brown
 * @author Sam Brannen
 */
class GroovyApplicationContextDynamicBeanPropertyTests {

	@Test
	void testAccessDynamicBeanProperties() {
		def ctx = new GenericGroovyApplicationContext();
		ctx.reader.loadBeanDefinitions("org/springframework/context/groovy/applicationContext.groovy");
		ctx.refresh()

		def framework = ctx.framework
		assertNotNull 'could not find framework bean', framework
		assertEquals 'Grails', framework
	}

	@Test
	void testAccessingNonExistentBeanViaDynamicProperty() {
		def ctx = new GenericGroovyApplicationContext();
		ctx.reader.loadBeanDefinitions("org/springframework/context/groovy/applicationContext.groovy");
		ctx.refresh()

		def err = shouldFail NoSuchBeanDefinitionException, { ctx.someNonExistentBean }

		assertEquals "No bean named 'someNonExistentBean' available", err.message
	}

}
