

package org.springframework.core;

import org.junit.Test;

import java.util.Collections;

import static org.junit.Assert.*;


public class SimpleAliasRegistryTests {

	SimpleAliasRegistry registry = new SimpleAliasRegistry();

	// 与map不同点：  测试 如果真实的beanName和要注册的别名相同，则直接删除，因为真实的名字和别名相同没有意义
	// 源码搜索串： if (alias.equals(name)) {
	@Test
	public void test1() {
		registry.registerAlias("foo", "Goat");
		assertEquals(1, registry.aliasMap.size()); // 成功注册一个别名
		registry.registerAlias("Goat", "Goat");
		assertEquals(0, registry.aliasMap.size()); // 别名与正名相同 通过key为Goat 进行删除
	}

	// 与map相同点： 别名作为key  不会重复
	// 源码搜索串： if (logger.isDebugEnabled()) logger.debug("Overriding alias '"
	@Test
	public void getAliases() {
		// 成功注册一个别名
		registry.registerAlias("foo", "Goat");
		// 覆盖掉 上面的 foo
		registry.registerAlias("bar", "Goat");
		// 验证被覆盖了
		assertEquals(Collections.singletonMap("Goat", "bar"), registry.aliasMap);
	}


	// 测试 存在顺序问题 如果是先 别名与正名相同，再注册别名就可以
	@Test
	public void test2() {
		registry.registerAlias("Goat", "Goat");
		assertEquals(0, registry.aliasMap.size());
		registry.registerAlias("bar", "Goat");
		assertEquals(1, registry.aliasMap.size());
	}

	// 源码搜索串：  if (registeredName.equals(name)) return;
	@Test
	public void test31(){
		registry.registerAlias("person","goat");
		// 如果别名已经在缓存中存在，并且缓存中的别名和beanName(注意:不是别名)相同,则直接返回,没有必要再注册一次
		registry.registerAlias("person","goat");
		assertEquals(Collections.singletonMap("goat", "person"), registry.aliasMap);
	}

	//  与map不同点：  测试 如果注册两个别名和正名顺序颠倒  将抛出异常
	// 源码搜索串：  if (hasAlias(alias, name)) {
	@Test(expected = IllegalStateException.class)
	public void test3() {
		registry.registerAlias("foo", "Goat");
		registry.registerAlias("Goat", "foo");
	}

	// 测试别名 链式传递
	@Test
	public void testAliasChaining() {
		registry.registerAlias("test", "testAlias");
		registry.registerAlias("testAlias", "testAlias2");
		registry.registerAlias("testAlias2", "testAlias3");
		assertEquals(3,registry.getAliases("test").length);
		assertEquals(2,registry.getAliases("testAlias").length);

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
