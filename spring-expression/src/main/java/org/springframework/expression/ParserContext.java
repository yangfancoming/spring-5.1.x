

package org.springframework.expression;

/**
 * Input provided to an expression parser that can influence an expression parsing/compilation routine.
 * @since 3.0
 */
public interface ParserContext {

	/**
	 * Whether or not the expression being parsed is a template. A template expression consists of literal text that can be mixed with evaluatable blocks.
	 * Some examples:
	 * <pre class="code">
	 * 	   Some literal text
	 *     Hello #{name.firstName}!
	 *     #{3 + 4}
	 * </pre>
	 * @return true if the expression is a template, false otherwise
	 * 是否是模版表达式。  比如：#{3 + 4}
	 */
	boolean isTemplate();

	/**
	 * For template expressions, returns the prefix that identifies the start of an expression block within a string. For example: "${"
	 * @return the prefix that identifies the start of an expression
	 *  模版的前缀、后缀  子类是可以定制化的~~~
	 */
	String getExpressionPrefix();

	/**
	 * For template expressions, return the prefix that identifies the end of an expression block within a string. For example: "}"
	 * @return the suffix that identifies the end of an expression
	 *  模版的前缀、后缀  子类是可以定制化的~~~
	 */
	String getExpressionSuffix();

	/**
	 * The default ParserContext implementation that enables template expression  parsing mode. The expression prefix is "#{" and the expression suffix is "}".
	 * @see #isTemplate()
	 * 默认提供的实例支持：#{} 的形式   显然我们可以改变它但我们一般并不需要这么去做~
	 */
	ParserContext TEMPLATE_EXPRESSION = new ParserContext() {
		@Override
		public boolean isTemplate() {
			return true;
		}
		@Override
		public String getExpressionPrefix() {
			return "#{";
		}
		@Override
		public String getExpressionSuffix() {
			return "}";
		}
	};
}
