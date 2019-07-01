

package org.springframework.expression.spel;

import org.junit.Test;

import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.ast.MethodReference;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import static org.junit.Assert.*;

/**
 * Test for caching in {@link MethodReference} (SPR-10657).
 *
 * @author Oliver Becker
 */
public class CachedMethodExecutorTests {

	private final ExpressionParser parser = new SpelExpressionParser();

	private final StandardEvaluationContext context = new StandardEvaluationContext(new RootObject());


	@Test
	public void testCachedExecutionForParameters() {
		Expression expression = this.parser.parseExpression("echo(#var)");

		assertMethodExecution(expression, 42, "int: 42");
		assertMethodExecution(expression, 42, "int: 42");
		assertMethodExecution(expression, "Deep Thought", "String: Deep Thought");
		assertMethodExecution(expression, 42, "int: 42");
	}

	@Test
	public void testCachedExecutionForTarget() {
		Expression expression = this.parser.parseExpression("#var.echo(42)");

		assertMethodExecution(expression, new RootObject(), "int: 42");
		assertMethodExecution(expression, new RootObject(), "int: 42");
		assertMethodExecution(expression, new BaseObject(), "String: 42");
		assertMethodExecution(expression, new RootObject(), "int: 42");
	}

	private void assertMethodExecution(Expression expression, Object var, String expected) {
		this.context.setVariable("var", var);
		assertEquals(expected, expression.getValue(this.context));
	}


	public static class BaseObject {

		public String echo(String value) {
			return "String: " + value;
		}
	}

	public static class RootObject extends BaseObject {

		public String echo(int value) {
			return "int: " + value;
		}
	}

}
