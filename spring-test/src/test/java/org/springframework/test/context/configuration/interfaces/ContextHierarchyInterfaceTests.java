

package org.springframework.test.context.configuration.interfaces;

import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.*;

/**
 * @author Sam Brannen
 * @since 4.3
 */
@RunWith(SpringRunner.class)
public class ContextHierarchyInterfaceTests implements ContextHierarchyTestInterface {

	@Autowired
	String foo;

	@Autowired
	String bar;

	@Autowired
	String baz;

	@Autowired
	ApplicationContext context;


	@Test
	public void loadContextHierarchy() {
		assertNotNull("child ApplicationContext", context);
		assertNotNull("parent ApplicationContext", context.getParent());
		assertNull("grandparent ApplicationContext", context.getParent().getParent());
		assertEquals("foo", foo);
		assertEquals("bar", bar);
		assertEquals("baz-child", baz);
	}

}
