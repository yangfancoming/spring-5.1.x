

package org.springframework.expression.spel.standard;

import org.springframework.expression.ParseException;
import org.springframework.expression.ParserContext;
import org.springframework.expression.common.TemplateAwareExpressionParser;
import org.springframework.expression.spel.SpelParserConfiguration;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

/**
 * SpEL parser. Instances are reusable and thread-safe.
 * @since 3.0
 */
public class SpelExpressionParser extends TemplateAwareExpressionParser {

	private final SpelParserConfiguration configuration;

	/**
	 * Create a parser with default settings.
	 */
	public SpelExpressionParser() {
		this.configuration = new SpelParserConfiguration();
	}

	/**
	 * Create a parser with the specified configuration.
	 * @param configuration custom configuration options
	 */
	public SpelExpressionParser(SpelParserConfiguration configuration) {
		Assert.notNull(configuration, "SpelParserConfiguration must not be null");
		this.configuration = configuration;
	}


	public SpelExpression parseRaw(String expressionString) throws ParseException {
		return doParseExpression(expressionString, null);
	}

	@Override
	protected SpelExpression doParseExpression(String expressionString, @Nullable ParserContext context) throws ParseException {
		return new InternalSpelExpressionParser(this.configuration).doParseExpression(expressionString, context);
	}

}
