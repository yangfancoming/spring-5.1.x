

package org.springframework.scripting.groovy;

import java.util.HashMap;
import java.util.Map;

import org.codehaus.groovy.control.customizers.ImportCustomizer;
import org.junit.Test;

import org.springframework.core.io.ClassPathResource;
import org.springframework.scripting.ScriptEvaluator;
import org.springframework.scripting.support.ResourceScriptSource;
import org.springframework.scripting.support.StandardScriptEvaluator;
import org.springframework.scripting.support.StaticScriptSource;

import static org.junit.Assert.*;

/**
 * @author Juergen Hoeller
 */
public class GroovyScriptEvaluatorTests {

	@Test
	public void testGroovyScriptFromString() {
		ScriptEvaluator evaluator = new GroovyScriptEvaluator();
		Object result = evaluator.evaluate(new StaticScriptSource("return 3 * 2"));
		assertEquals(6, result);
	}

	@Test
	public void testGroovyScriptFromFile() {
		ScriptEvaluator evaluator = new GroovyScriptEvaluator();
		Object result = evaluator.evaluate(new ResourceScriptSource(new ClassPathResource("simple.groovy", getClass())));
		assertEquals(6, result);
	}

	@Test
	public void testGroovyScriptWithArguments() {
		ScriptEvaluator evaluator = new GroovyScriptEvaluator();
		Map<String, Object> arguments = new HashMap<>();
		arguments.put("a", 3);
		arguments.put("b", 2);
		Object result = evaluator.evaluate(new StaticScriptSource("return a * b"), arguments);
		assertEquals(6, result);
	}

	@Test
	public void testGroovyScriptWithCompilerConfiguration() {
		GroovyScriptEvaluator evaluator = new GroovyScriptEvaluator();
		MyBytecodeProcessor processor = new MyBytecodeProcessor();
		evaluator.getCompilerConfiguration().setBytecodePostprocessor(processor);
		Object result = evaluator.evaluate(new StaticScriptSource("return 3 * 2"));
		assertEquals(6, result);
		assertTrue(processor.processed.contains("Script1"));
	}

	@Test
	public void testGroovyScriptWithImportCustomizer() {
		GroovyScriptEvaluator evaluator = new GroovyScriptEvaluator();
		ImportCustomizer importCustomizer = new ImportCustomizer();
		importCustomizer.addStarImports("org.springframework.util");
		evaluator.setCompilationCustomizers(importCustomizer);
		Object result = evaluator.evaluate(new StaticScriptSource("return ResourceUtils.CLASSPATH_URL_PREFIX"));
		assertEquals("classpath:", result);
	}

	@Test
	public void testGroovyScriptFromStringUsingJsr223() {
		StandardScriptEvaluator evaluator = new StandardScriptEvaluator();
		evaluator.setLanguage("Groovy");
		Object result = evaluator.evaluate(new StaticScriptSource("return 3 * 2"));
		assertEquals(6, result);
	}

	@Test
	public void testGroovyScriptFromFileUsingJsr223() {
		ScriptEvaluator evaluator = new StandardScriptEvaluator();
		Object result = evaluator.evaluate(new ResourceScriptSource(new ClassPathResource("simple.groovy", getClass())));
		assertEquals(6, result);
	}

	@Test
	public void testGroovyScriptWithArgumentsUsingJsr223() {
		StandardScriptEvaluator evaluator = new StandardScriptEvaluator();
		evaluator.setLanguage("Groovy");
		Map<String, Object> arguments = new HashMap<>();
		arguments.put("a", 3);
		arguments.put("b", 2);
		Object result = evaluator.evaluate(new StaticScriptSource("return a * b"), arguments);
		assertEquals(6, result);
	}

}
