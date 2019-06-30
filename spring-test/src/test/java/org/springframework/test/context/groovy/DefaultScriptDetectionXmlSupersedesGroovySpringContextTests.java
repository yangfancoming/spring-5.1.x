

package org.springframework.test.context.groovy;

import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.*;

/**
 * Integration test class that verifies proper detection of a default
 * XML config file even though a suitable Groovy script exists.
 *
 * @author Sam Brannen
 * @since 4.1
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
public class DefaultScriptDetectionXmlSupersedesGroovySpringContextTests {

	@Autowired
	protected String foo;


	@Test
	public final void foo() {
		assertEquals("The foo field should have been autowired.", "Foo", this.foo);
	}

}
