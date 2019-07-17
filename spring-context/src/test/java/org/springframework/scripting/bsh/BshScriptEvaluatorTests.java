

package org.springframework.scripting.bsh;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import org.springframework.core.io.ClassPathResource;
import org.springframework.scripting.ScriptEvaluator;
import org.springframework.scripting.support.ResourceScriptSource;
import org.springframework.scripting.support.StaticScriptSource;

import static org.junit.Assert.*;


public class BshScriptEvaluatorTests {

	@Test
	public void testBshScriptFromString() {
		ScriptEvaluator evaluator = new BshScriptEvaluator();
		Object result = evaluator.evaluate(new StaticScriptSource("return 3 * 2;"));
		assertEquals(6, result);
	}

	@Test
	public void testBshScriptFromFile() {
		ScriptEvaluator evaluator = new BshScriptEvaluator();
		Object result = evaluator.evaluate(new ResourceScriptSource(new ClassPathResource("simple.bsh", getClass())));
		assertEquals(6, result);
	}

	@Test
	public void testGroovyScriptWithArguments() {
		ScriptEvaluator evaluator = new BshScriptEvaluator();
		Map<String, Object> arguments = new HashMap<>();
		arguments.put("a", 3);
		arguments.put("b", 2);
		Object result = evaluator.evaluate(new StaticScriptSource("return a * b;"), arguments);
		assertEquals(6, result);
	}

}
