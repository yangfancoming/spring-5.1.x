

package org.springframework.expression.spel.standard;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;

import org.junit.Test;

import org.springframework.expression.Expression;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import static org.junit.Assert.*;

/**
 * @author Mark Fisher
 */
public class PropertiesConversionSpelTests {

	private static final SpelExpressionParser parser = new SpelExpressionParser();

	@Test
	public void props() {
		Properties props = new Properties();
		props.setProperty("x", "1");
		props.setProperty("y", "2");
		props.setProperty("z", "3");
		Expression expression = parser.parseExpression("foo(#props)");
		StandardEvaluationContext context = new StandardEvaluationContext();
		context.setVariable("props", props);
		String result = expression.getValue(context, new TestBean(), String.class);
		assertEquals("123", result);
	}

	@Test
	public void mapWithAllStringValues() {
		Map<String, Object> map = new HashMap<>();
		map.put("x", "1");
		map.put("y", "2");
		map.put("z", "3");
		Expression expression = parser.parseExpression("foo(#props)");
		StandardEvaluationContext context = new StandardEvaluationContext();
		context.setVariable("props", map);
		String result = expression.getValue(context, new TestBean(), String.class);
		assertEquals("123", result);
	}

	@Test
	public void mapWithNonStringValue() {
		Map<String, Object> map = new HashMap<>();
		map.put("x", "1");
		map.put("y", 2);
		map.put("z", "3");
		map.put("a", new UUID(1, 1));
		Expression expression = parser.parseExpression("foo(#props)");
		StandardEvaluationContext context = new StandardEvaluationContext();
		context.setVariable("props", map);
		String result = expression.getValue(context, new TestBean(), String.class);
		assertEquals("1null3", result);
	}

	@Test
	public void customMapWithNonStringValue() {
		CustomMap map = new CustomMap();
		map.put("x", "1");
		map.put("y", 2);
		map.put("z", "3");
		Expression expression = parser.parseExpression("foo(#props)");
		StandardEvaluationContext context = new StandardEvaluationContext();
		context.setVariable("props", map);
		String result = expression.getValue(context, new TestBean(), String.class);
		assertEquals("1null3", result);
	}


	private static class TestBean {

		@SuppressWarnings("unused")
		public String foo(Properties props) {
			return props.getProperty("x") + props.getProperty("y") + props.getProperty("z");
		}
	}


	@SuppressWarnings("serial")
	private static class CustomMap extends HashMap<String, Object> {
	}

}
