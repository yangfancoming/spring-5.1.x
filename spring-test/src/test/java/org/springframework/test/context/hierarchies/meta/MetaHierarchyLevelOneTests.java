

package org.springframework.test.context.hierarchies.meta;

import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.*;

/**
 * @author Sam Brannen
 * @since 4.0.3
 */
@RunWith(SpringJUnit4ClassRunner.class)
@MetaMetaContextHierarchyConfig
public class MetaHierarchyLevelOneTests {

	@Autowired
	private String foo;


	@Test
	public void foo() {
		assertEquals("Dev Foo", foo);
	}

}
