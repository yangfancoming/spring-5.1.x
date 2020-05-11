

package org.springframework.expression;

/**
 * Parses expression strings into compiled expressions that can be evaluated.
 * Supports parsing templates as well as standard expression strings.
 * @since 3.0
 * 他俩都是把字符串解析成一个Expression对象~~~~  备注expressionString都是可以被repeated evaluation的
 */
public interface ExpressionParser {

	/**
	 * Parse the expression string and return an Expression object you can use for repeated evaluation.
	 * Some examples:
	 * <pre class="code">
	 *     3 + 4
	 *     name.firstName
	 * </pre>
	 * @param expressionString the raw expression string to parse
	 * @return an evaluator for the parsed expression
	 * @throws ParseException an exception occurred during parsing
	 */
	Expression parseExpression(String expressionString) throws ParseException;

	/**
	 * Parse the expression string and return an Expression object you can use for repeated evaluation.
	 * Some examples:
	 * <pre class="code">
	 *     3 + 4
	 *     name.firstName
	 * </pre>
	 * @param expressionString the raw expression string to parse
	 * @param context a context for influencing this expression parsing routine (optional)
	 * 此处，ParserContext：提供给表达式分析器的输入，它可能影响表达式分析/编译例程。它会对我们解析表达式字符串的行为影响
	 * @return an evaluator for the parsed expression
	 * @throws ParseException an exception occurred during parsing
	 */
	Expression parseExpression(String expressionString, ParserContext context) throws ParseException;
}
