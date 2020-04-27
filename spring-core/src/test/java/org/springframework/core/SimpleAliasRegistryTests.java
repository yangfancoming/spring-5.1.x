

package org.springframework.core;

import org.junit.Test;
import java.util.Arrays;
import java.util.Collections;
import static org.junit.Assert.*;


public class SimpleAliasRegistryTests {

	SimpleAliasRegistry registry = new SimpleAliasRegistry();

	// 与map不同点：  测试 如果证明和别名相同，则直接删除，因为真实的名字和别名相同没有意义
	// 源码搜索串： if (alias.equals(name)) {
	@Test
	public void test1() {
		registry.registerAlias("李彦伯", "老K");
		assertEquals(1, registry.aliasMap.size()); // 成功注册一个别名
		registry.registerAlias("老K", "老K");
		assertEquals(0, registry.aliasMap.size()); // 别名与正名相同 通过key为Goat 进行删除
	}

	// 与map相同点： 别名作为key  不会重复
	// 源码搜索串： if (logger.isDebugEnabled()) logger.debug("Overriding alias '"
	@Test
	public void getAliases() {
		registry.registerAlias("李彦伯", "老K"); // 成功注册一个别名
		registry.registerAlias("马文明", "老K"); // 马文明 覆盖掉 李彦伯
		assertEquals(Collections.singletonMap("老K", "马文明"), registry.aliasMap);// 验证被覆盖了
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
		registry.registerAlias("李彦伯","老K");
		// 如果别名已经在缓存中存在，并且缓存中的正名和传入的正名相同,则直接返回,没有必要再注册一次
		registry.registerAlias("李彦伯","老K");
		assertEquals(Collections.singletonMap("老K", "李彦伯"), registry.aliasMap);
	}

	//  与map不同点：  测试 如果注册两个别名和正名顺序颠倒  将抛出异常
	// 源码搜索串：  checkForAliasCircle(name, alias);
	@Test(expected = IllegalStateException.class)
	public void test3() {
		registry.registerAlias("李彦伯", "老K");
		registry.registerAlias("老K", "李彦伯");
	}

	// 测试别名 链式传递
	@Test
	public void testAliasChaining() {
		registry.registerAlias("李彦伯", "李亮亮");
		registry.registerAlias("李亮亮", "老K");
		registry.registerAlias("老K", "JQK");
		assertEquals(Arrays.asList("李亮亮","老K","JQK").toArray(),registry.getAliases("李彦伯"));
		assertEquals(Arrays.asList("老K","JQK").toArray(),registry.getAliases("李亮亮"));
		// hasAlias 测试
		assertTrue(registry.hasAlias("李彦伯", "李亮亮"));
		assertTrue(registry.hasAlias("李彦伯", "老K"));
		assertTrue(registry.hasAlias("李彦伯", "JQK"));
		// canonicalName 测试
		assertSame("李彦伯", registry.canonicalName("李亮亮"));
		assertSame("李彦伯", registry.canonicalName("老K"));
		assertSame("李彦伯", registry.canonicalName("JQK"));
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
