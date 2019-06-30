

package org.springframework.test.context.configuration.interfaces;

import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.*;

/**
 * @author Sam Brannen
 * @since 4.3
 */
@RunWith(SpringRunner.class)
public class BootstrapWithInterfaceTests implements BootstrapWithTestInterface {

	@Autowired
	String foo;


	@Test
	public void injectedBean() {
		assertEquals("foo", foo);
	}

}
