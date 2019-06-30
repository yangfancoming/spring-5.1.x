

package org.springframework.test.context.web;

import org.junit.Test;

import org.springframework.test.context.ContextConfiguration;

import static org.junit.Assert.*;

/**
 * @author Sam Brannen
 * @since 4.1
 * @see BasicXmlWacTests
 */
// Config loaded from BasicGroovyWacTestsContext.groovy
@ContextConfiguration
public class BasicGroovyWacTests extends AbstractBasicWacTests {

	@Test
	public void groovyFooAutowired() {
		assertEquals("Groovy Foo", foo);
	}

}
