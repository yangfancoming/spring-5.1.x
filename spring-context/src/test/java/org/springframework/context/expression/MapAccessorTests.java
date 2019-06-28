

package org.springframework.context.expression;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.springframework.expression.Expression;
import org.springframework.expression.spel.standard.SpelCompiler;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import static org.junit.Assert.*;

/**
 * Unit tests for compilation of {@link MapAccessor}.
 *
 * @author Andy Clement
 */
public class MapAccessorTests {

	@Test
	public void mapAccessorCompilable() {
		Map<String, Object> testMap = getSimpleTestMap();
		StandardEvaluationContext sec = new StandardEvaluationContext();
		sec.addPropertyAccessor(new MapAccessor());
		SpelExpressionParser sep = new SpelExpressionParser();

		// basic
		Expression ex = sep.parseExpression("foo");
		assertEquals("bar",ex.getValue(sec,testMap));
		assertTrue(SpelCompiler.compile(ex));
		assertEquals("bar",ex.getValue(sec,testMap));

		// compound expression
		ex = sep.parseExpression("foo.toUpperCase()");
		assertEquals("BAR",ex.getValue(sec,testMap));
		assertTrue(SpelCompiler.compile(ex));
		assertEquals("BAR",ex.getValue(sec,testMap));

		// nested map
		Map<String,Map<String,Object>> nestedMap = getNestedTestMap();
		ex = sep.parseExpression("aaa.foo.toUpperCase()");
		assertEquals("BAR",ex.getValue(sec,nestedMap));
		assertTrue(SpelCompiler.compile(ex));
		assertEquals("BAR",ex.getValue(sec,nestedMap));

		// avoiding inserting checkcast because first part of expression returns a Map
		ex = sep.parseExpression("getMap().foo");
		MapGetter mapGetter = new MapGetter();
		assertEquals("bar",ex.getValue(sec,mapGetter));
		assertTrue(SpelCompiler.compile(ex));
		assertEquals("bar",ex.getValue(sec,mapGetter));
	}

	public static class MapGetter {
		Map<String,Object> map = new HashMap<>();

		public MapGetter() {
			map.put("foo", "bar");
		}

		@SuppressWarnings("rawtypes")
		public Map getMap() {
			return map;
		}
	}

	public Map<String,Object> getSimpleTestMap() {
		Map<String,Object> map = new HashMap<>();
		map.put("foo","bar");
		return map;
	}

	public Map<String,Map<String,Object>> getNestedTestMap() {
		Map<String,Object> map = new HashMap<>();
		map.put("foo","bar");
		Map<String,Map<String,Object>> map2 = new HashMap<>();
		map2.put("aaa", map);
		return map2;
	}

}
