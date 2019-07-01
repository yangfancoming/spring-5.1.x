

package org.springframework.expression.spel;

import org.junit.Test;

import org.springframework.expression.EvaluationException;
import org.springframework.expression.Operation;
import org.springframework.expression.OperatorOverloader;
import org.springframework.expression.spel.standard.SpelExpression;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import static org.junit.Assert.*;

/**
 * Test providing operator support
 *
 * @author Andy Clement
 */
public class OperatorOverloaderTests extends AbstractExpressionTests {

	@Test
	public void testSimpleOperations() throws Exception {
		// no built in support for this:
		evaluateAndCheckError("'abc'-true",SpelMessage.OPERATOR_NOT_SUPPORTED_BETWEEN_TYPES);

		StandardEvaluationContext eContext = TestScenarioCreator.getTestEvaluationContext();
		eContext.setOperatorOverloader(new StringAndBooleanAddition());

		SpelExpression expr = (SpelExpression)parser.parseExpression("'abc'+true");
		assertEquals("abctrue",expr.getValue(eContext));

		expr = (SpelExpression)parser.parseExpression("'abc'-true");
		assertEquals("abc",expr.getValue(eContext));

		expr = (SpelExpression)parser.parseExpression("'abc'+null");
		assertEquals("abcnull",expr.getValue(eContext));
	}


	static class StringAndBooleanAddition implements OperatorOverloader {

		@Override
		public Object operate(Operation operation, Object leftOperand, Object rightOperand) throws EvaluationException {
			if (operation==Operation.ADD) {
				return ((String)leftOperand)+((Boolean)rightOperand).toString();
			}
			else {
				return leftOperand;
			}
		}

		@Override
		public boolean overridesOperation(Operation operation, Object leftOperand, Object rightOperand) throws EvaluationException {
			if (leftOperand instanceof String && rightOperand instanceof Boolean) {
				return true;
			}
			return false;

		}
	}

}
