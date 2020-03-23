

package org.springframework.core;

import org.junit.Test;

import static org.junit.Assert.*;


public class SimpleAliasRegistryTests {

	SimpleAliasRegistry registry = new SimpleAliasRegistry();

	@Test
	public void testAliasChaining() {
		registry.registerAlias("test", "testAlias");
		registry.registerAlias("testAlias", "testAlias2");
		registry.registerAlias("testAlias2", "testAlias3");

		assertTrue(registry.hasAlias("test", "testAlias"));
		assertTrue(registry.hasAlias("test", "testAlias2"));
		assertTrue(registry.hasAlias("test", "testAlias3"));

		assertSame("test", registry.canonicalName("testAlias"));
		assertSame("test", registry.canonicalName("testAlias2"));
		assertSame("test", registry.canonicalName("testAlias3"));
	}

	@Test  // SPR-17191
	public void testAliasChainingWithMultipleAliases() {
		registry.registerAlias("name", "alias_a");
		registry.registerAlias("name", "alias_b");
		assertTrue(registry.hasAlias("name", "alias_a"));
		assertTrue(registry.hasAlias("name", "alias_b"));

		registry.registerAlias("real_name", "name");
		assertTrue(registry.hasAlias("real_name", "name"));
		assertTrue(registry.hasAlias("real_name", "alias_a"));
		assertTrue(registry.hasAlias("real_name", "alias_b"));

		registry.registerAlias("name", "alias_c");
		assertTrue(registry.hasAlias("real_name", "name"));
		assertTrue(registry.hasAlias("real_name", "alias_a"));
		assertTrue(registry.hasAlias("real_name", "alias_b"));
		assertTrue(registry.hasAlias("real_name", "alias_c"));
	}

}
